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
package io.datty.unit.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import io.datty.api.DattyOperation;
import io.datty.api.DattyResult;
import io.datty.api.operation.GetOperation;
import io.datty.api.result.GetResult;

/**
 * DattyBatchTest
 * 
 * @author dadril
 *
 */

public class DattyBatchTest extends AbstractDattyUnitTest {

	@Test
	public void testEmpty() {
		
		List<DattyResult> result = cacheManager.getDatty().executeBatch(Collections.<DattyOperation>emptyList()).toBlocking().value();
		
		Assert.assertTrue(result.isEmpty());
		
	}
	
	@Test
	public void testSingle() {
		
		String majorKey = UUID.randomUUID().toString();
		
		GetOperation getOp = new GetOperation(CACHE_NAME, majorKey);
		
		List<DattyOperation> batch = new ArrayList<DattyOperation>();
		batch.add(getOp);
		
		List<DattyResult> results = cacheManager.getDatty().executeBatch(batch).toBlocking().value();
		
		Assert.assertFalse(results.isEmpty());
		Assert.assertEquals(1, results.size());
		
		GetResult result = (GetResult) results.get(0);
		Assert.assertNotNull(result);
		Assert.assertFalse(result.exists());
		
	}
	
}