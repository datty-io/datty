/*
 * Copyright (C) 2016 Data Drilling Corporation
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

import io.datty.api.DattyResult;
import io.datty.api.SingleResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch Result
 * 
 * @author dadril
 *
 */

public class BatchResult implements DattyResult {

	private final List<SingleResult<?>> list = new ArrayList<>();

	/**
	 * Adds operation to the batch
	 * 
	 * @param operation
	 *            - not null single operation
	 * @return this
	 */

	public BatchResult add(SingleResult<?> result) {
		list.add(result);
		return this;
	}

	public List<SingleResult<?>> getList() {
		return list;
	}

	public int size() {
		return list.size();
	}

	public SingleResult<?> get(int i) {
		return list.get(i);
	}

}