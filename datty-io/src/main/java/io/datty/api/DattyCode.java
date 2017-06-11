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
package io.datty.api;

/**
 * DattyCode
 * 
 * Serialization codes
 * 
 * @author Alex Shvid
 *
 */

public final class DattyCode {

	private DattyCode() {
	}

  public static final int FIELD_OPCODE = 1;      // Int
  public static final int FIELD_RESCODE = 2;     // Int        
	
  public static final int FIELD_SET_NAME = 10;         // String
  public static final int FIELD_SUPER_KEY = 11;        // String  
  public static final int FIELD_MAJOR_KEY = 12;        // String
  public static final int FIELD_MINOR_KEY = 13;        // String
  public static final int FIELD_TIMEOUT_MLS = 14;      // Int
  
  
  public static final int FIELD_MAXCODE = 20;    
  
}