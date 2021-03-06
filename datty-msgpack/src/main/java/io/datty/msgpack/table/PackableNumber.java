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
import org.msgpack.value.impl.ImmutableDoubleValueImpl;
import org.msgpack.value.impl.ImmutableLongValueImpl;

import io.datty.msgpack.core.writer.DoubleWriter;
import io.datty.msgpack.core.writer.LongWriter;
import io.datty.msgpack.table.support.PackableException;
import io.datty.msgpack.table.support.PackableNumberFormatException;
import io.datty.msgpack.table.util.PackableStringifyUtil;
import io.datty.msgpack.table.util.PackableStringifyUtil.NumberType;
import io.netty.buffer.ByteBuf;

/**
 * Immutable number type
 * 
 * @author Alex Shvid
 *
 */

public final class PackableNumber extends PackableValue<PackableNumber> {

	private final long longValue;
	private final double doubleValue;
	private final PackableNumberType type;

	public PackableNumber(long value) {
		this.type = PackableNumberType.LONG;
		this.longValue = value;
		this.doubleValue = 0.0;
	}
	
	public PackableNumber(double value) {
		this.type = PackableNumberType.DOUBLE;
		this.longValue = 0L;
		this.doubleValue = value;
	}

	public PackableNumber(String stringValue) {
		
		if (stringValue == null) {
			throw new IllegalArgumentException("empty value");
		}
		
		NumberType numberType = PackableStringifyUtil.detectNumber(stringValue);
		
		switch(numberType) {
		case LONG:
			try {
				this.type = PackableNumberType.LONG;
				this.longValue = Long.parseLong(stringValue);
				this.doubleValue = 0.0;
			}
			catch(NumberFormatException e) {
				throw new PackableNumberFormatException(stringValue, e);
			}				
			break;
		case DOUBLE:
			try {
				this.type = PackableNumberType.DOUBLE;
				this.longValue = 0L;
				this.doubleValue = Double.parseDouble(stringValue);
			}
			catch(NumberFormatException e) {
				throw new PackableNumberFormatException(stringValue, e);
			}			
			break;
		case NAN:
		default:
			throw new PackableNumberFormatException(stringValue);
		}
		
	}
	
	/**
	 * Gets type of the Msg number
	 * 
	 * @return not null type
	 */
	
	public PackableNumberType getType() {
		return type;
	}

	/**
	 * Add number
	 * 
	 * @param number - number to add
	 * @return new immutable Msg number
	 */
	
	public PackableNumber add(PackableNumber otherNumber) {
		switch(otherNumber.getType()) {
		case LONG:
			return add(otherNumber.asLong());
		case DOUBLE:
			return add(otherNumber.asDouble());
		}
		throw new PackableException("unexpected type: " + otherNumber.getType());
	}

	/**
	 * Add number
	 * 
	 * @param number - number to add
	 * @return new immutable Msg number
	 */

	public PackableNumber add(long otherLongValue) {
		switch(type) {
		case LONG:
			return new PackableNumber(longValue + otherLongValue);
		case DOUBLE:
			return new PackableNumber(doubleValue + (double) otherLongValue);
		}
		throw new PackableException("unexpected type: " + type);
	}

	/**
	 * Add number
	 * 
	 * @param number - number to add
	 * @return new immutable Msg number
	 */

	public PackableNumber add(double otherDoubleValue) {
		switch(type) {
		case LONG:
			return new PackableNumber((double) longValue + otherDoubleValue);
		case DOUBLE:
			return new PackableNumber(doubleValue + otherDoubleValue);
		}
		throw new PackableException("unexpected type: " + type);
	}
	
	/**
	 * Subtract number
	 * 
	 * @param number - number to add
	 * @return new immutable Msg number
	 */
	
	public PackableNumber subtract(PackableNumber otherNumber) {
		switch(otherNumber.getType()) {
		case LONG:
			return subtract(otherNumber.asLong());
		case DOUBLE:
			return subtract(otherNumber.asDouble());
		}
		throw new PackableException("unexpected type: " + otherNumber.getType());
	}	

	/**
	 * Subtract number
	 * 
	 * @param number - number to add
	 * @return new immutable Msg number
	 */
	
	public PackableNumber subtract(long otherLongValue) {
		switch(type) {
		case LONG:
			return new PackableNumber(longValue - otherLongValue);
		case DOUBLE:
			return new PackableNumber(doubleValue - (double) otherLongValue);
		}
		throw new PackableException("unexpected type: " + type);
	}
	
	/**
	 * Subtract number
	 * 
	 * @param number - number to add
	 * @return new immutable Msg number
	 */
	
	public PackableNumber subtract(double otherDoubleValue) {
		switch(type) {
		case LONG:
			return new PackableNumber((double) longValue - otherDoubleValue);
		case DOUBLE:
			return new PackableNumber(doubleValue - otherDoubleValue);
		}
		throw new PackableException("unexpected type: " + type);
	}

	/**
	 * Gets long, even if value is double, it will be converted to long
	 * 
	 * @return long value
	 */
	
	public long asLong() {
		switch(type) {
		case LONG:
			return longValue;
		case DOUBLE:
			return (long) doubleValue;
		}
		throw new PackableException("unexpected type: " + type);
	}

	/**
	 * Gets double, even if value is long, it will be converted to double
	 * 
	 * @return double value
	 */
	
	public double asDouble() {
		switch(type) {
		case LONG:
			return longValue;
		case DOUBLE:
			return doubleValue;
		}
		throw new PackableException("unexpected type: " + type);
	}

	@Override
	public String asString() {
		switch(type) {
		case LONG:
			return Long.toString(longValue);
		case DOUBLE:
			return Double.toString(doubleValue);
		}
		throw new PackableException("unexpected type: " + type);
	}
	
  @Override
  public Value toValue() {
		switch(type) {
		case LONG:
			return new ImmutableLongValueImpl(longValue);  
		case DOUBLE:
	    return new ImmutableDoubleValueImpl(doubleValue);  
		}	
		throw new PackableException("unexpected type: " + type);
  }
  
  @Override
	public void writeTo(MessagePacker packer) throws IOException {
		switch(type) {
		case LONG:
			packer.packLong(longValue);
			break;
		case DOUBLE:
			packer.packDouble(doubleValue);
			break;
		default:
		  throw new IOException("unexpected type: " + type);		
		}	
	}

	@Override
	public ByteBuf pack(ByteBuf buffer) throws IOException {
		switch(type) {
		case LONG:
			return LongWriter.INSTANCE.writeVLong(longValue, buffer);
		case DOUBLE:
			return DoubleWriter.INSTANCE.writeDouble(doubleValue, buffer);
		default:
		  throw new IOException("unexpected type: " + type);		
		}	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (type == PackableNumberType.DOUBLE) {
			long temp;
			temp = Double.doubleToLongBits(doubleValue);
			result = prime * result + (int) (temp ^ (temp >>> 32));
		}
		else if (type == PackableNumberType.LONG) {
			result = prime * result + (int) (longValue ^ (longValue >>> 32));
		}
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		PackableNumber other = (PackableNumber) obj;
		if (type != other.type)
			return false;
		if (type == PackableNumberType.DOUBLE && Double.doubleToLongBits(doubleValue) != Double.doubleToLongBits(other.doubleValue))
			return false;
		if (type == PackableNumberType.LONG && longValue != other.longValue)
			return false;
		return true;
	}

	@Override
	public void print(StringBuilder str, int initialSpaces, int tabSpaces) {
		str.append("PackableNumber [type=").append(type);
		if (type == PackableNumberType.LONG) {
			str.append(", longValue=").append(longValue);
		}
		else if (type == PackableNumberType.DOUBLE) {
			str.append(", doubleValue=").append(doubleValue);
		}
		str.append("]");
	}
	

}
