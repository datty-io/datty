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
package io.datty.spi;

import io.datty.api.DattyError;
import io.datty.api.DattyOperation;
import io.datty.api.DattySingle;
import io.datty.api.operation.SetOperation;
import io.datty.api.operation.TypedOperation;
import io.datty.api.result.AbstractResult;
import io.datty.api.result.RecordResult;
import io.datty.api.result.TypedResult;
import io.datty.support.exception.DattyOperationException;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

/**
 * Client side
 * 
 * @author Alex Shvid
 *
 */

public class DattySingleProvider implements DattySingle {

	private final DattySingle driver;
	
	public DattySingleProvider(DattySingle driver) {
		this.driver = driver;
	}
	
	@Override
	public Observable<RecordResult> execute(SetOperation operation) {
		
		final RecordResult fallback = operation.getFallback();

		try {
			Observable<RecordResult> result = driver.execute(operation);
			
			if (fallback != null) {
				
				result = result.onErrorReturn(new Func1<Throwable, RecordResult>() {

					@Override
					public RecordResult call(Throwable t) {
						return fallback;
					}
					
				});
			}
			
			return result;
		}
		catch(RuntimeException e) {
			if (fallback != null) {
				return Observable.just(fallback);
			}
			else {
				return Observable.error(transformException(operation, e));
			}
		}
	}


	@Override
	public <O extends TypedOperation<O, R>, R extends TypedResult<O>> Single<R> execute(final O operation) {

		final R fallback = operation.getFallback();

		try {
			Single<R> result = driver.execute(operation);
			
			result = result.map(new Func1<R, R>() {

				@Override
				public R call(R res) {
					if (res instanceof AbstractResult) {
						@SuppressWarnings("unchecked")
						AbstractResult<O, R> abstractResult = (AbstractResult<O, R>) res;
						abstractResult.setOperation(operation);
					}
					return res;
				}
				
			});
			
			if (fallback != null) {
				
				result = result.onErrorReturn(new Func1<Throwable, R>() {

					@Override
					public R call(Throwable t) {
						return fallback;
					}
					
				});
			}
			
			return result;
		}
		catch(RuntimeException e) {
			if (fallback != null) {
				return Single.just(fallback);
			}
			else {
				return Single.error(transformException(operation, e));
			}
		}
	}

	@Override
	public <O extends TypedOperation<O, R>, R extends TypedResult<O>> Single<R> execute(Single<O> operation) {
		
		return operation.flatMap(new Func1<O, Single<R>>() {

			@Override
			public Single<R> call(O op) {
				return execute(op);
			}
			
		});
		
	}

	private static Throwable transformException(DattyOperation operation, Throwable t) {
		if (t instanceof DattyOperationException) {
			return t;
		}
		else {
			return new DattyOperationException(DattyError.ErrCode.UNKNOWN, operation, t);
		}
	}

}
