package com.github.billberg.api;

import java.util.List;
import java.util.Map;

/**
 * 查询接口类
 * @author Administrator
 *
 */
public class QueryService {
	
	/**
	 * 根据用户姓名查询用户
	 * @param fullName 用户姓名
	 * @return 用户ID列表
	 */
	public static List<String> queryUsers(String fullName) {
		return null;
	}

	/**
	 * 根据用户姓名查询用户
	 * @param firstName 用户名
	 * @param lastName 用户姓
	 * @return 符合条件的用户集合
	 */
	public static Map<String,Object> queryUsers(String firstName, String lastName) {
		return null;
	}

}
