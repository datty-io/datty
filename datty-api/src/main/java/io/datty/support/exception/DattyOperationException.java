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
package io.datty.support.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.datty.api.DattyError;
import io.datty.api.DattyOperation;

/**
 * Base class that comes to stream errors from single operation
 * 
 * @author Alex Shvid
 *
 */

public class DattyOperationException extends DattyException implements DattyError {

	private static final long serialVersionUID = 5237472769279645794L;
	
	private final ErrCode errorCode;
	private final String errorMessage;
	private final String errorStackTrace;
	
	private final DattyOperation operation;
	
	public DattyOperationException(DattyError.ErrCode errCode, DattyOperation operation) {
		super(errCode.name());
		this.errorCode = errCode;
		this.errorMessage = errCode.name();
		this.errorStackTrace = null;
		this.operation = operation;
	}
	
	public DattyOperationException(DattyError.ErrCode errCode, DattyOperation operation, Throwable t) {
		super(errCode.name(), t);
		this.errorCode = errCode;
		this.errorMessage = t.getMessage();
		this.errorStackTrace = getStackTrace(t);
		this.operation = operation;
	}
	
	public DattyOperationException(DattyError.ErrCode errCode, String message, DattyOperation operation) {
		super(message);
		this.errorCode = errCode;
		this.errorMessage = errCode.name();
		this.errorStackTrace = null;
		this.operation = operation;
	}
	
	public DattyOperationException(DattyError.ErrCode errCode, String message, DattyOperation operation, Throwable t) {
		super(message, t);
		this.errorCode = errCode;
		this.errorMessage = message;
		this.errorStackTrace = getStackTrace(t);
		this.operation = operation;
	}
	
	public DattyOperationException(DattyError error, DattyOperation operation) {
		super(error.getErrorMessage());
		this.errorCode = error.getErrorCode();
		this.errorMessage = error.getErrorMessage();
		this.errorStackTrace = error.getErrorStackTrace();
		this.operation = operation;
	}
	
	@Override
	public ErrCode getErrorCode() {
		return errorCode;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public String getErrorStackTrace() {
		return errorStackTrace;
	}

	public DattyOperation getOperation() {
		return operation;
	}

	public static String getStackTrace(Throwable t) {
		StringWriter w = new StringWriter();
		if (t != null) {
			t.printStackTrace(new PrintWriter(w));
		}
		return w.toString();
	}
	
}
