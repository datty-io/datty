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
package io.datty.api.result;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.datty.api.operation.ExistsOperation;

public class ExistsResult extends AbstractResult<ExistsOperation, ExistsResult> {

	private final boolean value;
	
	private final Map<String, Boolean> values;
	
	public ExistsResult(boolean value) {
		this(value, Collections.<String, Boolean>emptyMap());
	}
	
	public ExistsResult(boolean value, Map<String, Boolean> values) {
		this.value = value;
		this.values = values;
	}

	public static ExistsResult of(boolean value) {
		return new ExistsResult(value);
	}
	
	public static ExistsResult of(boolean value, Map<String, Boolean> values) {
		return new ExistsResult(value, values);
	}
	
	public boolean get() {
		return value;
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}
	
	public int size() {
		return values.size();
	}
	
	public Set<String> minorKeys() {
		return values.keySet();
	}
	
	public Boolean get(String minorKey) {
		return values.get(minorKey);
	}
	
	@Override
	public ResCode getCode() {
		return ResCode.EXISTS;
	}

	@Override
	public String toString() {
		return "ExistsResult [value=" + value + ", values=" + values + "]";
	}
	
	
}
