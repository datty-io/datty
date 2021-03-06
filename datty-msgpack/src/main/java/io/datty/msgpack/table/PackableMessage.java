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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.msgpack.core.MessageFormat;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.core.buffer.ArrayBufferOutput;
import org.msgpack.value.Value;
import org.msgpack.value.impl.ImmutableBinaryValueImpl;
import org.msgpack.value.impl.ImmutableMapValueImpl;
import org.msgpack.value.impl.ImmutableStringValueImpl;

import io.datty.msgpack.table.support.PackableMessageException;

/**
 * PackableMessage
 * 
 * Event contains both header and body.
 * 
 * @author Alex Shvid
 *
 */

public final class PackableMessage {

	/**
	 * All headers are under this key
	 */
	
	static final String HEADER_KEY = "header";

	/**
	 * All payloads are under this key
	 */
	
	static final String BODY_KEY = "body";
	
	private final Map<String, String> header = new HashMap<String, String>();
	private final Map<String, Payload> body = new HashMap<String, Payload>();

	interface Payload {
		
		/**
		 * Get bytes of the payload
		 * 
		 * @param copy - copy bytes if needed
		 * @return bytes
		 */
		
		byte[] getBytes(boolean copy);
		
		/**
		 * Converts payload to utf-8 string
		 * 
		 * @return string
		 */
		
		String toUtf8();
		
		/**
		 * Converts payload to message value
		 * 
		 * @return message value
		 */
		
		PackableValue<?> toMessageValue();
		
		/**
		 * Converts to message pack value
		 * 
		 * @return value
		 */
		
		Value toValue();
		
		/**
		 * Writes payload to packer
		 * 
		 * @param packer - message packer
		 * @throws IOException
		 */
		
		void writeTo(MessagePacker packer) throws IOException;
		
	}
	
	/**
	 * Binary payload implementation
	 * 
	 * @author Alex Shvid
	 *
	 */
	
	private static final class BinaryPayload implements Payload {
		
		private final byte[] value;
		
		public BinaryPayload(MessageUnpacker unpacker) throws IOException {
     	int length = unpacker.unpackBinaryHeader();
     	this.value = new byte[length];
     	unpacker.readPayload(value);
		}
		
		public BinaryPayload(byte[] payload, boolean copy) {
			this.value = copy ? Arrays.copyOf(payload, payload.length) : payload;
		}

		@Override
		public byte[] getBytes(boolean copy) {
			 return copy ? Arrays.copyOf(value, value.length) : value;
		}
		
		@Override
		public String toUtf8() {
			return new String(value, StandardCharsets.UTF_8);
		}

		@Override
		public PackableValue<?> toMessageValue() {
			return new PackableString(value, false);
		}

		@Override
		public Value toValue() {
			return new ImmutableBinaryValueImpl(value);
		}

		@Override
		public void writeTo(MessagePacker packer) throws IOException {
    	packer.packBinaryHeader(value.length);
    	packer.writePayload(value);
		}

	}
	
	/**
	 * Utf8 payload implementation
	 * 
	 * @author Alex Shvid
	 *
	 */
	
	private static final class Utf8Payload implements Payload {
		
		private final String value;
		
		public Utf8Payload(MessageUnpacker unpacker) throws IOException {
     	int length = unpacker.unpackRawStringHeader();
     	byte[] bytes = new byte[length];
     	unpacker.readPayload(bytes);
     	this.value = new String(bytes, StandardCharsets.UTF_8);
		}
		
		public Utf8Payload(String payload) {
			this.value = payload;
		}
		
		@Override
		public byte[] getBytes(boolean copy) {
			return value.getBytes(StandardCharsets.UTF_8);
		}

		@Override
		public String toUtf8() {
			return value;
		}
		
		@Override
		public PackableValue<?> toMessageValue() {
			return new PackableString(value);
		}

		@Override
		public Value toValue() {
			return new ImmutableStringValueImpl(value);
		}

		@Override
		public void writeTo(MessagePacker packer) throws IOException {
    	packer.packString(value);
		}
		
	}
	
	/**
	 * Packable value payload implementation
	 * 
	 * @author Alex Shvid
	 *
	 */
	
	private static final class PackableValuePayload implements Payload {
		
		private final PackableValue<?> value;
		
		public PackableValuePayload(MessageUnpacker unpacker) throws IOException {
			this.value = PackableValueFactory.newValue(unpacker);
		}
		
		public PackableValuePayload(PackableValue<?> payload) {
			this.value = payload;
		}
		
		@Override
		public byte[] getBytes(boolean copy) {
			return value.toByteArray();
		}

		@Override
		public String toUtf8() {
			return value.asString();
		}
		
		@Override
		public PackableValue<?> toMessageValue() {
			return value;
		}

		@Override
		public Value toValue() {
			return value.toValue();
		}

		@Override
		public void writeTo(MessagePacker packer) throws IOException {
			value.writeTo(packer);
		}
		
	}
	
	public PackableMessage() {
	}

	public PackableMessage(byte[] buffer) {
		
		if (buffer == null) {
			throw new IllegalArgumentException("null buffer");
		}
		
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(buffer);
		try {
			parse(unpacker);
		} catch (IOException e) {
			throw new PackableMessageException("unexpected IOException", e);
		}
	}
	
	public PackableMessage(byte[] buffer, int offset, int length) {
		
		if (buffer == null) {
			throw new IllegalArgumentException("null buffer");
		}
		
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(buffer, offset, length);
		try {
			parse(unpacker);
		} catch (IOException e) {
			throw new PackableMessageException("unexpected IOException", e);
		}		
	}
	
	public PackableMessage(ByteBuffer buffer) {
		
		if (buffer == null) {
			throw new IllegalArgumentException("null buffer");
		}
		
		MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(buffer);
		try {
			parse(unpacker);
		} catch (IOException e) {
			throw new PackableMessageException("unexpected IOException", e);
		}
	}
	
	private void parse(MessageUnpacker unpacker) throws IOException {
		
		if(!unpacker.hasNext()) { 
			return;
		}
		
		MessageFormat format = unpacker.getNextFormat();

		if (isNull(format)) {
			unpacker.unpackNil();
			return;
		}
		
		if (!isMap(format)) {
			throw new IOException("expected Map in message pack format");
		}
		
    int size = unpacker.unpackMapHeader();
    
    for (int i = 0; i != size; ++i) {
    	
    	String key = unpacker.unpackString();
    	
    	if (HEADER_KEY.equals(key)) {
    		parseHeader(unpacker);
    	}
    	else if (BODY_KEY.equals(key)) {
    		parseBody(unpacker);
    	}
    	
    }
		
	}
	
	private void parseHeader(MessageUnpacker unpacker) throws IOException {
		
		if(!unpacker.hasNext()) { 
			return;
		}
		
		MessageFormat format = unpacker.getNextFormat();

		if (isNull(format)) {
			unpacker.unpackNil();
			return;
		}
		
		if (!isMap(format)) {
			throw new PackableMessageException("expected Map in message pack format");
		}
		
    int size = unpacker.unpackMapHeader();
		
    for (int i = 0; i != size; ++i) {
    	
     	String key = unpacker.unpackString();
     	String value = unpacker.unpackString();
     	
     	header.put(key, value);
     	
    }
		
	}
	
	private void parseBody(MessageUnpacker unpacker) throws IOException {
		
		if(!unpacker.hasNext()) { 
			return;
		}
		
		MessageFormat format = unpacker.getNextFormat();

		if (isNull(format)) {
			unpacker.unpackNil();
			return;
		}
		
		if (!isMap(format)) {
			throw new PackableMessageException("expected Map in message pack format");
		}
		
    int size = unpacker.unpackMapHeader();
		
    for (int i = 0; i != size; ++i) {
    	
     	String key = unpacker.unpackString();
     	
     	Payload payload = parsePayload(unpacker);
     	
     	if (payload != null) {
     		body.put(key, payload);
     	}
     	
    }
		
	}
	
	private Payload parsePayload(MessageUnpacker unpacker) throws IOException {
		
		if(!unpacker.hasNext()) { 
			return null;
		}
		
		MessageFormat format = unpacker.getNextFormat();
		
		if (isNull(format)) {
			unpacker.unpackNil();
			return null;
		}
		
		if (isBinary(format)) {
			return new BinaryPayload(unpacker);
		}
		
		if (isUtf8(format)) {
			return new Utf8Payload(unpacker);
		}
		
		return new PackableValuePayload(unpacker);
		
	}
	
	private static boolean isNull(MessageFormat format) {
		return MessageFormat.NIL == format;
	}

	private static boolean isMap(MessageFormat format) {

		switch (format) {

		case FIXMAP:
		case MAP16:
		case MAP32:
			return true;

		default:
			return false;

		}

	}
	
	private static boolean isBinary(MessageFormat format) {

		switch (format) {

		case BIN8:
		case BIN16:
		case BIN32:
			return true;

		default:
			return false;

		}

	}
	
	private static boolean isUtf8(MessageFormat format) {

		switch (format) {

		case FIXSTR:
		case STR8:
		case STR16:
		case STR32:
			return true;

		default:
			return false;

		}

	}
	
	/**
	 * Checks if message is empty
	 * 
	 * @return true if both header and body are empty
	 */
	
	public boolean isEmpty() {
		return header.isEmpty() && body.isEmpty();
	}

	/**
	 * Adds header to the message
	 * 
	 * @param key - name of the header
	 * @param value - value of the header
	 * @return this
	 */
	
	public PackableMessage addHeader(String key, String value) {
		if (value != null) {
			header.put(key, value);
		}
		else {
			header.remove(key);
		}
		return this;
	}
	
	/**
	 * Gets header
	 * 
	 * @param key - header key
	 * @return null or header value
	 */
	
	public String getHeader(String key) {
		return header.get(key);
	}
  
	/**
   * Gets all header keys from the message
   * 
   * @return not null set
   */
	
	public Set<String> getHeaderKeys() {
		return header.keySet();
	}
	
	/**
	 * Adds payload to the message
	 * 
	 * @param key - name of the payload
	 * @param payload - message value
	 * @return this
	 */
	
	public PackableMessage addPayload(String key, PackableValue<?> payload) {
		if (payload != null) {
			body.put(key, new PackableValuePayload(payload));
		}
		else {
			body.remove(key);
		}
		return this;
	}

	/**
	 * Adds payload to the message
	 * 
	 * @param key - name of the payload
	 * @param payload - byte array
	 * @param copy - copy if needed
	 * @return this
	 */
	
	public PackableMessage addPayload(String key, byte[] payload, boolean copy) {
		if (payload != null) {
			body.put(key, new BinaryPayload(payload, copy));
		}
		else {
			body.remove(key);
		}
		return this;
	}
	
	/**
	 * Adds payload as UTF-8 string
	 * 
	 * @param key - name of the payload
	 * @param payload - byte array
	 * @return this
	 */
	
	public PackableMessage addPayloadUtf8(String key, String payload) {
		if (payload != null) {
			body.put(key, new Utf8Payload(payload));
		}
		else {
			body.remove(key);
		}
		return this;
	}
	
	/**
	 * Gets typed payload
	 * 
	 * @param key - payload key
	 * @return null or payload value
	 */
	
	@SuppressWarnings("unchecked")
	public <T extends PackableValue<?>> T getTypedPayload(String key) {
		return (T) getPayload(key);
	}

	/**
	 * Gets payload
	 * 
	 * @param key - payload key
	 * @return null or payload value
	 */
	
	public PackableValue<?> getPayload(String key) {
		Payload payload = body.get(key);
		return payload != null ? payload.toMessageValue() : null;
	}

	/**
	 * Gets payload
	 * 
	 * @param key - payload key
	 * @param copy - copy bytes if needed
	 * @return null or payload value
	 */
	
	public byte[] getPayload(String key, boolean copy) {
		Payload payload = body.get(key);
		return payload != null ? payload.getBytes(copy) : null;
	}

	/**
	 * Gets payload as UTF-8 string
	 * 
	 * @param key - payload key
	 * @return null or payload value
	 */
	
	public String getPayloadUtf8(String key) {
		Payload payload = body.get(key);
		return payload != null ? payload.toUtf8() : null;
	}
	
	/**
	 * Gets payload keys from the message
	 *  
	 * @return not null set 
	 */
	
	public Set<String> getPayloadKeys() {
		return body.keySet();
	}

	/**
	 * Converts message to json format
	 * 
	 * @return not null json
	 */
	
	public String toJson() {
		return toValue().toJson();
	}

	/**
	 * Serialize message to MsgPack
	 * 
	 * @return not null byte array
	 */
	
	public byte[] toByteArray() {
		
		ArrayBufferOutput out = new ArrayBufferOutput();
		try {
			MessagePacker packer = MessagePack.newDefaultPacker(out);
			writeTo(packer);
			packer.flush();
		} catch (IOException e) {
			throw new PackableMessageException("IOException happened during serialization to byte array", e);
		}

		return out.toByteArray();
	}
	
	/**
	 * Converts message to MsgPack value
	 * 
	 * @return not null value object
	 */
	
	public Value toValue() {
		
		int size = 0;
		if (!header.isEmpty()) {
			size += 2;
		}
		if (!body.isEmpty()) {
			size += 2;
		}
		
		int index = 0;
    Value[] array = new Value[size];
		
    if (!header.isEmpty()) {
    	array[index++] = new ImmutableStringValueImpl(HEADER_KEY);
    	array[index++] = toHeaderValue();
    }
    
    if (!body.isEmpty()) {
    	array[index++] = new ImmutableStringValueImpl(BODY_KEY);
    	array[index++] = toBodyValue();
    }
    
    return new ImmutableMapValueImpl(array);
	}
	
	private Value toHeaderValue() {
		
		int index = 0;
    Value[] array = new Value[header.size()  * 2];
    
    for (Map.Entry<String, String> entry : header.entrySet()) {
    	
    	String key = entry.getKey();
    	String value = entry.getValue();

      array[index++] = new ImmutableStringValueImpl(key);
      array[index++] = new ImmutableStringValueImpl(value);
    	
    }
    
    return new ImmutableMapValueImpl(array);
	}
	
	private Value toBodyValue() {
		
		int index = 0;
    Value[] array = new Value[body.size()  * 2];
    
    for (Map.Entry<String, Payload> entry : body.entrySet()) {
    	
    	String key = entry.getKey();
    	Payload payload = entry.getValue();
    	
      array[index++] = new ImmutableStringValueImpl(key);
      array[index++] = payload.toValue();
    }
    
    return new ImmutableMapValueImpl(array);
	}
	
  private void writeTo(MessagePacker packer) throws IOException {
  	
  	int size = 0;
  	if (!header.isEmpty()) {
  		size++;
  	}
  	if (!body.isEmpty()) {
  		size++;
  	}
  	
    packer.packMapHeader(size);
    
    if (!header.isEmpty()) {
    	packer.packString(HEADER_KEY);
    	writeHeaderTo(packer);
    }

    if (!body.isEmpty()) {
    	packer.packString(BODY_KEY);
    	writeBodyTo(packer);
    }

  } 
  
  private void writeHeaderTo(MessagePacker packer) throws IOException {
  	
    int size = header.size();
    
    packer.packMapHeader(size);
    
    for (Map.Entry<String, String> entry : header.entrySet()) {
    	
    	String key = entry.getKey();
    	String value = entry.getValue();
    	
    	packer.packString(key);
    	packer.packString(value);
    	
    }
    
  } 
  
  private void writeBodyTo(MessagePacker packer) throws IOException {
  	
    int size = body.size();
    
    packer.packMapHeader(size);
    
    for (Map.Entry<String, Payload> entry : body.entrySet()) {
    	
    	String key = entry.getKey();
    	Payload payload = entry.getValue();
    	
    	packer.packString(key);
    	payload.writeTo(packer);
    	
    }
    
  }

	@Override
	public String toString() {
		return "PackableMessage [header=" + header + ", body=" + body + "]";
	} 
	
}
