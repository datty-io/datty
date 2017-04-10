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
package io.datty.api;

/**
 * Type of the action if cache exists
 * 
 * @author dadril
 *
 */

public enum CacheExistsAction {

	/**
	 * Creates a new cache only with given properties
	 * 
	 * throws CacheExistsException if region already exists
	 */

	CREATE_ONLY,

	/**
	 * Creates a new cache if not exists, otherwise returns existing cache
	 * without changing it's properties
	 */

	CREATE_IF_NOT_EXISTS,

	/**
	 * Creates a new cache if not exists, otherwise updates properties for
	 * existing one
	 * 
	 * throws CacheMismatchException if it is impossible to update properties
	 * runtime if they different
	 * 
	 */

	UPDATE;

}