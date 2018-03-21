/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * LiXiang 2016年4月26日 上午11:25:24 创建
 */
package com.yiji.boot.securityframework.index.domain;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yiji.boot.securityframework.IndexProperties;
import com.yiji.common.security.index.IndexManager;
import com.yiji.common.security.index.IndexNotFoundException;
import com.yiji.common.security.index.IndexResult;
import com.yiji.common.security.index.IndexResult.Fragment;
import com.yjf.common.collection.ArrayUtils;
import com.yjf.common.collection.CollectionUtils;
import com.yjf.common.lang.security.IndexUtil;
import com.yjf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.nio.charset.Charset;
import java.util.*;
import java.util.Map.Entry;

/**
 * 使用安全域实现的索引管理器。 该管理器会先从缓存中取值，如果没有再从安全域中取值。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 */
public class DomainIndexManager implements IndexManager {
	/** 日志记录器 */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Charset charset = Charset.forName("UTF-8");
	
	/**
	 * 被代理的目标索引管理器
	 */
	@Reference(version = "1.5", group = "securitydata")
	private IndexManager indexManager;
	
	private RedisTemplate<String, Map<String, String>> redisTemplate;
	private IndexProperties indexProperties;
	
	private ThreadPoolTaskExecutor indexCacheTaskExecutor;
	
	/**
	 * 构造一个 DomainIndexManager 。
	 *
	 * @param redisTemplate 使用的 {@link RedisTemplate} 。
	 */
	public DomainIndexManager(	RedisTemplate<String, Map<String, String>> redisTemplate,
								IndexProperties indexProperties) {
		this.redisTemplate = redisTemplate;
		this.indexProperties = indexProperties;
	}
	
	public void setIndexCacheTaskExecutor(ThreadPoolTaskExecutor indexCacheTaskExecutor) {
		this.indexCacheTaskExecutor = indexCacheTaskExecutor;
	}
	
	/**
	 * 设置使用的编码。
	 *
	 * @param charset 使用的编码。
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}
	
	public IndexResult index(String securityUser, Object index, List<Pair> pairs) {
		if (CollectionUtils.isNotEmpty(pairs)) {
			Iterator<Pair> iterator = pairs.iterator();
			while (iterator.hasNext()) {
				Pair pair = iterator.next();
				if (pair.getOriginal() != null && pair.getOriginal().toString().indexOf('*') >= 0) {
					if (indexProperties.isEnableIndexLog()) {
						logger.info("[{}]的值含有'*'号", pair.getName());
					}
					//带“*”的字符串，不发往安全中心
					iterator.remove();
				}
			}
		}
		if (CollectionUtils.isEmpty(pairs)) {
			if (indexProperties.isEnableIndexLog()) {
				logger.info("没有敏感数据，不发往安全中心！");
			}
			//没有敏感数据就不用发用安全中心了
			return new IndexResult(Charset.forName("utf-8"), null, new ArrayList<Fragment>());
		} else {
			IndexResult result = this.indexManager.index(securityUser, index, pairs);
			if (indexProperties.isEnableLocalCache()) {
				//写入本地缓存
				if (indexCacheTaskExecutor == null) {
					//同步写入缓存
					saveToLocalCache(result, pairs);
				} else {
					//异步写入缓存
					indexCacheTaskExecutor.execute(new Runnable() {
						@Override
						public void run() {
							saveToLocalCache(result, pairs);
						}
					});
				}
			}
			return result;
		}
	}
	
	public IndexResult reverseIndex(String securityUser, Object index, String... names) {
		if (index == null) {
			throw new IndexNotFoundException(null);
		}
		if (this.redisTemplate != null) {
			Map<String, String> entries = null;
			try {
				if (indexProperties.isEnableIndexLog()) {
					logger.info("从缓存中加载索引对应数据......index={}", StringUtils.toString(index));
				}
				entries = this.redisTemplate.opsForValue().get("com.yjf.security.data#" + StringUtils.toString(index));
			} catch (Exception e) {
				//吃掉异常，放弃从缓存中加载原数据
				logger.warn("从缓存中加载索引异常，err={}", e.getMessage());
			}
			
			if (CollectionUtils.isNotEmpty(entries)) {
				if (indexProperties.isEnableIndexLog()) {
					logger.info("从缓存中加载索引对应数据成功，keys={}", entries.keySet());
				}
				List<Fragment> fragments = new ArrayList<Fragment>(entries.size());
				for (Entry<String, String> entry : entries.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (ArrayUtils.isEmpty(names) || ArrayUtils.contains(names, key)) {
						fragments
							.add(new Fragment(this.charset, key, value == null ? null : value.getBytes(this.charset),
								value == null ? null : IndexUtil.getDigest(value).getBytes(this.charset)));
					}
				}
				return new IndexResult(this.charset, StringUtils.toString(index).getBytes(this.charset), fragments);
			}
		}
		if (indexProperties.isEnableIndexLog()) {
			logger.info("==>>>调用安全中心解析数据....");
		}
		IndexResult result = this.indexManager.reverseIndex(securityUser, index, names);
		if (indexProperties.isEnableIndexLog()) {
			logger.info("==>>>调用安全中心解析数据....");
		}
		return result;
	}
	
	public IndexResult index(String securityUser, Object index, Pair pair, Pair... pairs) {
		List<Pair> ps = new ArrayList<Pair>(pairs.length + 1);
		ps.add(pair);
		for (Pair p : pairs) {
			ps.add(p);
		}
		return index(securityUser, index, ps);
	}
	
	private void saveToLocalCache(IndexResult result, List<Pair> pairs) {
		List<Fragment> fragments = result.getFragments();
		if (CollectionUtils.isNotEmpty(fragments)) {
			Map<String, String> entries = new HashMap<String, String>();
			try {
				for (Pair pair : pairs) {
					//pair.getOriginal() 是String类型
					entries.put(pair.getName(), (pair.getOriginal() == null ? null : pair.getOriginal().toString()));
				}
			} catch (Exception e) {
				return;
			}
			
			String index = new String(result.getIndex(), charset);
			if (indexProperties.isEnableIndexLog()) {
				logger.info("索引结果写入本地缓存...index={},names={}", index, entries.keySet());
			}
			try {
				this.redisTemplate.opsForValue().set("com.yjf.security.data#" + index, entries);
			} catch (Exception e) {
				logger.warn("索引结果写入缓存异常，err={}", e.getMessage());
			}
		}
	}
	
}
