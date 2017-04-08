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
package io.datty.unit;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import io.datty.api.CacheFactory;
import io.datty.api.CacheManager;

/**
 * Unit factory test
 * 
 * @author dadril
 *
 */

public class UnitDattyFactoryTest {

	@Test
	public void testCreation() throws IOException {

		UnitCacheFactory factory = new UnitCacheFactory();

		Assert.assertEquals("unit", factory.getFactoryName());

		Properties props = new Properties();
		props.put(UnitPropertyKeys.NAME, "test");

		CacheManager cacheManager = factory.newInstance(props);

		Assert.assertNotNull(cacheManager);


	}

	@Test
	public void testLocator() throws IOException {

		CacheFactory.Locator locator = new CacheFactory.Locator();

		CacheFactory factory = locator.getByName("unit");

		Assert.assertEquals("unit", factory.getFactoryName());
		Assert.assertTrue(factory instanceof UnitCacheFactory);

	}

}
