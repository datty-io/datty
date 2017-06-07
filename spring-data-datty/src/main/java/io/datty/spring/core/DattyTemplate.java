/*
 * Copyright (C) 2016 Datty.io Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.datty.spring.core;

import java.util.Optional;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.MappingException;
import org.springframework.util.Assert;

import io.datty.api.Datty;
import io.datty.api.DattyOperation;
import io.datty.api.DattyResult;
import io.datty.api.DattyRow;
import io.datty.api.UpdatePolicy;
import io.datty.api.operation.SizeOperation;
import io.datty.api.operation.ClearOperation;
import io.datty.api.operation.ExistsOperation;
import io.datty.api.operation.GetOperation;
import io.datty.api.operation.PutOperation;
import io.datty.api.operation.RemoveOperation;
import io.datty.api.operation.ScanOperation;
import io.datty.api.result.ExistsResult;
import io.datty.api.result.GetResult;
import io.datty.api.result.PutResult;
import io.datty.api.result.QueryResult;
import io.datty.spring.convert.DattyConverter;
import io.datty.spring.mapping.DattyPersistentEntity;
import io.datty.spring.mapping.DattyPersistentProperty;
import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

/**
 * DattyTemplate
 * 
 * @author Alex Shvid
 *
 */

public class DattyTemplate implements DattyOperations {

	private final Datty datty;
	private final DattyConverter converter;
	private final MappingContext<? extends DattyPersistentEntity<?>, DattyPersistentProperty> mappingContext;

	public DattyTemplate(Datty datty, DattyConverter converter) {
		Assert.notNull(datty, "datty is null");
		Assert.notNull(converter, "converter is null");
		this.datty = datty;
		this.converter = converter;
		this.mappingContext = converter.getMappingContext();
	}

	@Override
	public <T> Optional<DattyId> getId(T entity) {
		Assert.notNull(entity, "entity is null");

		@SuppressWarnings("unchecked")
		DattyPersistentEntity<?> entityMetadata = getPersistentEntity((Class<T>) entity.getClass());

		return getId(entityMetadata, entity);
	}

	@Override
	public <S extends T, T> Single<S> save(Class<T> entityClass, final S entity) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(entity, "entity is null");
		
		DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);

		return datty.execute(toPutOperation(entityMetadata, entity))
		
		.map(new Func1<PutResult, S>() {

			@Override
			public S call(PutResult t) {
				return entity;
			}
			
		});
		
	}

	protected PutOperation toPutOperation(DattyPersistentEntity<?> entityMetadata, Object entity) {
		
		DattyId id = getId(entityMetadata, entity).orElse(null);
		
		if (id == null) {
			throw new MappingException("id property not found for " + entityMetadata);
		}
		
		DattyRow row = new DattyRow();
		converter.write(entity, row);
		
		return new PutOperation(entityMetadata.getSetName())
		.setSuperKey(id.getSuperKey())
		.setMajorKey(id.getMajorKey())
		.setTtlSeconds(entityMetadata.getTtlSeconds())
		.setRow(row)
		.setUpdatePolicy(UpdatePolicy.MERGE)
		.setUpstreamContext(entity)
		.withTimeoutMillis(entityMetadata.getTimeoutMillis());
		
	}
	
	@Override
	public <S extends T, T> Observable<S> save(Class<T> entityClass, Iterable<S> entities) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(entities, "entities is null");
		return save(entityClass, Observable.from(entities));
	}

	@Override
	public <S extends T, T> Observable<S> save(Class<T> entityClass, Observable<S> entityStream) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(entityStream, "entityStream is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		Observable<DattyOperation> operations = entityStream.map(new Func1<S, DattyOperation>() {

			@Override
			public DattyOperation call(S entity) {
				return toPutOperation(entityMetadata, entity);
			}
			
		});
		
		return datty.executeSequence(operations)
		
		.map(new Func1<DattyResult, S>() {

			@SuppressWarnings("unchecked")
			@Override
			public S call(DattyResult res) {
				
				PutResult putResult = (PutResult) res;
				return (S) putResult.getOperation().getUpstreamContext();
			}

			
		});
		
	}

	
	protected GetOperation toGetOperation(DattyPersistentEntity<?> entityMetadata, DattyId id) {
		
		return new GetOperation(entityMetadata.getSetName())
		.setSuperKey(id.getSuperKey())
		.setMajorKey(id.getMajorKey())
		.withTimeoutMillis(entityMetadata.getTimeoutMillis());
		
	}
	
	@Override
	public <T> Single<T> findOne(final Class<T> entityClass, DattyId id) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(id, "id is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		GetOperation getOp = toGetOperation(entityMetadata, id);
		
		return datty.execute(getOp).map(new Func1<GetResult, T>() {

			@Override
			public T call(GetResult res) {
				
				DattyRow row = res.getRow();
				if (row != null) {
					return (T) converter.read(entityClass, row);
					
				}
				
				return null;
			}
			
		});
		
	}

	@Override
	public <T> Single<T> findOne(final Class<T> entityClass, Single<DattyId> id) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(id, "id is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		Single<GetOperation> getOp = id.map(new Func1<DattyId, GetOperation>() {

			@Override
			public GetOperation call(DattyId dattyId) {
				return toGetOperation(entityMetadata, dattyId);
			}
			
		});
		
		return datty.execute(getOp).map(new Func1<GetResult, T>() {

			@Override
			public T call(GetResult res) {
				
				DattyRow row = res.getRow();
				if (row != null) {
					return (T) converter.read(entityClass, row);
				}
				
				return null;
			}
			
		});
		
	}

	protected ExistsOperation toExistsOperation(DattyPersistentEntity<?> entityMetadata, DattyId id) {
		
		ExistsOperation op = new ExistsOperation(entityMetadata.getSetName())
		.setSuperKey(id.getSuperKey())
		.setMajorKey(id.getMajorKey());
		
		if (id.hasMinorKey()) {
			op.addMinorKey(id.getMinorKey());
		}
		else {
			op.anyMinorKey();
		}
		
		op.withTimeoutMillis(entityMetadata.getTimeoutMillis());
		
		return op;
	}
	
	@Override
	public Single<Boolean> exists(final Class<?> entityClass, DattyId id) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(id, "id is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		ExistsOperation existsOp = toExistsOperation(entityMetadata, id);
		
		return datty.execute(existsOp).map(new Func1<ExistsResult, Boolean>() {

			@Override
			public Boolean call(ExistsResult res) {
				return res.exists();
			}
			
		});
		
	}

	@Override
	public Single<Boolean> exists(Class<?> entityClass, Single<DattyId> id) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(id, "id is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		Single<ExistsOperation> existsOp = id.map(new Func1<DattyId, ExistsOperation>() {

			@Override
			public ExistsOperation call(DattyId dattyId) {
				return toExistsOperation(entityMetadata, dattyId);
			}
			
		});
		
		return datty.execute(existsOp).map(new Func1<ExistsResult, Boolean>() {

			@Override
			public Boolean call(ExistsResult res) {
				return res.exists();
			}
			
		});
	}

	@Override
	public <T> Observable<T> findAll(final Class<T> entityClass) {
		Assert.notNull(entityClass, "entityClass is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		ScanOperation scanOp = new ScanOperation(entityMetadata.getSetName());
		scanOp.withTimeoutMillis(entityMetadata.getTimeoutMillis());
		
		return datty.executeQuery(scanOp).map(new Func1<DattyResult, T>() {

			@Override
			public T call(DattyResult res) {

				QueryResult queryRes = (QueryResult) res;
				
				DattyRow row = queryRes.getRow();
				if (row != null) {
					return (T) converter.read(entityClass, row);
				}
				
				return null;
			}
			
		});
		
	}

	@Override
	public <T> Observable<T> findAll(Class<T> entityClass, Iterable<DattyId> ids) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(ids, "ids is null");
		return findAll(entityClass, Observable.from(ids));
	}

	@Override
	public <T> Observable<T> findAll(final Class<T> entityClass, Observable<DattyId> idStream) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(idStream, "idStream is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		Observable<DattyOperation> operations = idStream.map(new Func1<DattyId, DattyOperation>() {

			@Override
			public DattyOperation call(DattyId dattyId) {
				return toGetOperation(entityMetadata, dattyId);
			}
			
		});
		
		return datty.executeSequence(operations).map(new Func1<DattyResult, T>() {

			@Override
			public T call(DattyResult res) {
				
				GetResult getRes = (GetResult) res;
				
				DattyRow row = getRes.getRow();
				if (row != null) {
					return (T) converter.read(entityClass, row);
				}
				
				return null;
			}
			
		});
		
	}

	@Override
	public Single<Long> count(Class<?> entityClass) {
		Assert.notNull(entityClass, "entityClass is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		SizeOperation countOp = new SizeOperation(entityMetadata.getSetName());
		countOp.withTimeoutMillis(entityMetadata.getTimeoutMillis());

		return datty.executeQuery(countOp).map(new Func1<QueryResult, Long>() {

			@Override
			public Long call(QueryResult res) {
				return res.count();
			}
			
		}).toSingle();
		
	}
	
	protected RemoveOperation toRemoveOperation(DattyPersistentEntity<?> entityMetadata, DattyId id) {
		
		RemoveOperation removeOp = new RemoveOperation(entityMetadata.getSetName())
		.setSuperKey(id.getSuperKey())
		.setMajorKey(id.getMajorKey())
		.withTimeoutMillis(entityMetadata.getTimeoutMillis());

		if (entityMetadata.hasMinorKey()) {
			removeOp.addMinorKey(entityMetadata.getMinorKey());
		}
		else if (entityMetadata.useTags()) {
			for (Integer tag : entityMetadata.getPropertyTags()) {
				removeOp.addMinorKey(tag.toString());
			}
		}
		else {
			removeOp.addMinorKeys(entityMetadata.getPropertyNames());
		}
		
		return removeOp;
		
	}

	@Override
	public Completable delete(Class<?> entityClass, DattyId id) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(id, "id is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		RemoveOperation removeOp = toRemoveOperation(entityMetadata, id);
		
		return datty.execute(removeOp).toCompletable();
	}

	@Override
	public <T> Completable delete(Class<T> entityClass, T entity) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(entity, "entity is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		DattyId id = getId(entityMetadata, entity).orElse(null);
		
		if (id == null) {
			throw new MappingException("id property not found for " + entityMetadata);
		}
		
		RemoveOperation removeOp = toRemoveOperation(entityMetadata, id);
		
		return datty.execute(removeOp).toCompletable();
	}

	@Override
	public <T> Completable delete(Class<T> entityClass, Iterable<? extends T> entities) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(entities, "entities is null");
		return delete(entityClass, Observable.from(entities));
	}

	@Override
	public <T> Completable delete(Class<T> entityClass, Observable<? extends T> entityStream) {
		Assert.notNull(entityClass, "entityClass is null");
		Assert.notNull(entityStream, "entityStream is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		Observable<DattyOperation> operations = entityStream
				.map(new Func1<T, DattyOperation>() {

					@Override
					public DattyOperation call(T entity) {
						DattyId id = getId(entityMetadata, entity).orElse(null);
						if (id == null) {
							throw new MappingException("id property not found for " + entityMetadata);
						}
						return toRemoveOperation(entityMetadata, id);
					}
					
				});
		
		
		return datty.executeSequence(operations).toCompletable();
	}

	@Override
	public Completable deleteAll(Class<?> entityClass) {
		Assert.notNull(entityClass, "entityClass is null");
		
		final DattyPersistentEntity<?> entityMetadata = getPersistentEntity(entityClass);
		
		ClearOperation deleteOp = new ClearOperation(entityMetadata.getSetName())
			.withTimeoutMillis(entityMetadata.getTimeoutMillis());

		return datty.executeQuery(deleteOp).toCompletable();
	}

	@Override
	public DattyConverter getConverter() {
		return converter;
	}

	@Override
	public Datty getDatty() {
		return datty;
	}

	protected Optional<DattyId> getId(DattyPersistentEntity<?> entityMetadata, Object entity) {
		
		Optional<DattyPersistentProperty> idProperty = entityMetadata.getIdProperty();

		Object id;
		
		if (idProperty.isPresent()) {

			try {
				id = new BeanWrapperImpl(entity).getPropertyValue(idProperty.get().getName());
			} catch (Exception e) {
				throw new MappingException("fail to access id property: " + idProperty.get().getName(), e);
			}
		}
		else {
			
			return Optional.empty();
			
		}
		
		DattyId dattyId = converter.toDattyId(id);
		
		if (entityMetadata.hasMinorKey()) {
			dattyId.setMinorKey(entityMetadata.getMinorKey());
		}
		
		return Optional.of(dattyId);
		
	}
	
	@Override
	public  String getSetName(Class<?> entityClass) {
		return getPersistentEntity(entityClass).getSetName();
	}
	
	protected DattyPersistentEntity<?> getPersistentEntity(Class<?> entityClass) {

		Assert.notNull(entityClass, "empty entityClass");

		DattyPersistentEntity<?> entity = mappingContext.getPersistentEntity(entityClass)
				.orElse(null);

		if (entity == null) {
			throw new MappingException("persistent entity not found for a given class " + entityClass);
		}

		return entity;
	}

	
}
