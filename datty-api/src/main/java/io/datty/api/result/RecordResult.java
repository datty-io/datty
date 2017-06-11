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
import java.util.Set;

import io.datty.api.DattyResult;
import io.datty.api.DattyRow;
import io.datty.api.operation.Version;
import io.netty.buffer.ByteBuf;

/**
 * RecordResult
 * 
 * @author Alex Shvid
 *
 */

public class RecordResult implements DattyResult {

	private String majorKey;
	
	/**
	 * Record version if exists
	 */
	
	private Version version;
	
	private DattyRow row;
	
	/**
	 * Count records in result
	 * Valid for CountOperation and DeleteOperation
	 */
	
	private long count;
	
	public RecordResult() {
	}
	
	public String getMajorKey() {
		return majorKey;
	}

	public RecordResult setMajorKey(String majorKey) {
		this.majorKey = majorKey;
		return this;
	}

	public DattyRow getRow() {
		return row;
	}
	
	public RecordResult setRow(DattyRow row) {
		this.row = row;
		return this;
	}
	
	public boolean hasRow() {
		return row != null;
	}
	
	public RecordResult addValue(String minorKey, ByteBuf value) {
		if (row == null) {
			row = new DattyRow();
		}
		row.putValue(minorKey, value, true);
		return this;
	}
	
	public boolean hasVersion() {
		return version != null;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public RecordResult setVersion(Version version) {
		this.version = version;
		return this;
	}
	
	public long count() {
		return count;
	}

	public long getCount() {
		return count;
	}
	
	public void setCount(long count) {
		this.count = count;
	}

	public boolean exists() {
		return this.version != null;
	}

	public boolean isEmpty() {
		
		if (!hasRow()) {
			return true;
		}
		
		return row.isEmpty();
	}
	
	public Set<String> minorKeys() {
		
		if (!hasRow()) {
			return Collections.emptySet();
		}
		
		return row.minorKeys();
	}
	
	public ByteBuf get(String minorKey) {
		
		if (!hasRow()) {
			return null;
		}
		
		return row.get(minorKey);
	}
	
	public int size() {
		
		if (!hasRow()) {
			return 0;
		}
		
		return row.size();
	}
	
	@Override
	public ResCode getCode() {
		return ResCode.RECORD;
	}

	@Override
	public String toString() {
		return "SetResult [majorKey=" + majorKey + ", version=" + version + ", row=" + row
				+ ", count=" + count + "]";
	}

}