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

import io.datty.api.result.RemoveResult;

/**
 * Remove Operation
 * 
 * @author Alex Shvid
 *
 */

public class RemoveOperation extends AbstractRecordOperation<RemoveOperation, RemoveResult> {

	public RemoveOperation() {
	}
	
	public RemoveOperation(String setName) {
		setSetName(setName);
	}

	public RemoveOperation(String setName, String majorKey) {
		setSetName(setName).setMajorKey(majorKey);
	}
	
	@Override
	public OpCode getCode() {
		return OpCode.REMOVE;
	}

	@Override
	public String toString() {
		return "RemoveOperation [allMinorKeys=" + allMinorKeys + ", minorKeys=" + minorKeys + ", setName=" + setName
				+ ", superKey=" + superKey + ", majorKey=" + majorKey + "]";
	}

}
