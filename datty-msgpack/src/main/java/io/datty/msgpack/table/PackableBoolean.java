/*
 * Copyright (C) 2017 Datty.io Authors
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
package io.datty.msgpack.table;

import java.io.IOException;

import org.msgpack.core.MessagePacker;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableBooleanValueImpl;

import io.datty.msgpack.core.writer.BooleanWriter;
import io.netty.buffer.ByteBuf;

/**
 * Immutable boolean type 
 * 
 * @author Alex Shvid
 *
 */

public final class PackableBoolean extends PackableValue<PackableBoolean> {

	private final boolean booleanValue;
	
	public PackableBoolean(boolean value) {
		this.booleanValue = value;
	}
	
	public PackableBoolean(String value) {
		this.booleanValue = Boolean.parseBoolean(value);
	}
	
	@Override
	public String asString() {
		return Boolean.toString(booleanValue);
	}

	/**
	 * Gets boolean value
	 * 
	 * @return bool value
	 */
	public boolean asBoolean() {
		return booleanValue;
	}

	@Override
  public Value toValue() {
    return booleanValue ? ImmutableBooleanValueImpl.TRUE : ImmutableBooleanValueImpl.FALSE;
  }
	
  @Override
	public void writeTo(MessagePacker packer) throws IOException {
  	packer.packBoolean(booleanValue);
	}

	@Override
	public ByteBuf pack(ByteBuf buffer) throws IOException {
		return BooleanWriter.INSTANCE.writeBoolean(booleanValue, buffer);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (booleanValue ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PackableBoolean other = (PackableBoolean) obj;
		if (booleanValue != other.booleanValue)
			return false;
		return true;
	}

	@Override
	public void print(StringBuilder str, int initialSpaces, int tabSpaces) {
		str.append("PackableBoolean [booleanValue=").append(booleanValue).append("]");
	}
	
}
