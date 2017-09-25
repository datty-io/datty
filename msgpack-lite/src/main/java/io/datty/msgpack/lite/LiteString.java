package io.datty.msgpack.lite;

/**
 * Immutable String
 * 
 * That can be two types: UFT-8 and BYTES strings
 * 
 * @author Alex Shvid
 *
 */

public interface LiteString extends LiteValue<LiteString> {

	/**
	 * Gets type of the string
	 * 
	 * @return not null type, can by UTF8 or Bytes
	 */
	
	LiteStringType getType();

	/**
	 * Gets value as utf8 string
	 * 
	 * @return utf8 string
	 */
	
	String toUtf8();
	
	/**
	 * Gets value as byte array
	 * 
	 * @param copy - copy bytes or not
	 * @return not null bytes
	 */
	
	byte[] getBytes(boolean copy);
	
}