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
package com.dadril.datty.support.exception;

/**
 * Datty configuration exception
 * 
 * @author dadril
 *
 */

public class ConfigurationException extends DattyException {

	private static final long serialVersionUID = 7345765063535628582L;

	public ConfigurationException(String msg) {
		super(msg);
	}

	public ConfigurationException(String msg, Throwable t) {
		super(msg, t);
	}

	public ConfigurationException(Throwable t) {
		super(t);
	}

}
