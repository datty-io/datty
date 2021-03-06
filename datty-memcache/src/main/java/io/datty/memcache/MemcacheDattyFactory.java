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
package io.datty.memcache;

import java.util.Properties;

import io.datty.api.DattyFactory;
import io.datty.api.DattyManager;

/**
 * MemcacheDattyFactory
 * 
 * @author Alex Shvid
 *
 */

public class MemcacheDattyFactory implements DattyFactory {

	@Override
	public String getFactoryName() {
		return "memcache";
	}

	@Override
	public DattyManager newInstance(Properties props) {
		return new MemcacheDattyManager(props);
	}

	@Override
	public DattyManager newDefaultInstance() {
		return new io.datty.unit.UnitDattyManager(new Properties());
	}

}
