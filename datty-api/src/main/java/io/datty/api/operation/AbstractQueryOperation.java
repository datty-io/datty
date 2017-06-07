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
package io.datty.api.operation;

import io.datty.api.DattyConstants;
import io.datty.api.result.QueryResult;

/**
 * AbstractQueryOperation
 * 
 * @author Alex Shvid
 *
 */

public abstract class AbstractQueryOperation<O extends QueryOperation> implements QueryOperation {

	protected String setName;

	protected String superKey;
	
	protected int timeoutMillis = DattyConstants.UNSET_TIMEOUT;
	
	protected QueryResult fallback;
	
	public AbstractQueryOperation(String setName) {
		this.setName = setName;
	}
	
	@Override
	public boolean hasTimeoutMillis() {
		return timeoutMillis != DattyConstants.UNSET_TIMEOUT;
	}
	
	@Override
	public String getSetName() {
		return setName;
	}

	@Override
	public String getSuperKey() {
		return superKey;
	}

	public O setSuperKey(String superKey) {
		this.superKey = superKey;
		return castThis();
	}
	
	@Override
	public int getTimeoutMillis() {
		return timeoutMillis;
	}

	public O withTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
		return castThis();
	}
	
	@Override
	public QueryResult getFallback() {
		return fallback;
	}

	public O onFallback(QueryResult fallback) {
		this.fallback = fallback.setOperation(castThis());
		return castThis();
	}
	
	public O withFallback(QueryResult fallback) {
		return onFallback(fallback);
	}
	
	@SuppressWarnings("unchecked")
	protected O castThis() {
		return (O) this;
	}
	
	
}
