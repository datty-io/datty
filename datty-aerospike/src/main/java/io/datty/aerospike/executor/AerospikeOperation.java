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
package io.datty.aerospike.executor;

import io.datty.aerospike.AerospikeSet;
import io.datty.api.operation.TypedOperation;
import io.datty.api.result.TypedResult;
import rx.Single;

/**
 * OperationExecutor
 * 
 * @author Alex Shvid
 *
 * @param <O> - operation type
 * @param <R> - result type
 */

public interface AerospikeOperation<O extends TypedOperation<O, R>, R extends TypedResult<O>> {
	
	/**
	 * Executes operation
	 * 
	 * @param set - datty set
	 * @param operation - datty operation
	 * @return result
	 */
	
	Single<R> execute(AerospikeSet set, O operation);
	
}