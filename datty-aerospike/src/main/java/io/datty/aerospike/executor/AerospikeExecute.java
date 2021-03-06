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

import com.aerospike.client.Key;
import com.aerospike.client.Value;
import com.aerospike.client.policy.WritePolicy;

import io.datty.aerospike.AerospikeSet;
import io.datty.aerospike.AerospikeDattyManager;
import io.datty.aerospike.AerospikeConstants;
import io.datty.aerospike.support.AerospikeValueUtil;
import io.datty.api.operation.Execute;
import io.datty.api.result.ExecuteResult;
import rx.Single;
import rx.functions.Func1;

/**
 * AerospikeExecute
 * 
 * @author Alex Shvid
 *
 */

public enum AerospikeExecute implements AerospikeOperation<Execute, ExecuteResult> {

	INSTANCE;

	@Override
	public Single<ExecuteResult> execute(AerospikeSet set, Execute operation) {
		
		if (set.getParent().isUnitEmulation() && 
				AerospikeConstants.UDF_UNIT_LOOPBACK.equals(operation.getFunctionName())) {
			return Single.just(new ExecuteResult().set(operation.getArguments()));
		}
		
		AerospikeDattyManager manager = set.getParent();
		WritePolicy writePolicy = set.getConfig().getWritePolicy(operation, false);
		Key recordKey = new Key(manager.getConfig().getNamespace(), set.getName(), operation.getMajorKey());
		Value arguments = AerospikeValueUtil.toValue(operation.getArguments());
		
		Single<Object> result = manager.getClient().execute(writePolicy, recordKey, 
				operation.getPackageName(), operation.getFunctionName(), 
				new Value[] { arguments },
				set.singleExceptionTransformer(operation, false));
		
		return result.map(new Func1<Object, ExecuteResult>() {

			@Override
			public ExecuteResult call(Object aerospikeValue) {
				return new ExecuteResult().set(AerospikeValueUtil.toByteBuf(aerospikeValue));
			}
			
		});
		
	}

	
}
