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
package io.datty.spring.converter.name;

import org.junit.Assert;
import org.junit.Test;

import io.datty.api.DattyRow;
import io.datty.spring.support.DattyConverterUtil;
import io.netty.buffer.ByteBuf;

/**
 * NamedEntityTest
 * 
 * @author Alex Shvid
 *
 */

public class NamedEntityTest {

	@Test
	public void testNames() {
		
		NamedEntity entity = new NamedEntity();
		entity.setId(123L);
		entity.setName("Alex");
		
		DattyRow row = new DattyRow();
		DattyConverterUtil.write(entity, row);

		ByteBuf bb = row.get("id").asByteBuf();
		Assert.assertNotNull(bb);
		
		bb = row.get("first").asByteBuf();
		Assert.assertNotNull(bb);
		
		NamedMigratedEntity actual = DattyConverterUtil.read(NamedMigratedEntity.class, row);
		Assert.assertEquals(entity.getId(), actual.getId());
		Assert.assertEquals(entity.getName(), actual.getName());
		
		row = new DattyRow();
		DattyConverterUtil.write(actual, row);

		bb = row.get("id").asByteBuf();
		Assert.assertNotNull(bb);
		
		Assert.assertNull(row.get("first"));
		
		bb = row.get("last").asByteBuf();
		Assert.assertNotNull(bb);
		
		//System.out.println(Arrays.toString(ByteBufUtil.getBytes(bb)));
		
		actual = DattyConverterUtil.read(NamedMigratedEntity.class, row);
		Assert.assertEquals(entity.getId(), actual.getId());
		Assert.assertEquals(entity.getName(), actual.getName());
		
	}
	
}