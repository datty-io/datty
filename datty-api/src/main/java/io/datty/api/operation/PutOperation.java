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
package io.datty.api.operation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.datty.api.result.PutResult;
import io.netty.buffer.ByteBuf;

/**
 * Put operation
 * 
 * @author Alex Shvid
 *
 */

public class PutOperation extends AbstractUpdateOperation<PutOperation, PutResult> {

	/**
	 * Key is the minorKey, value is payload
	 */
	private Map<String, ByteBuf> newValues;
	
	public PutOperation(String storeName) {
		super(storeName);
	}

	public PutOperation(String storeName, String majorKey) {
		super(storeName);
		setMajorKey(majorKey);
	}

	public PutOperation addValue(String minorKey, ByteBuf valueOrNull) {
		if (this.newValues == null) {
			this.newValues = Collections.singletonMap(minorKey, valueOrNull);
		}
		else {
			if (this.newValues.size() == 1) {
				this.newValues = new HashMap<>(this.newValues);
			}
			this.newValues.put(minorKey, valueOrNull);
		}
		return this;
	}
	
	public PutOperation addValues(Map<String, ByteBuf> values) {
		if (this.newValues == null) {
			this.newValues = new HashMap<String, ByteBuf>(values);
		}
		else {
			if (this.newValues.size() == 1) {
				this.newValues = new HashMap<String, ByteBuf>(this.newValues);
			}
			this.newValues.putAll(values);
		}
		return this;
	}

	public Map<String, ByteBuf> getValues() {
		return newValues != null ? newValues : Collections.<String, ByteBuf>emptyMap();
	}
	
	@Override
	public OpCode getCode() {
		return OpCode.PUT;
	}

	@Override
	public String toString() {
		return "PutOperation [newValues=" + newValues + ", updatePolicy=" + updatePolicy + ", cacheName=" + cacheName
				+ ", superKey=" + superKey + ", majorKey=" + majorKey + ", timeoutMillis=" + timeoutMillis + "]";
	}

}
