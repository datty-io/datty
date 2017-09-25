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
package io.datty.msgpack.core;

import io.datty.msgpack.MessageReader;
import io.datty.msgpack.support.MessageIOException;
import io.netty.buffer.ByteBuf;

/**
 * MapMessageReader
 * 
 * @author Alex Shvid
 *
 */

public class MapMessageReader extends ValueMessageReader implements MessageReader {

	private final int size;
	
	public MapMessageReader(int size) {
		this.size = size;
	}
	
	public MapMessageReader(ByteBuf buffer) {
		if (!isMap(buffer)) {
			throw new MessageIOException("expected map message");
		}
		size = readMapHeader(buffer);
	}
	
	@Override
	public int size() {
		return size;
	}
	
}
