/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ranger.biz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.ranger.common.ContextUtil;
import org.apache.ranger.common.RESTErrorUtil;
import org.apache.ranger.common.SearchCriteria;
import org.apache.ranger.common.StringUtil;
import org.apache.ranger.common.UserSessionBase;
import org.apache.ranger.db.RangerDaoManager;
import org.apache.ranger.db.XXGroupDao;
import org.apache.ranger.db.XXGroupUserDao;
import org.apache.ranger.db.XXUserDao;
import org.apache.ranger.entity.XXGroup;
import org.apache.ranger.entity.XXUser;
import org.apache.ranger.security.context.RangerContextHolder;
import org.apache.ranger.security.context.RangerSecurityContext;
import org.apache.ranger.service.XGroupService;
import org.apache.ranger.service.XGroupUserService;
import org.apache.ranger.service.XUserService;
import org.apache.ranger.view.VXGroup;
import org.apache.ranger.view.VXGroupList;
import org.apache.ranger.view.VXGroupUser;
import org.apache.ranger.view.VXGroupUserList;
import org.apache.ranger.view.VXPortalUser;
import org.apache.ranger.view.VXUser;
import org.apache.ranger.view.VXUserGroupInfo;
import org.apache.ranger.view.VXUserList;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestXUserMgr {

	private static Long userId = 10L;
	
	private static Integer emptyValue;

	@InjectMocks
	XUserMgr xUserMgr = new XUserMgr();

	@Mock
	XGroupService xGroupService;

	@Mock
	RangerDaoManager daoManager;

	@Mock
	RESTErrorUtil restErrorUtil;

	@Mock
	XGroupUserService xGroupUserService;

	@Mock
	StringUtil stringUtil;

	@Mock
	RangerBizUtil msBizUtil;

	@Mock
	UserMgr userMgr;

	@Mock
	XUserService xUserService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	public void setup() {
		RangerSecurityContext context = new RangerSecurityContext();
		context.setUserSession(new UserSessionBase());
		RangerContextHolder.setSecurityContext(context);
		UserSessionBase currentUserSession = ContextUtil
				.getCurrentUserSession();
		currentUserSession.setUserAdmin(true);
	}

	private VXUser vxUser() {
		Collection<String> userRoleList = new ArrayList<String>();
		userRoleList.add("ROLE_USER");
		Collection<String> groupNameList = new ArrayList<String>();
		groupNameList.add("Grp2");
		VXUser vxUser = new VXUser();
		vxUser.setId(userId);
		vxUser.setDescription("group test working");
		vxUser.setName("grouptest");
		vxUser.setUserRoleList(userRoleList);
		vxUser.setGroupNameList(groupNameList);
		return vxUser;
	}

	@Test
	public void test11CreateXUser() {

		setup();
		VXUser vxUser = vxUser();

		VXPortalUser vXPortalUser = new VXPortalUser();

		Mockito.when(userMgr.createDefaultAccountUser(vXPortalUser))
				.thenReturn(vXPortalUser);
		Mockito.when(xUserService.createResource(vxUser)).thenReturn(vxUser);

		VXUser dbUser = xUserMgr.createXUser(vxUser);
		Assert.assertNotNull(dbUser);
		userId = dbUser.getId();
		Assert.assertEquals(userId, dbUser.getId());
		Assert.assertEquals(dbUser.getDescription(), vxUser.getDescription());
		Assert.assertEquals(dbUser.getName(), vxUser.getName());
		Assert.assertEquals(dbUser.getUserRoleList(), vxUser.getUserRoleList());
		Assert.assertEquals(dbUser.getGroupNameList(),
				vxUser.getGroupNameList());
	
		Mockito.verify(xUserService).createResource(vxUser);

		Mockito.when(xUserService.readResourceWithOutLogin(userId)).thenReturn(
				vxUser);
		VXUser dbvxUser = xUserMgr.getXUser(userId);
		Assert.assertNotNull(dbvxUser);
		Assert.assertEquals(userId, dbvxUser.getId());
		Assert.assertEquals(dbvxUser.getDescription(), vxUser.getDescription());
		Assert.assertEquals(dbvxUser.getName(), vxUser.getName());
		Assert.assertEquals(dbvxUser.getUserRoleList(),
				vxUser.getUserRoleList());
		Assert.assertEquals(dbvxUser.getGroupNameList(),
				vxUser.getGroupNameList());
		Mockito.verify(xUserService).readResourceWithOutLogin(userId);
	}

	@Test
	public void test12UpdateXUser() {
		VXUser vxUser = vxUser();
		Mockito.when(xUserService.updateResource(vxUser)).thenReturn(vxUser);

		VXUser dbvxUser = xUserMgr.updateXUser(vxUser);
		Assert.assertNotNull(dbvxUser);
		Assert.assertEquals(dbvxUser.getId(), vxUser.getId());
		Assert.assertEquals(dbvxUser.getDescription(), vxUser.getDescription());
		Assert.assertEquals(dbvxUser.getName(), vxUser.getName());
		Mockito.verify(xUserService).updateResource(vxUser);
	}

	@Test
	public void test13ModifyUserVisibilitySetOne() {
		XXUserDao xxUserDao = Mockito.mock(XXUserDao.class);
		XXUser xxUser = Mockito.mock(XXUser.class);
		VXUser vxUser = vxUser();
		
		Mockito.when(xUserService.updateResource(vxUser)).thenReturn(vxUser);		
		HashMap<Long, Integer> visibilityMap = new HashMap<Long, Integer>();
		Integer value = 1;
		visibilityMap.put(userId, value);
		
		Mockito.when(daoManager.getXXUser()).thenReturn(xxUserDao);
		Mockito.when(xxUserDao.getById(userId)).thenReturn(xxUser);
		Mockito.when(xUserService.populateViewBean(xxUser)).thenReturn(vxUser);
		
		xUserMgr.modifyUserVisibility(visibilityMap);
		Assert.assertEquals(value, vxUser.getIsVisible());
		Assert.assertEquals(userId, vxUser.getId());
		Mockito.verify(xUserService).updateResource(vxUser);
		Mockito.verify(daoManager).getXXUser();
		Mockito.verify(xUserService).populateViewBean(xxUser);
	}
	
	@Test
	public void test14ModifyUserVisibilitySetZero() {
		XXUserDao xxUserDao = Mockito.mock(XXUserDao.class);
		XXUser xxUser = Mockito.mock(XXUser.class);
		VXUser vxUser = vxUser();
		
		Mockito.when(xUserService.updateResource(vxUser)).thenReturn(vxUser);		
		HashMap<Long, Integer> visibilityMap = new HashMap<Long, Integer>();
		Integer value = 0;
		visibilityMap.put(userId, value);
		
		Mockito.when(daoManager.getXXUser()).thenReturn(xxUserDao);
		Mockito.when(xxUserDao.getById(userId)).thenReturn(xxUser);
		Mockito.when(xUserService.populateViewBean(xxUser)).thenReturn(vxUser);
		
		xUserMgr.modifyUserVisibility(visibilityMap);		
		Assert.assertEquals(value, vxUser.getIsVisible());
		Assert.assertEquals(userId, vxUser.getId());
		Mockito.verify(xUserService).updateResource(vxUser);
		Mockito.verify(daoManager).getXXUser();
		Mockito.verify(xUserService).populateViewBean(xxUser);
	}
	
	@Test
	public void test15ModifyUserVisibilitySetEmpty() {
		XXUserDao xxUserDao = Mockito.mock(XXUserDao.class);
		XXUser xxUser = Mockito.mock(XXUser.class);
		VXUser vxUser = vxUser();
		
		Mockito.when(xUserService.updateResource(vxUser)).thenReturn(vxUser);		
		HashMap<Long, Integer> visibilityMap = new HashMap<Long, Integer>();
		visibilityMap.put(userId, emptyValue);
		
		Mockito.when(daoManager.getXXUser()).thenReturn(xxUserDao);
		Mockito.when(xxUserDao.getById(userId)).thenReturn(xxUser);
		Mockito.when(xUserService.populateViewBean(xxUser)).thenReturn(vxUser);
		
		xUserMgr.modifyUserVisibility(visibilityMap);		
		Assert.assertEquals(emptyValue, vxUser.getIsVisible());
		Assert.assertEquals(userId, vxUser.getId());
		Mockito.verify(xUserService).updateResource(vxUser);
		Mockito.verify(daoManager).getXXUser();
		Mockito.verify(xUserService).populateViewBean(xxUser);
	}

	@Test
	public void test16CreateXGroup() {
		setup();
		VXGroup vXGroup = new VXGroup();
		vXGroup.setId(userId);
		vXGroup.setDescription("group test");
		vXGroup.setName("grouptest");

		Mockito.when(xGroupService.createResource(vXGroup)).thenReturn(vXGroup);

		VXGroup dbXGroup = xUserMgr.createXGroup(vXGroup);
		Assert.assertNotNull(dbXGroup);
		userId = dbXGroup.getId();
		Assert.assertEquals(userId, dbXGroup.getId());
		Assert.assertEquals(vXGroup.getDescription(), dbXGroup.getDescription());
		Assert.assertEquals(vXGroup.getName(), dbXGroup.getName());
		Mockito.verify(xGroupService).createResource(vXGroup);

		Mockito.when(xGroupService.readResourceWithOutLogin(userId))
				.thenReturn(vXGroup);
		VXGroup dbxGroup = xUserMgr.getXGroup(userId);
		Assert.assertNotNull(dbXGroup);
		Assert.assertEquals(userId, dbxGroup.getId());
		Assert.assertEquals(dbXGroup.getDescription(),
				dbxGroup.getDescription());
		Assert.assertEquals(dbXGroup.getName(), dbxGroup.getName());
		Mockito.verify(xGroupService).readResourceWithOutLogin(userId);	
	}

	@Test
	public void test17UpdateXGroup() {
		XXGroupDao xxGroupDao = Mockito.mock(XXGroupDao.class);
		setup();
		VXGroup vXGroup = new VXGroup();
		vXGroup.setId(userId);
		vXGroup.setDescription("group test");
		vXGroup.setName("grouptest");

		XXGroup xxGroup = new XXGroup();
		Mockito.when(daoManager.getXXGroup()).thenReturn(xxGroupDao);
		Mockito.when(xxGroupDao.getById(vXGroup.getId())).thenReturn(xxGroup);
		Mockito.when(xGroupService.updateResource(vXGroup)).thenReturn(vXGroup);

		VXGroup dbvxGroup = xUserMgr.updateXGroup(vXGroup);
		Assert.assertNotNull(dbvxGroup);
		userId = dbvxGroup.getId();
		Assert.assertEquals(userId, dbvxGroup.getId());
		Assert.assertEquals(vXGroup.getDescription(),
				dbvxGroup.getDescription());
		Assert.assertEquals(vXGroup.getName(), dbvxGroup.getName());
		Mockito.verify(daoManager).getXXGroup();	
		Mockito.verify(xGroupService).updateResource(vXGroup);	
	}

	@Test
	public void test18ModifyGroupsVisibilitySetOne() {
		XXGroupDao xxGroupDao = Mockito.mock(XXGroupDao.class);		
		VXGroup vXGroup = new VXGroup();
		vXGroup.setId(userId);
		vXGroup.setDescription("group test");
		vXGroup.setName("grouptest");

		XXGroup xxGroup = new XXGroup();		
		HashMap<Long, Integer> groupVisibilityMap = new HashMap<Long, Integer>();
		Integer value = 1;
		groupVisibilityMap.put(userId, value);
		
		Mockito.when(daoManager.getXXGroup()).thenReturn(xxGroupDao);
		Mockito.when(xxGroupDao.getById(vXGroup.getId())).thenReturn(xxGroup);
		Mockito.when(xGroupService.populateViewBean(xxGroup)).thenReturn(vXGroup);
		Mockito.when(xGroupService.updateResource(vXGroup)).thenReturn(vXGroup);
	
		xUserMgr.modifyGroupsVisibility(groupVisibilityMap);
		Assert.assertEquals(value, vXGroup.getIsVisible());
		Assert.assertEquals(userId, vXGroup.getId());		
		Mockito.verify(daoManager).getXXGroup();
		Mockito.verify(xGroupService).populateViewBean(xxGroup);
		Mockito.verify(xGroupService).updateResource(vXGroup);
	}
	
	@Test
	public void test19ModifyGroupsVisibilitySetZero() {
		XXGroupDao xxGroupDao = Mockito.mock(XXGroupDao.class);		
		VXGroup vXGroup = new VXGroup();
		vXGroup.setId(userId);
		vXGroup.setDescription("group test");
		vXGroup.setName("grouptest");

		XXGroup xxGroup = new XXGroup();		
		HashMap<Long, Integer> groupVisibilityMap = new HashMap<Long, Integer>();
		Integer value = 0;
		groupVisibilityMap.put(userId, value);
		
		Mockito.when(daoManager.getXXGroup()).thenReturn(xxGroupDao);
		Mockito.when(xxGroupDao.getById(vXGroup.getId())).thenReturn(xxGroup);
		Mockito.when(xGroupService.populateViewBean(xxGroup)).thenReturn(vXGroup);
		Mockito.when(xGroupService.updateResource(vXGroup)).thenReturn(vXGroup);
	
		xUserMgr.modifyGroupsVisibility(groupVisibilityMap);
		Assert.assertEquals(value, vXGroup.getIsVisible());
		Assert.assertEquals(userId, vXGroup.getId());		
		Mockito.verify(daoManager).getXXGroup();
		Mockito.verify(xGroupService).populateViewBean(xxGroup);
		Mockito.verify(xGroupService).updateResource(vXGroup);
	}
	
	@Test
	public void test20ModifyGroupsVisibilitySetEmpty() {
		XXGroupDao xxGroupDao = Mockito.mock(XXGroupDao.class);		
		VXGroup vXGroup = new VXGroup();
		vXGroup.setId(userId);
		vXGroup.setDescription("group test");
		vXGroup.setName("grouptest");

		XXGroup xxGroup = new XXGroup();		
		HashMap<Long, Integer> groupVisibilityMap = new HashMap<Long, Integer>();
		groupVisibilityMap.put(userId, emptyValue);
		
		Mockito.when(daoManager.getXXGroup()).thenReturn(xxGroupDao);
		Mockito.when(xxGroupDao.getById(vXGroup.getId())).thenReturn(xxGroup);
		Mockito.when(xGroupService.populateViewBean(xxGroup)).thenReturn(vXGroup);
		Mockito.when(xGroupService.updateResource(vXGroup)).thenReturn(vXGroup);
	
		xUserMgr.modifyGroupsVisibility(groupVisibilityMap);
		Assert.assertEquals(emptyValue, vXGroup.getIsVisible());
		Assert.assertEquals(userId, vXGroup.getId());		
		Mockito.verify(daoManager).getXXGroup();
		Mockito.verify(xGroupService).populateViewBean(xxGroup);
		Mockito.verify(xGroupService).updateResource(vXGroup);
	}
	
	@Test
	public void test21createXGroupUser() {
		VXGroupUser vxGroupUser = new VXGroupUser();
		vxGroupUser.setId(userId);
		vxGroupUser.setName("group user test");
		vxGroupUser.setOwner("Admin");
		vxGroupUser.setUserId(userId);
		vxGroupUser.setUpdatedBy("User");

		Mockito.when(
				xGroupUserService.createXGroupUserWithOutLogin(vxGroupUser))
				.thenReturn(vxGroupUser);

		VXGroupUser dbVXGroupUser = xUserMgr.createXGroupUser(vxGroupUser);
		Assert.assertNotNull(dbVXGroupUser);
		userId = dbVXGroupUser.getId();
		Assert.assertEquals(userId, dbVXGroupUser.getId());
		Assert.assertEquals(dbVXGroupUser.getOwner(), vxGroupUser.getOwner());
		Assert.assertEquals(dbVXGroupUser.getName(), vxGroupUser.getName());
		Assert.assertEquals(dbVXGroupUser.getUserId(), vxGroupUser.getUserId());
		Assert.assertEquals(dbVXGroupUser.getUpdatedBy(),
				vxGroupUser.getUpdatedBy());
		Mockito.verify(xGroupUserService).createXGroupUserWithOutLogin(vxGroupUser);	

		Mockito.when(xGroupUserService.readResourceWithOutLogin(userId))
				.thenReturn(vxGroupUser);
		VXGroup vXGroup = new VXGroup();
				vXGroup.setId(userId);
				vXGroup.setDescription("group test");
				vXGroup.setName("grouptest");

		VXGroupUser dbvxGroupUser = xUserMgr.getXGroupUser(userId);
		Assert.assertNotNull(dbvxGroupUser);
		userId = dbvxGroupUser.getId();
		Assert.assertEquals(userId, dbvxGroupUser.getId());
		Assert.assertEquals(dbvxGroupUser.getOwner(), vxGroupUser.getOwner());
		Assert.assertEquals(dbvxGroupUser.getName(), vxGroupUser.getName());
		Assert.assertEquals(dbvxGroupUser.getUserId(), vxGroupUser.getUserId());
		Assert.assertEquals(dbvxGroupUser.getUpdatedBy(),
				vxGroupUser.getUpdatedBy());
		Mockito.verify(xGroupUserService).readResourceWithOutLogin(userId);	
	}

	@Test
	public void test22GetXUserGroups() {
		VXGroupList dbVXGroupList = xUserMgr.getXUserGroups(userId);
		Assert.assertNotNull(dbVXGroupList);
	}

	@Test
	public void test23GetXGroupUsers() {
		VXUserList dbVXUserList = xUserMgr.getXGroupUsers(userId);VXGroup vXGroup = new VXGroup();
		vXGroup.setId(userId);
		vXGroup.setDescription("group test");
		vXGroup.setName("grouptest");
		Assert.assertNotNull(dbVXUserList);
	}

	@Test
	public void test24GetXUserByUserName() {
		VXUser vxUser = vxUser();
		String userName = "test";

		Mockito.when(xUserService.getXUserByUserName(userName)).thenReturn(
				vxUser);

		VXUser dbVXUser = xUserMgr.getXUserByUserName(userName);
		Assert.assertNotNull(dbVXUser);
		userId = dbVXUser.getId();
		Assert.assertEquals(userId, dbVXUser.getId());
		Assert.assertEquals(dbVXUser.getName(), vxUser.getName());
		Assert.assertEquals(dbVXUser.getOwner(), vxUser.getOwner());
		Mockito.verify(xUserService).getXUserByUserName(userName);	
	}

	@Test
	public void test25CreateXUserWithOutLogin(){
		VXUser vxUser = vxUser();
	
		Mockito.when(xUserService.createXUserWithOutLogin(vxUser))
		.thenReturn(vxUser);
		
		VXUser dbUser = xUserMgr.createXUserWithOutLogin(vxUser);
		Assert.assertNotNull(dbUser);
		userId = dbUser.getId();
		Assert.assertEquals(userId, dbUser.getId());
		Assert.assertEquals(dbUser.getDescription(), vxUser.getDescription());
		Assert.assertEquals(dbUser.getName(), vxUser.getName());
		Assert.assertEquals(dbUser.getUserRoleList(), vxUser.getUserRoleList());
		Assert.assertEquals(dbUser.getGroupNameList(),
				vxUser.getGroupNameList());
		Mockito.verify(xUserService).createXUserWithOutLogin(vxUser);	
	}
	
	@Test
	public void test26CreateXGroupWithoutLogin(){

		VXGroup vXGroup = new VXGroup();
		vXGroup.setId(userId);
		vXGroup.setDescription("group test");
		vXGroup.setName("grouptest");
	
		Mockito.when(xGroupService.createXGroupWithOutLogin(vXGroup))
		.thenReturn(vXGroup);
		
	    VXGroup dbVXGroup = xUserMgr.createXGroupWithoutLogin(vXGroup);
	    Assert.assertNotNull(dbVXGroup);
		userId = dbVXGroup.getId();
		Assert.assertEquals(userId, dbVXGroup.getId());
		Assert.assertEquals(vXGroup.getDescription(), dbVXGroup.getDescription());
		Assert.assertEquals(vXGroup.getName(), dbVXGroup.getName());
		Mockito.verify(xGroupService).createXGroupWithOutLogin(vXGroup);	
	}
	
	@Test
	public void test27DeleteXGroup() {
		XXGroupDao xxGroupDao = Mockito.mock(XXGroupDao.class);

		VXGroupUserList vxGroupUserList = new VXGroupUserList();
		XXGroup xxGroup = new XXGroup();
		boolean force = true;
		Mockito.when(
				xGroupUserService.searchXGroupUsers((SearchCriteria) Mockito
						.anyObject())).thenReturn(vxGroupUserList);

		Mockito.when(daoManager.getXXGroup()).thenReturn(xxGroupDao);
		Mockito.when(xxGroupDao.getById(userId)).thenReturn(xxGroup);

		xUserMgr.deleteXGroup(userId, force);
		Mockito.verify(xGroupUserService).searchXGroupUsers((SearchCriteria) Mockito.anyObject());	
	}

	@Test
	public void test28DeleteXUser() {
		XXGroupUserDao xxGroupDao = Mockito.mock(XXGroupUserDao.class);
		XXUserDao xxUserDao = Mockito.mock(XXUserDao.class);
		VXGroupUserList vxGroupUserList = new VXGroupUserList();
		boolean force = true;

		Mockito.when(
				xGroupUserService.searchXGroupUsers((SearchCriteria) Mockito
						.anyObject())).thenReturn(vxGroupUserList);
		Mockito.when(daoManager.getXXGroupUser()).thenReturn(xxGroupDao);
		Mockito.when(daoManager.getXXUser()).thenReturn(xxUserDao);
		Mockito.when(xxUserDao.remove(userId)).thenReturn(true);

		xUserMgr.deleteXUser(userId, force);
		Mockito.verify(xGroupUserService).searchXGroupUsers((SearchCriteria) Mockito
				.anyObject());
		Mockito.verify(daoManager).getXXGroupUser();
		Mockito.verify(daoManager).getXXUser();
	}

	@Test
	public void test29deleteXGroupAndXUser() {
		VXUser vxUser = vxUser();
		VXGroup vxGroup = new VXGroup();
		VXGroupUserList vxGroupUserList = new VXGroupUserList();

		String groupName = "Grp2";
		String userName = "test";

		Mockito.when(xGroupService.getGroupByGroupName(Mockito.anyString()))
				.thenReturn(vxGroup);
		Mockito.when(xUserService.getXUserByUserName(Mockito.anyString()))
				.thenReturn(vxUser);
		Mockito.when(
				xGroupUserService.searchXGroupUsers((SearchCriteria) Mockito
						.anyObject())).thenReturn(vxGroupUserList);

		xUserMgr.deleteXGroupAndXUser(groupName, userName);
		Mockito.verify(xGroupService).getGroupByGroupName(Mockito.anyString());
		Mockito.verify(xUserService).getXUserByUserName(Mockito.anyString());
		Mockito.verify(xGroupUserService).searchXGroupUsers((SearchCriteria) Mockito
				.anyObject());
	}
	
	@Test
	public void test30CreateVXUserGroupInfo(){
	
		VXUserGroupInfo vXUserGroupInfo = new VXUserGroupInfo();
		VXUser vXUser = new VXUser();
		vXUser.setName("user1");
		vXUser.setDescription("testuser1 -added for unit testing");
		
		List<VXGroupUser> vXGroupUserList = new ArrayList<VXGroupUser>();
		List<VXGroup> vXGroupList = new ArrayList<VXGroup>();
		
		final VXGroup vXGroup1 = new VXGroup();
		vXGroup1.setName("users");
		vXGroup1.setDescription("users -added for unit testing");
		vXGroupList.add(vXGroup1);
		
		VXGroupUser vXGroupUser1 = new VXGroupUser();
		vXGroupUser1.setName("users");
		vXGroupUserList.add(vXGroupUser1);
		
		final VXGroup vXGroup2 = new VXGroup();
		vXGroup2.setName("user1");
		vXGroup2.setDescription("user1 -added for unit testing");
		vXGroupList.add(vXGroup2);
		
		VXGroupUser vXGroupUser2 = new VXGroupUser();
		vXGroupUser2.setName("user1");
		vXGroupUserList.add(vXGroupUser2);
		
		vXUserGroupInfo.setXuserInfo(vXUser);
		vXUserGroupInfo.setXgroupInfo(vXGroupList);
		
		Mockito.when(xUserService.createXUserWithOutLogin(vXUser)).thenReturn(vXUser);
		Mockito.when(xGroupService.createXGroupWithOutLogin(vXGroup1)).thenReturn(vXGroup1);
		Mockito.when(xGroupService.createXGroupWithOutLogin(vXGroup2)).thenReturn(vXGroup2);
		Mockito.when(xGroupUserService.createXGroupUserWithOutLogin(vXGroupUser1)).thenReturn(vXGroupUser1);
		Mockito.when(xGroupUserService.createXGroupUserWithOutLogin(vXGroupUser2)).thenReturn(vXGroupUser2);
		
		VXUserGroupInfo vxUserGroupTest = xUserMgr.createXUserGroupFromMap(vXUserGroupInfo);
		Assert.assertEquals("user1", vxUserGroupTest
				.getXuserInfo()
				.getName());
		List<VXGroup> result = vxUserGroupTest.getXgroupInfo();
		List<VXGroup> expected = new ArrayList<VXGroup>();
		expected.add(vXGroup1);
		expected.add(vXGroup2);
		Assert.assertTrue(result.containsAll(expected));
	}	
}