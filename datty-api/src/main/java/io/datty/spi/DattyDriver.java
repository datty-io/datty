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

import java.util.List;

import io.datty.api.Datty;
import io.datty.api.DattyBatch;
import io.datty.api.DattyKey;
import io.datty.api.DattyResult;
import io.datty.api.DattySingle;
import io.datty.api.DattyStream;
import io.datty.api.operation.RecordOperation;
import io.datty.api.operation.SetOperation;
import io.datty.api.operation.TypedOperation;
import io.datty.api.result.RecordResult;
import io.datty.api.result.TypedResult;
import io.netty.buffer.ByteBuf;
import rx.Observable;
import rx.Single;

/**
 * DattyDriver
 * 
 * @author Alex Shvid
 *
 */

public class DattyDriver implements Datty {

	private final DattySingle single;
	private final DattyBatch batch;
	private final DattyStream stream;

	public DattyDriver(DattySingle single, DattyBatch batch, DattyStream stream) {
		this.single = single;
		this.batch = batch;
		this.stream = stream;
	}

	public static Builder newBuilder() {
		return new Builder();
	}
	
	@Override
	public Observable<RecordResult> execute(SetOperation operation) {
		return single.execute(operation);
	}

	@Override
	public <O extends TypedOperation<O, R>, R extends TypedResult<O>> Single<R> execute(O operation) {
		return single.execute(operation);
	}

	@Override
	public <O extends TypedOperation<O, R>, R extends TypedResult<O>> Single<R> execute(Single<O> operation) {
		return single.execute(operation);
	}

	@Override
	public Single<List<DattyResult>> executeBatch(List<RecordOperation> operations) {
		return batch.executeBatch(operations);
	}

	@Override
	public Observable<DattyResult> executeSequence(Observable<RecordOperation> operations) {
		return batch.executeSequence(operations);
	}

	@Override
	public Observable<ByteBuf> streamOut(DattyKey key) {
		return stream.streamOut(key);
	}

	@Override
	public Single<Long> streamIn(DattyKey key, Observable<ByteBuf> value) {
		return stream.streamIn(key, value);
	}

	public static final class Builder {

		private DattySingle single;
		private DattyBatch batch;
		private DattyStream stream;

		public DattySingle getSingle() {
			return single;
		}

		public Builder setSingle(DattySingle single) {
			this.single = single;
			return this;			
		}

		public DattyBatch getBatch() {
			return batch;
		}

		public Builder setBatch(DattyBatch batch) {
			this.batch = batch;
			return this;
		}

		public DattyStream getStream() {
			return stream;
		}

		public Builder setStream(DattyStream stream) {
			this.stream = stream;
			return this;			
		}

		public DattyDriver build() {

			if (single == null) {
				throw new IllegalArgumentException("empty single");
			}

			if (batch == null) {
				throw new IllegalArgumentException("empty batch");
			}

			if (stream == null) {
				throw new IllegalArgumentException("empty stream");
			}

			return new DattyDriver(single, batch, stream);
		}

	}

}
