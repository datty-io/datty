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
package io.datty.unit.executor;

import java.util.concurrent.ConcurrentMap;

import io.datty.api.operation.Execute;
import io.datty.api.result.ExecuteResult;
import io.datty.unit.UnitRecord;
import rx.Single;

/**
 * ExecuteExecutor
 * 
 * @author Alex Shvid
 *
 */

public enum ExecuteExecutor implements OperationExecutor<Execute, ExecuteResult> {

	INSTANCE;

	@Override
	public Single<ExecuteResult> execute(ConcurrentMap<String, UnitRecord> recordMap, Execute operation) {
		
		return Single.just(new ExecuteResult().set(operation.getArguments()));
		
	}

	
}
