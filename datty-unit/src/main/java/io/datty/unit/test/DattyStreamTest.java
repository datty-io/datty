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

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import io.datty.api.DattyKey;
import io.datty.unit.UnitConstants;
import io.netty.buffer.ByteBuf;
import rx.Observable;
import rx.functions.Func2;


/**
 * DattyStreamTest
 * 
 * @author Alex Shvid
 *
 */

public class DattyStreamTest extends AbstractDattyUnitTest {

	@Test
	public void testEmpty() {
		
		String majorKey = UUID.randomUUID().toString();
		
		DattyKey key = new DattyKey()
				.setSetName(SET_NAME)
				.setMajorKey(majorKey)
				.setMinorKey(minorKey);
		
		Iterable<ByteBuf> iter = dattyManager.getDatty().streamOut(key).toBlocking().toIterable();
		List<ByteBuf> list = Lists.newArrayList(iter);
		
		Assert.assertEquals(0, list.size());
		
	}
	
	@Test
	public void testSingle() {
	
		String majorKey = UUID.randomUUID().toString();
		
		DattyKey key = new DattyKey()
				.setSetName(SET_NAME)
				.setMajorKey(majorKey)
				.setMinorKey(minorKey);
		
		Observable<ByteBuf> input = Observable.just(value.resetReaderIndex());
		
		Long written = dattyManager.getDatty().streamIn(key, input).toBlocking().value();
		
		Assert.assertEquals(written, Long.valueOf(value.resetReaderIndex().readableBytes()));
		
		final ByteBuf destBuffer = UnitConstants.ALLOC.buffer();
		
		dattyManager.getDatty().streamOut(key)
				.reduce(destBuffer, new Func2<ByteBuf, ByteBuf, ByteBuf>() {

			@Override
			public ByteBuf call(ByteBuf dest, ByteBuf chunk) {
				dest.writeBytes(chunk);
				return dest;
			}
			
		}).toBlocking().subscribe();
		
		Assert.assertEquals(value, destBuffer.resetReaderIndex());
		
	}
	
	@Test
	public void testTwo() {
	
		String majorKey = UUID.randomUUID().toString();
		
		DattyKey key = new DattyKey()
				.setSetName(SET_NAME)
				.setMajorKey(majorKey)
				.setMinorKey(minorKey);
		
		Observable<ByteBuf> input = Observable.just(value.resetReaderIndex(), newValue.resetReaderIndex());
		
		Long written = dattyManager.getDatty().streamIn(key, input).toBlocking().value();
		
		Assert.assertEquals(written, Long.valueOf(value.resetReaderIndex().readableBytes() + newValue.resetReaderIndex().readableBytes()));
		
		final ByteBuf destBuffer = UnitConstants.ALLOC.buffer();
		
		dattyManager.getDatty().streamOut(key)
				.reduce(destBuffer, new Func2<ByteBuf, ByteBuf, ByteBuf>() {

			@Override
			public ByteBuf call(ByteBuf dest, ByteBuf chunk) {
				dest.writeBytes(chunk);
				return dest;
			}
			
		}).toBlocking().subscribe();
		
		ByteBuf expected = UnitConstants.ALLOC.buffer();
		expected.writeBytes(value.resetReaderIndex());
		expected.writeBytes(newValue.resetReaderIndex());
		
		Assert.assertEquals(expected.resetReaderIndex(), destBuffer.resetReaderIndex());
		
	}
	
}
