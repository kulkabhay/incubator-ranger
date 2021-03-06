/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ranger.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItem;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyItemAccess;
import org.apache.ranger.plugin.model.RangerPolicy.RangerPolicyResource;
import org.apache.ranger.plugin.model.RangerServiceDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerAccessTypeDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerResourceDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerServiceConfigDef;

import com.google.common.collect.Lists;

public class ValidationTestUtils {
	
	Map<String, String> createMap(String[] keys) {
		Map<String, String> result = new HashMap<String, String>();
		for (String key : keys) {
			result.put(key, "valueof-" + key);
		}
		return result;
	}

	// helper methods for tests
	List<RangerServiceConfigDef> createServiceConditionDefs(Object[][] input) {
		List<RangerServiceConfigDef> result = new ArrayList<RangerServiceDef.RangerServiceConfigDef>();
		
		for (Object data[] : input) {
			RangerServiceConfigDef aConfigDef = mock(RangerServiceConfigDef.class);
			when(aConfigDef.getName()).thenReturn((String)data[0]);
			when(aConfigDef.getMandatory()).thenReturn((boolean)data[1]);
			result.add(aConfigDef);
		}
		
		return result;
	}
	
	void checkFailureForSemanticError(List<ValidationFailureDetails> failures, String fieldName) {
		checkFailure(failures, null, null, true, fieldName, null);
	}

	void checkFailureForSemanticError(List<ValidationFailureDetails> failures, String fieldName, String subField) {
		checkFailure(failures, null, null, true, fieldName, subField);
	}

	void checkFailureForMissingValue(List<ValidationFailureDetails> failures, String field) {
		checkFailure(failures, null, true, null, field, null);
	}

	void checkFailureForMissingValue(List<ValidationFailureDetails> failures, String field, String subField) {
		checkFailure(failures, null, true, null, field, subField);
	}

	void checkFailureForInternalError(List<ValidationFailureDetails> failures, String fieldName) {
		checkFailure(failures, true, null, null, fieldName, null);
	}

	void checkFailureForInternalError(List<ValidationFailureDetails> failures) {
		checkFailure(failures, true, null, null, null, null);
	}

	void checkFailure(List<ValidationFailureDetails> failures, Boolean internalError, Boolean missing, Boolean semanticError, String field, String subField) {
		if (CollectionUtils.isEmpty(failures)) {
			fail("List of failures is null/empty!");
		} else {
			boolean found = false;
			for (ValidationFailureDetails f : failures) {
				if ((internalError == null || internalError == f._internalError) &&
						(missing == null || missing == f._missing) &&
						(semanticError == null || semanticError == f._semanticError) &&
						(field == null || field.equals(f._fieldName)) &&
						(subField == null || subField.equals(f._subFieldName))) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	List<RangerAccessTypeDef> createAccessTypeDefs(String[] names) {
		assertFalse(names == null); // fail if null is passed in!
		List<RangerAccessTypeDef> defs = new ArrayList<RangerServiceDef.RangerAccessTypeDef>();
		for (String name : names) {
			RangerAccessTypeDef def = mock(RangerAccessTypeDef.class);
			when(def.getName()).thenReturn(name);
			defs.add(def);
		}
		return defs;
	}

	RangerServiceDef createServiceDefWithAccessTypes(String[] accesses) {
		RangerServiceDef serviceDef = mock(RangerServiceDef.class);
		List<RangerAccessTypeDef> accessTypeDefs = new ArrayList<RangerServiceDef.RangerAccessTypeDef>();
		for (String access : accesses) {
			RangerAccessTypeDef accessTypeDef = mock(RangerAccessTypeDef.class);
			when(accessTypeDef.getName()).thenReturn(access);
			accessTypeDefs.add(accessTypeDef);
		}
		when(serviceDef.getAccessTypes()).thenReturn(accessTypeDefs);
		return serviceDef;
	}

	List<RangerPolicyItemAccess> createItemAccess(Object[][] data) {
		List<RangerPolicyItemAccess> accesses = new ArrayList<RangerPolicyItemAccess>();
		for (Object[] row : data) {
			RangerPolicyItemAccess access = mock(RangerPolicyItemAccess.class);
			when(access.getType()).thenReturn((String)row[0]);
			when(access.getIsAllowed()).thenReturn((Boolean)row[1]);
			accesses.add(access);
		}
		return accesses;
	}

	List<RangerPolicyItem> createPolicyItems(Object[] data) {
		List<RangerPolicyItem> policyItems = new ArrayList<RangerPolicyItem>();
		for (Object object : data) {
			@SuppressWarnings("unchecked")
			Map<String, Object[]> map = (Map<String, Object[]>) object; 
			RangerPolicyItem policyItem = mock(RangerPolicyItem.class);
			
			List<String> usersList = null;
			if (map.containsKey("users")) {
				usersList = Arrays.asList((String[])map.get("users"));
			}
			when(policyItem.getUsers()).thenReturn(usersList);
			
			List<String> groupsList = null;
			if (map.containsKey("groups")) {
				groupsList = Arrays.asList((String[])map.get("groups"));
			}
			when(policyItem.getGroups()).thenReturn(groupsList);
			
			String[] accesses = (String[])map.get("accesses");;
			Boolean[] isAllowedFlags = (Boolean[])map.get("isAllowed");
			List<RangerPolicyItemAccess> accessesList = null;
			if (accesses != null && isAllowedFlags != null) {
				accessesList = new ArrayList<RangerPolicyItemAccess>();
				for (int i = 0; i < accesses.length; i++) {
					String access = accesses[i];
					Boolean isAllowed = isAllowedFlags[i]; 
					RangerPolicyItemAccess itemAccess = mock(RangerPolicyItemAccess.class);
					when(itemAccess.getType()).thenReturn(access);
					when(itemAccess.getIsAllowed()).thenReturn(isAllowed);
					accessesList.add(itemAccess);
				}
			}
			when(policyItem.getAccesses()).thenReturn(accessesList);
			
			policyItems.add(policyItem);
		}
		return policyItems;
	}

	List<RangerResourceDef> createResourceDefs(Object[][] data) {
		// if data itself is null then return null back
		if (data == null) {
			return null;
		}
		List<RangerResourceDef> defs = new ArrayList<RangerResourceDef>();
		for (Object[] row : data) {
			RangerResourceDef aDef = null;
			if (row != null) {
				String name = null;
				Boolean mandatory = null;
				String regExPattern = null;
				switch(row.length) {
				case 3:
					regExPattern = (String)row[2];
				case 2:
					mandatory = (Boolean)row[1];
				case 1:
					name = (String)row[0];
				}
				aDef = mock(RangerResourceDef.class);
				when(aDef.getName()).thenReturn(name);
				when(aDef.getMandatory()).thenReturn(mandatory);
				when(aDef.getValidationRegEx()).thenReturn(regExPattern);
			}
			defs.add(aDef);
		}
		return defs;
	}

	List<RangerResourceDef> createResourceDefsWithRegEx(String[][] data) {
		// if data itself is null then return null back
		if (data == null) {
			return null;
		}
		List<RangerResourceDef> defs = new ArrayList<RangerResourceDef>();
		for (String[] row : data) {
			RangerResourceDef aDef = null;
			if (row != null) {
				String name = row[0];
				String regEx = row[1];
				aDef = mock(RangerResourceDef.class);
				when(aDef.getName()).thenReturn(name);
				when(aDef.getValidationRegEx()).thenReturn(regEx);
			}
			defs.add(aDef);
		}
		return defs;
	}

	Map<String, RangerPolicyResource> createPolicyResourceMap(Map<String, String[]> input) {
		if (input == null) {
			return null;
		}
		Map<String, RangerPolicyResource> resourceMap = new HashMap<String, RangerPolicyResource>();
		for (Map.Entry<String, String[]> entry : input.entrySet()) {
			String key = entry.getKey();
			String[] inputValues = entry.getValue();
			RangerPolicyResource policyResource = mock(RangerPolicyResource.class);
			if (inputValues != null) {
				List<String> values = Lists.newArrayList(inputValues);
				when(policyResource.getValues()).thenReturn(values);
			}
			resourceMap.put(key, policyResource);
		}
		return resourceMap;
	}
}
