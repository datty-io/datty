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

import io.datty.api.DattyField;
import io.datty.api.DattyRecordIO;
import io.datty.api.UpdatePolicy;
import io.datty.api.version.VersionIO;
import io.datty.msgpack.MessageReader;
import io.datty.util.FieldWriter;
import io.netty.buffer.ByteBuf;

/**
 * PutOperationIO
 * 
 * @author Alex Shvid
 *
 */

public class PutOperationIO extends AbstractOperationIO<PutOperation> {

	@Override
	public PutOperation newOperation() {
		return new PutOperation();
	}
	
	@Override
	public boolean readField(PutOperation operation, DattyField field, MessageReader reader, ByteBuf source) {
		
		boolean read = super.readField(operation, field, reader, source);
		
		if (read) {
			return true;
		}
		
		switch(field) {
		
			case USE_VERSION:
				operation.setUseVersion(((Boolean) reader.readValue(source, true)));
				return true;
			
			case VERSION:
				operation.useVersion(VersionIO.readVersion(source));
				return true;
			
			case RECORD:
				operation.setRecord(DattyRecordIO.readRecord(source));
				return true;

			case TTL_SEC:
				operation.setTtlSeconds(((Long) reader.readValue(source, true)).intValue());
				return true;
	
			case UPDATE_POLICY:
				int code = ((Long) reader.readValue(source, true)).intValue();
				operation.setUpdatePolicy(UpdatePolicy.findByCode(code));
				return true;				
				
			default:
				return false;
				
		}
		
	}
	
	@Override
	protected void writeFields(PutOperation operation, FieldWriter fieldWriter) {
		
		super.writeFields(operation, fieldWriter);
		
		if (operation.useVersion()) {
			fieldWriter.writeField(DattyField.USE_VERSION, operation.useVersion());
		}
		
		if (operation.hasVersion()) {
			fieldWriter.writeField(DattyField.VERSION, operation.getVersion());
		}
		
		if (operation.hasRecord()) {
			fieldWriter.writeField(DattyField.RECORD, operation.getRecord());
		}
		
		if (operation.hasTtlSeconds()) {
			fieldWriter.writeField(DattyField.TTL_SEC, operation.getTtlSeconds());
		}
		
		fieldWriter.writeField(DattyField.UPDATE_POLICY, operation.getUpdatePolicy().getCode());
		
	}
	
}
