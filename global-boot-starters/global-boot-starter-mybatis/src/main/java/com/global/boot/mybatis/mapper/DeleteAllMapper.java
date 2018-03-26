/*
 * www.yiji.com Inc.
 * Copyright (c) 2017 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2017-01-13 11:30 创建
 */
package com.global.boot.mybatis.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.provider.base.BaseDeleteProvider;

import java.io.Serializable;
import java.util.Set;

/**
 * @author qiubo@yiji.com
 */
public interface DeleteAllMapper<T> {
	/**
	 * 删除表中所有数据
	 */
	@DeleteProvider(type = DeleteAllProvider.class, method = "dynamicSQL")
	void deleteAll();
	
	/**
	 * 通过主键集合删除数据
	 * @param ids
	 */
	@DeleteProvider(type = DeleteAllProvider.class, method = "dynamicSQL")
	void deleteByPrimaryKeys(Serializable... ids);
	
	class DeleteAllProvider extends BaseDeleteProvider {
		public DeleteAllProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
			super(mapperClass, mapperHelper);
		}
		
		public String deleteAll(MappedStatement ms) {
			Class<?> entityClass = getEntityClass(ms);
			StringBuilder sql = new StringBuilder();
			sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
			return sql.toString();
		}
		
		public String deleteByPrimaryKeys(MappedStatement ms) {
			Class<?> entityClass = getEntityClass(ms);
			StringBuilder sql = new StringBuilder();
			sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
			Set<EntityColumn> columnList = EntityHelper.getPKColumns(entityClass);
			if (columnList == null || columnList.size() != 1) {
				throw new UnsupportedOperationException("deleteByPrimaryKeys只支持唯一主键");
			}
			String pkName = null;
			for (EntityColumn entityColumn : columnList) {
				 pkName=entityColumn.getColumn();
			}
			sql.append(" WHERE "+pkName+" IN");
			sql.append(
				"<foreach item=\"item\" index=\"index\" collection=\"array\" open=\"(\" separator=\",\" close=\")\">  \n"
						+ " #{item}  \n" + "</foreach>  ");
			return sql.toString();
		}
	}
}
