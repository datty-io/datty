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
package io.datty.aerospike;

import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Language;
import com.aerospike.client.task.RegisterTask;

import io.datty.api.DattyFactory;
import io.datty.api.DattyManager;

/**
 * AerospikeDattyFactory
 * 
 * @author Alex Shvid
 *
 */

public class AerospikeDattyFactory implements DattyFactory {

	private static final Logger logger = LoggerFactory.getLogger(AerospikeDattyFactory.class);
	
	@Override
	public String getFactoryName() {
		return "aerospike";
	}

	@Override
	public DattyManager newInstance(Properties props) {
		return new AerospikeDattyManager(props);
	}
	
	@Override
	public DattyManager newDefaultInstance() {
		Properties props = copyPropertiesWithPrefix(AerospikePropertyKeys.AEROSPIKE_PREFIX, System.getProperties());
		AerospikeDattyManager dattyManager = new AerospikeDattyManager(props);
		
		try {
			registerUnitUDF(dattyManager.getClient().getClient());
		}
		catch(AerospikeException e) {
			logger.warn("no permissions to register unit.lua, emulate mode is enabled for tests"); 
			/*
			 * Unable to register unit.lua for ExecuteOperation
			 */
		  dattyManager.setUnitEmulation(true);
		}
		
		return dattyManager;
	}

	@Override
	public String toString() {
		return "AerospikeDattyFactory";
	}
	
	private void registerUnitUDF(AerospikeClient client) {
		
		client.removeUdf(null, AerospikeConstants.UDF_UNIT_SERVER_PATH);
		
		RegisterTask task = client.register(null, getClass().getClassLoader(), AerospikeConstants.UDF_UNIT_CLASSPATH, AerospikeConstants.UDF_UNIT_SERVER_PATH, Language.LUA);
		task.waitTillComplete();
		
	}
	
	private static Properties copyPropertiesWithPrefix(String prefix, Properties src) {
		int prefixLength = prefix.length();
		Properties dest = new Properties();
		Enumeration<Object> e = src.keys();
		while(e.hasMoreElements()) {
			Object key = e.nextElement();
			if (key instanceof String) {
				String strKey = (String) key;
				if (strKey.startsWith(prefix)) {
					dest.put(strKey.substring(prefixLength), src.get(key));
				}
			}
		}
		return dest;
	}
	
}
