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
package io.datty.unit;

import io.datty.api.Datty;
import io.datty.api.DattyKey;
import io.datty.api.DattySingle;
import io.datty.api.DattyStream;
import io.datty.api.operation.TypedOperation;
import io.datty.api.result.TypedResult;
import io.datty.spi.AbstractDattyAdapter;
import io.netty.buffer.ByteBuf;
import rx.Observable;
import rx.Single;

public class UnitDatty extends AbstractDattyAdapter implements Datty {

	private final DattySingle single;
	private final DattyStream stream;
	
	public UnitDatty(DattySingle single, DattyStream stream) {
		this.single = single;
		this.stream = stream;
	}
	
	@Override
	public <O extends TypedOperation<O, R>, R extends TypedResult<O>> Single<R> execute(O operation) {
		return single.execute(operation);
	}

	@Override
	public Observable<ByteBuf> streamOut(DattyKey key) {
		return stream.streamOut(key);
	}

	@Override
	public Single<Long> streamIn(DattyKey key, Observable<ByteBuf> value) {
		return stream.streamIn(key, value);
	}

}
