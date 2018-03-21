/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 *
 * 修订记录:
 * zhouxi@yiji.com 2015-09-15 15:41 创建
 *
 */
package com.yiji.boot.boss;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yiji.boot.core.Apps;
import com.yjf.common.id.ID;
import com.yjf.common.lang.exception.Exceptions;
import com.yjf.common.util.ToString;
import com.yjf.marmot.user.Item;
import com.yjf.marmot.user.MarmotUser;
import com.yjf.marmot.user.Menu;
import com.yjf.marmot.user.UserAuthInfo;
import com.yjf.permission.facade.api.query.PermissionQueryService;
import com.yjf.permission.facade.api.query.RoleQueryService;
import com.yjf.permission.facade.enums.PermissionStatusEnum;
import com.yjf.permission.facade.enums.PermissionTypeEnum;
import com.yjf.permission.facade.info.PermissionParentInfo;
import com.yjf.permission.facade.info.RoleInfo;
import com.yjf.permission.facade.order.permission.OperatorPermAllOrder;
import com.yjf.permission.facade.order.permission.OperatorPermTreeOrder;
import com.yjf.permission.facade.order.role.BgRoleQueryOrder;
import com.yjf.permission.facade.result.PermissionInfoResult;
import com.yjf.permission.facade.result.PermissionPageListResult;
import com.yjf.permission.facade.result.RoleInfoResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouxi@yiji.com
 */
public class BossPermissionAdapter implements MarmotUser {
	
	private static final Logger logger = LoggerFactory.getLogger(BossPermissionAdapter.class);
	
	@Reference(version = "1.5")
	private PermissionQueryService permissionQueryService;
	
	@Reference(version = "1.5")
	private RoleQueryService roleQueryService;
	
	@Override
	public UserAuthInfo getMarmotUserAuthInfo(String userId, String serverName) {
		OperatorPermAllOrder order = new OperatorPermAllOrder();
		UserAuthInfo userAuthInfo = new UserAuthInfo();
		try {
			logger.info("开始调用远程权限查询,userId={},sysName={}", userId, Apps.getAppName());
			order.setOperatorId(userId);
			order.setGid(ID.newID("CS000000"));
			order.setPartnerId("CS000000000000000000");
			order.setMerchOrderNo("cs-merch");
			order.setSystemFlag(Apps.getAppName());
			PermissionPageListResult pageListResult = permissionQueryService.queryPermissionInfoByOperatorId(order);
			logger.info("查询权限结果:{}", pageListResult);
			BgRoleQueryOrder roleOrder = new BgRoleQueryOrder();
			roleOrder.setOperatorId(userId);
			roleOrder.setGid(ID.newID("CS000000"));
			roleOrder.setPartnerId("CS000000000000000001");
			roleOrder.setMerchOrderNo("cs-merch");
			roleOrder.setStatus(PermissionStatusEnum.NORMAL);
			RoleInfoResult roleInfoResult = roleQueryService.queryBgOperatorRoleInfo(roleOrder);
			logger.info("查询角色结果:{}", roleInfoResult);
			if (roleInfoResult.isSuccess() && roleInfoResult.getRoleInfoList() != null) {
				List<RoleInfo> roleInfoList = roleInfoResult.getRoleInfoList();
				List<String> acRoleName = roleInfoList.stream().filter(r -> r.getRoleName() != null)
					.map(roleInfo -> roleInfo.getRoleName()).collect(Collectors.toCollection(ArrayList<String>::new));
				userAuthInfo.setAcRoleName(acRoleName);
			}
			if (pageListResult.isSuccess() && pageListResult.getParentInfoList() != null) {
				List<String> permissions = pageListResult.getParentInfoList().stream()
					.filter(p -> p.getPermCode() != null).map(p -> p.getPermCode()).collect(Collectors.toList());
				userAuthInfo.setPermissions(permissions);
			}
		} catch (Exception e) {
			logger.error("查询权限角色出错，错误原因：{}", e.getMessage(), e);
			Exceptions.rethrow(e);
		}
		return userAuthInfo;
	}
	
	@Override
	public Menu getMenus(String userId) {
		logger.info("查询权限菜单开始");
		Menu result = new Menu();
		Map<String, Item> menu = new HashMap<>();
		OperatorPermTreeOrder order = new OperatorPermTreeOrder();
		PermissionInfoResult pageListResult;
		try {
			order.setOperatorId(userId);
			order.setGid(ID.newID("CS000000"));
			order.setPartnerId("CS000000000000000000");
			order.setMerchOrderNo("cs-merch");
			order.setPermissionType(PermissionTypeEnum.MENU);
			order.setTopCode("perm_top_node");
			logger.info("调用远程接口，查询权限菜单入参：OperatorPermTreeOrder={}", order);
			pageListResult = permissionQueryService.queryTreePermissionInfoByOperationId(order);
			logger.info("调用远程接口，查询权限菜单结果：pageListResult={}", pageListResult.toString());
			if (pageListResult.isSuccess() && pageListResult.getPermissionParentInfo() != null) {
				PermissionParentInfo permissionParentInfo = pageListResult.getPermissionParentInfo();
				for (PermissionParentInfo permissionInfo : permissionParentInfo.getpList()) {
					Item temp = new Item();
					menuAdapter(temp, permissionInfo);
					menu.put(permissionInfo.getPermName(), temp);
				}
			}
			result.setMenuItems(menu);
			logger.info("调用远程接口，查询权限菜单出参：Menu={}", ToString.toString(result));
		} catch (Exception e) {
			logger.error("查询权限菜单出错，错误原因：{}", e.getMessage(), e);
			Exceptions.rethrow(e);
		}
		return result;
	}
	
	private void menuAdapter(Item desc, PermissionParentInfo parentInfo) {
		desc.setId((int) parentInfo.getId());
		desc.setText(parentInfo.getPermName());
		desc.setValue(parentInfo.getAddress() + parentInfo.getUrl());
		if (parentInfo.getpList().size() == 0 || parentInfo.getpList() == null)
			return;
		else {
			for (PermissionParentInfo permissionInfo : parentInfo.getpList()) {
				Item item = new Item();
				desc.getChildren().add(item);
				menuAdapter(item, permissionInfo);
			}
		}
	}
}
