/*
 * Copyright (C) 2016 Data Drilling Corporation
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

import io.datty.api.Datty;
import io.datty.api.DattyFactory;
import io.datty.unit.UnitDattyFactory;
import io.datty.unit.UnitPropertyKeys;

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit factory test
 * 
 * @author dadril
 *
 */

public class UnitDattyFactoryTest {

	@Test
	public void testCreation() throws IOException {

		UnitDattyFactory factory = new UnitDattyFactory();

		Assert.assertEquals("unit", factory.getName());

		Properties props = new Properties();
		props.put(UnitPropertyKeys.NAME, "test");

		Datty datty = factory.newInstance(props);

		Assert.assertNotNull(datty);
		Assert.assertEquals("test", datty.getName());

	}

	@Test
	public void testLocator() throws IOException {

		DattyFactory.Locator locator = new DattyFactory.Locator();

		DattyFactory factory = locator.getByName("unit");

		Assert.assertEquals("unit", factory.getName());
		Assert.assertTrue(factory instanceof UnitDattyFactory);

	}

}