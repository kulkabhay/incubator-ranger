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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ranger.plugin.model.RangerPolicy;
import org.apache.ranger.plugin.model.RangerService;
import org.apache.ranger.plugin.model.RangerServiceDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerAccessTypeDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerResourceDef;
import org.apache.ranger.plugin.model.RangerServiceDef.RangerServiceConfigDef;
import org.apache.ranger.plugin.store.ServiceStore;
import org.apache.ranger.rest.RangerValidator.Action;
import org.junit.Before;
import org.junit.Test;

public class TestRangerValidator {

	static class RangerValidatorForTest extends RangerValidator {

		public RangerValidatorForTest(ServiceStore store) {
			super(store);
		}
		
		boolean isValid(String behavior) {
			boolean valid;
			if (behavior.equals("valid")) {
				valid = true;
			} else {
				valid = false;
			}
			return valid;
		}
	}
	
	@Before
	public void before() {
		_store = mock(ServiceStore.class);
		_validator = new RangerValidatorForTest(_store);
	}

	@Test
	public void test_ctor_firewalling() {
		try {
			// service store can't be null during construction  
			new RangerValidatorForTest(null);
			fail("Should have thrown exception!");
		} catch (IllegalArgumentException e) {
			// expected exception
		}
	}
	
	@Test
	public void test_validate() {
		// default implementation should fail.  This is abstract class.  Sub-class must do something sensible with isValid
		try {
			_validator.validate(1L, Action.CREATE);
			fail("Should have thrown exception!");
		} catch (Exception e) {
			// ok expected exception
			String message = e.getMessage();
			assertTrue(message.contains("internal error"));
		}
	}

	@Test
	public void test_getServiceConfigParameters() {
		// reasonable protection against null values
		Set<String> parameters = _validator.getServiceConfigParameters(null);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());
		
		RangerService service = mock(RangerService.class);
		when(service.getConfigs()).thenReturn(null);
		parameters = _validator.getServiceConfigParameters(service);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());
		
		when(service.getConfigs()).thenReturn(new HashMap<String, String>());
		parameters = _validator.getServiceConfigParameters(service);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());

		String[] keys = new String[] { "a", "b", "c" };
		Map<String, String> map = _utils.createMap(keys);
		when(service.getConfigs()).thenReturn(map);
		parameters = _validator.getServiceConfigParameters(service);
		for (String key: keys) {
			assertTrue("key", parameters.contains(key));
		}
	}
	
	@Test
	public void test_getRequiredParameters() {
		// reasonable protection against null things
		Set<String> parameters = _validator.getRequiredParameters(null);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());

		RangerServiceDef serviceDef = mock(RangerServiceDef.class);
		when(serviceDef.getConfigs()).thenReturn(null);
		parameters = _validator.getRequiredParameters(null);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());

		List<RangerServiceConfigDef> configs = new ArrayList<RangerServiceDef.RangerServiceConfigDef>();
		when(serviceDef.getConfigs()).thenReturn(configs);
		parameters = _validator.getRequiredParameters(null);
		assertNotNull(parameters);
		assertTrue(parameters.isEmpty());
		
		Object[][] input = new Object[][] {
				{ "param1", false },
				{ "param2", true },
				{ "param3", true },
				{ "param4", false },
		};
		configs = _utils.createServiceConditionDefs(input);
		when(serviceDef.getConfigs()).thenReturn(configs);
		parameters = _validator.getRequiredParameters(serviceDef);
		assertTrue("result does not contain: param2", parameters.contains("param2"));
		assertTrue("result does not contain: param3", parameters.contains("param3"));
	}
	
	@Test
	public void test_getServiceDef() {
		try {
			// if service store returns null or throws an exception then service is deemed invalid
			when(_store.getServiceDefByName("return null")).thenReturn(null);
			when(_store.getServiceDefByName("throw")).thenThrow(new Exception());
			RangerServiceDef serviceDef = mock(RangerServiceDef.class);
			when(_store.getServiceDefByName("good-service")).thenReturn(serviceDef);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception during mocking!");
		}
		
		assertNull(_validator.getServiceDef("return null"));
		assertNull(_validator.getServiceDef("throw"));
		assertFalse(_validator.getServiceDef("good-service") == null);
	}

	@Test
	public void test_getPolicy() throws Exception {
		// if service store returns null or throws an exception then return null policy
		when(_store.getPolicy(1L)).thenReturn(null);
		when(_store.getPolicy(2L)).thenThrow(new Exception());
		RangerPolicy policy = mock(RangerPolicy.class);
		when(_store.getPolicy(3L)).thenReturn(policy);
		
		assertNull(_validator.getPolicy(1L));
		assertNull(_validator.getPolicy(2L));
		assertTrue(_validator.getPolicy(3L) != null);
	}

	@Test
	public void test_getService_byId() throws Exception {
		// if service store returns null or throws an exception then service is deemed invalid
		when(_store.getService(1L)).thenReturn(null);
		when(_store.getService(2L)).thenThrow(new Exception());
		RangerService service = mock(RangerService.class);
		when(_store.getService(3L)).thenReturn(service);
		
		assertNull(_validator.getService(1L));
		assertNull(_validator.getService(2L));
		assertTrue(_validator.getService(3L) != null);
	}

	@Test
	public void test_getService() {
		try {
			// if service store returns null or throws an exception then service is deemed invalid
			when(_store.getServiceByName("return null")).thenReturn(null);
			when(_store.getServiceByName("throw")).thenThrow(new Exception());
			RangerService service = mock(RangerService.class);
			when(_store.getServiceByName("good-service")).thenReturn(service);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception during mocking!");
		}
		
		assertNull(_validator.getService("return null"));
		assertNull(_validator.getService("throw"));
		assertFalse(_validator.getService("good-service") == null);
	}
	
	@Test
	public void test_getAccessTypes() {
		// passing in null service def
		Set<String> accessTypes = _validator.getAccessTypes((RangerServiceDef)null);
		assertTrue(accessTypes.isEmpty());
		// that has null or empty access type def
		RangerServiceDef serviceDef = mock(RangerServiceDef.class);
		when(serviceDef.getAccessTypes()).thenReturn(null);
		accessTypes = _validator.getAccessTypes(serviceDef);
		assertTrue(accessTypes.isEmpty());

		List<RangerAccessTypeDef> accessTypeDefs = new ArrayList<RangerServiceDef.RangerAccessTypeDef>();
		when(serviceDef.getAccessTypes()).thenReturn(accessTypeDefs);
		accessTypes = _validator.getAccessTypes(serviceDef);
		assertTrue(accessTypes.isEmpty());
		
		// having null accesstypedefs
		accessTypeDefs.add(null);
		accessTypes = _validator.getAccessTypes(serviceDef);
		assertTrue(accessTypes.isEmpty());
		
		// access type defs with null empty blank names are skipped, spaces within names are preserved
		String[] names = new String[] { null, "", "a", "  ", "b ", "		", " c" };
		accessTypeDefs.addAll(_utils.createAccessTypeDefs(names));
		accessTypes = _validator.getAccessTypes(serviceDef);
		assertEquals(3, accessTypes.size());
		assertTrue(accessTypes.contains("a"));
		assertTrue(accessTypes.contains("b "));
		assertTrue(accessTypes.contains(" c"));
	}
	
	@Test
	public void test_getResourceNames() {
		// passing in null service def
		Set<String> accessTypes = _validator.getMandatoryResourceNames((RangerServiceDef)null);
		assertTrue(accessTypes.isEmpty());
		// that has null or empty access type def
		RangerServiceDef serviceDef = mock(RangerServiceDef.class);
		when(serviceDef.getResources()).thenReturn(null);
		accessTypes = _validator.getMandatoryResourceNames(serviceDef);
		assertTrue(accessTypes.isEmpty());

		List<RangerResourceDef> resourceDefs = new ArrayList<RangerResourceDef>();
		when(serviceDef.getResources()).thenReturn(resourceDefs);
		accessTypes = _validator.getMandatoryResourceNames(serviceDef);
		assertTrue(accessTypes.isEmpty());
		
		// having null accesstypedefs
		resourceDefs.add(null);
		accessTypes = _validator.getMandatoryResourceNames(serviceDef);
		assertTrue(accessTypes.isEmpty());
		
		// access type defs with null empty blank names are skipped, spaces within names are preserved
		Object[][] data = {
				{ "a", true }, // all good
				null,          // this should put a null element in the resource def!
				{ "b", null }, // mandatory field is null, i.e. false
				{ "c", false }, // mandatory field false
				{ "d", true }, // all good
		};
		resourceDefs.addAll(_utils.createResourceDefs(data));
		accessTypes = _validator.getMandatoryResourceNames(serviceDef);
		assertEquals(2, accessTypes.size());
		assertTrue(accessTypes.contains("a"));
		assertTrue(accessTypes.contains("d"));
		
		accessTypes = _validator.getAllResourceNames(serviceDef);
		assertEquals(4, accessTypes.size());
		assertTrue(accessTypes.contains("b"));
		assertTrue(accessTypes.contains("c"));
	}

	@Test
	public void test_getValidationRegExes() {
		// passing in null service def
		Map<String, String> regExMap = _validator.getValidationRegExes((RangerServiceDef)null);
		assertTrue(regExMap.isEmpty());
		// that has null or empty access type def
		RangerServiceDef serviceDef = mock(RangerServiceDef.class);
		when(serviceDef.getResources()).thenReturn(null);
		regExMap = _validator.getValidationRegExes(serviceDef);
		assertTrue(regExMap.isEmpty());

		List<RangerResourceDef> resourceDefs = new ArrayList<RangerResourceDef>();
		when(serviceDef.getResources()).thenReturn(resourceDefs);
		regExMap = _validator.getValidationRegExes(serviceDef);
		assertTrue(regExMap.isEmpty());
		
		// having null accesstypedefs
		resourceDefs.add(null);
		regExMap = _validator.getValidationRegExes(serviceDef);
		assertTrue(regExMap.isEmpty());
		
		// access type defs with null empty blank names are skipped, spaces within names are preserved
		String[][] data = {
				{ "a", null },     // null-regex
				null,              // this should put a null element in the resource def!
				{ "b", "regex1" }, // valid
				{ "c", "" },       // empty regex
				{ "d", "regex2" }, // valid
				{ "e", "   " },    // blank regex
				{ "f", "regex3" }, // all good
		};
		resourceDefs.addAll(_utils.createResourceDefsWithRegEx(data));
		regExMap = _validator.getValidationRegExes(serviceDef);
		assertEquals(3, regExMap.size());
		assertEquals("regex1", regExMap.get("b"));
		assertEquals("regex2", regExMap.get("d"));
		assertEquals("regex3", regExMap.get("f"));
	}

	private RangerValidatorForTest _validator;
	private ServiceStore _store;
	private ValidationTestUtils _utils = new ValidationTestUtils();
}
