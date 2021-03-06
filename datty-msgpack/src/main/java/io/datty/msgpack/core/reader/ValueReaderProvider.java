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
package io.datty.msgpack.core.reader;

/**
 * ValueReaderProvider
 * 
 * @author Alex Shvid
 *
 */

public interface ValueReaderProvider {

	/**
	 * Finds value reader for type
	 * 
	 * @param type - class type
	 * @return not null value reader
	 * @throws MessageIOException if not found
	 */
	
	<T> ValueReader<T> find(Class<T> type);
	
}
