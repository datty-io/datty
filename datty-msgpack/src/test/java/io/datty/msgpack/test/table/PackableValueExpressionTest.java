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
package io.datty.msgpack.test.table;

import org.junit.Assert;
import org.junit.Test;

import io.datty.msgpack.table.PackableNumber;
import io.datty.msgpack.table.PackableString;
import io.datty.msgpack.table.PackableTable;
import io.datty.msgpack.table.PackableValueExpression;



/**
 * PackableValueExpressionTest
 * 
 * @author Alex Shvid
 *
 */

public class PackableValueExpressionTest {

	@Test(expected=IllegalArgumentException.class)
	public void testNull() {
		
		new PackableValueExpression(null);
		
	}
	
	@Test
	public void testEmpty() {
		
		PackableValueExpression ve = new PackableValueExpression("");
		Assert.assertTrue(ve.isEmpty());
		
	}
	
	@Test
	public void testSingle() {
		
		PackableValueExpression ve = new PackableValueExpression("logins");
		Assert.assertFalse(ve.isEmpty());
		Assert.assertEquals(1, ve.size());
		Assert.assertEquals("logins", ve.get(0));
		
		//System.out.println(ve);
		
	}
	
	@Test
	public void testSingleIndex() {
		
		PackableValueExpression ve = new PackableValueExpression("[4]");
		Assert.assertFalse(ve.isEmpty());
		Assert.assertEquals(1, ve.size());
		Assert.assertEquals("4", ve.get(0));
		
		//System.out.println(ve);
		
	}
	
	@Test
	public void testTwo() {
		
		PackableValueExpression ve = new PackableValueExpression("name.first");
		Assert.assertFalse(ve.isEmpty());
		Assert.assertEquals(2, ve.size());
		Assert.assertEquals("name", ve.get(0));
		Assert.assertEquals("first", ve.get(1));
		
		//System.out.println(ve);
		
	}
	
	@Test
	public void testTwoWithIndex() {
		
		PackableValueExpression ve = new PackableValueExpression("name[1]");
		Assert.assertFalse(ve.isEmpty());
		Assert.assertEquals(2, ve.size());
		Assert.assertEquals("name", ve.get(0));
		Assert.assertEquals("1", ve.get(1));
		
		//System.out.println(ve);
		
	}
	
	@Test
	public void testComplex() {
		
		PackableValueExpression ve = new PackableValueExpression("educations[2].name");
		Assert.assertFalse(ve.isEmpty());
		Assert.assertEquals(3, ve.size());
		Assert.assertEquals("educations", ve.get(0));
		Assert.assertEquals("2", ve.get(1));
		Assert.assertEquals("name", ve.get(2));
		
		//System.out.println(ve);
		
	}
	
	@Test
	public void testSimple() {
		
		PackableTable table = new PackableTable();
		table.put("name", "John");
		
		PackableValueExpression ve = new PackableValueExpression("name");
		
		Assert.assertEquals(new PackableString("John"), table.get(ve));
		Assert.assertEquals(new PackableString("John"), table.getString(ve));
		
	}
	
	@Test
	public void testInner() {
		
		PackableTable innerTable = new PackableTable();
		innerTable.put("first", "John");
		innerTable.put("last", "Dow");
		
		PackableTable table = new PackableTable();
		table.put("name", innerTable);
		
		PackableValueExpression ve = new PackableValueExpression("name.first");
		
		Assert.assertEquals(new PackableString("John"), table.get(ve));
		Assert.assertEquals(new PackableString("John"), table.getString(ve));
		
	}
	
	@Test
	public void testInnerWithIndex() {
		
		PackableTable innerTable = new PackableTable();
		innerTable.put(1, "John");
		innerTable.put(2, "Dow");
		
		PackableTable table = new PackableTable();
		table.put("name", innerTable);
		
		PackableValueExpression ve = new PackableValueExpression("name[1]");
		
		Assert.assertEquals(new PackableString("John"), table.get(ve));
		Assert.assertEquals(new PackableString("John"), table.getString(ve));
		
	}
	
	@Test
	public void testEmptyPut() {
		
		PackableTable table = new PackableTable();
		PackableValueExpression ve = new PackableValueExpression("name");

		table.put(ve, "John");
		
		Assert.assertEquals(new PackableString("John"), table.get(ve));
		Assert.assertEquals(new PackableString("John"), table.getString(ve));
	}
	
	@Test
	public void testEmptyInnerPut() {
		
		PackableTable table = new PackableTable();
		PackableValueExpression ve = new PackableValueExpression("name.first");

		table.put(ve, "John");
		
		Assert.assertEquals(new PackableString("John"), table.get(ve));
		Assert.assertEquals(new PackableString("John"), table.getString(ve));
		
	}
	
	@Test
	public void testIncrement() {
		
		PackableTable table = new PackableTable();
		PackableValueExpression ve = new PackableValueExpression("logins");

		PackableNumber number = table.getNumber(ve);
		if (number == null) {
			number = new PackableNumber(0l);
		}
		number = number.add(new PackableNumber(1));
		
		table.put(ve, number);
		
		Assert.assertEquals(1, table.getNumber(ve).asLong());
		
	}
	
	@Test
	public void testDecrement() {
		
		PackableTable table = new PackableTable();
		PackableValueExpression ve = new PackableValueExpression("logins");

		PackableNumber number = table.getNumber(ve);
		if (number == null) {
			number = new PackableNumber(0l);
		}
		number = number.subtract(new PackableNumber(1.0));
		
		table.put(ve, number);
		
		Assert.assertEquals(-1, table.getNumber(ve).asLong());
		
	}

	
}
