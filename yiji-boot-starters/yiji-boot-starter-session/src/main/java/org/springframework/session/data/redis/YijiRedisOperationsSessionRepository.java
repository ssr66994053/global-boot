/*
 * www.yiji.com Inc.
 * Copyright (c) 2016 All Rights Reserved
 */

/*
 * 修订记录:
 * qiubo@yiji.com 2016-05-03 17:25 创建
 */
package org.springframework.session.data.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.yiji.boot.session.LoginChecker.PRFIX;

/**
 * 请原谅我又copy 代码,确实没有办法扩展
 * @author qiubo@yiji.com
 */
public class YijiRedisOperationsSessionRepository
													implements
													SessionRepository<YijiRedisOperationsSessionRepository.RedisSession> {
	/**
	 * The prefix for each key of the Redis Hash representing a single session. The suffix is the unique session id.
	 */
	static final String BOUNDED_HASH_KEY_PREFIX = "spring:session:sessions:";
	
	/**
	 * The key in the Hash representing {@link org.springframework.session.ExpiringSession#getCreationTime()}
	 */
	static final String CREATION_TIME_ATTR = "creationTime";
	
	/**
	 * The key in the Hash representing
	 * {@link org.springframework.session.ExpiringSession#getMaxInactiveIntervalInSeconds()}
	 */
	static final String MAX_INACTIVE_ATTR = "maxInactiveInterval";
	
	/**
	 * The key in the Hash representing {@link org.springframework.session.ExpiringSession#getLastAccessedTime()}
	 */
	static final String LAST_ACCESSED_ATTR = "lastAccessedTime";
	
	/**
	 * The prefix of the key for used for session attributes. The suffix is the name of the session attribute. For
	 * example, if the session contained an attribute named attributeName, then there would be an entry in the hash
	 * named sessionAttr:attributeName that mapped to its value.
	 */
	static final String SESSION_ATTR_PREFIX = "sessionAttr:";
	
	private final RedisOperations<String, ExpiringSession> sessionRedisOperations;
	
	private final RedisSessionExpirationPolicy expirationPolicy;
	
	/**
	 * If non-null, this value is used to override the default value for
	 * {@link RedisSession#setMaxInactiveIntervalInSeconds(int)}.
	 */
	private Integer defaultMaxInactiveInterval;
	
	/**
	 * Allows creating an instance and uses a default {@link RedisOperations} for both managing the session and the
	 * expirations.
	 *
	 * @param redisConnectionFactory the {@link RedisConnectionFactory} to use.
	 */
	@SuppressWarnings("unchecked")
	public YijiRedisOperationsSessionRepository(RedisConnectionFactory redisConnectionFactory) {
		this(createDefaultTemplate(redisConnectionFactory));
	}
	
	/**
	 * Creates a new instance. For an example, refer to the class level javadoc.
	 *
	 * @param sessionRedisOperations The {@link RedisOperations} to use for managing the sessions. Cannot be null.
	 */
	public YijiRedisOperationsSessionRepository(RedisOperations<String, ExpiringSession> sessionRedisOperations) {
		Assert.notNull(sessionRedisOperations, "sessionRedisOperations cannot be null");
		this.sessionRedisOperations = sessionRedisOperations;
		this.expirationPolicy = new RedisSessionExpirationPolicy(sessionRedisOperations);
	}
	
	/**
	 * Sets the maximum inactive interval in seconds between requests before newly created sessions will be invalidated.
	 * A negative time indicates that the session will never timeout. The default is 1800 (30 minutes).
	 *
	 * @param defaultMaxInactiveInterval the number of seconds that the {@link Session} should be kept alive between
	 * client requests.
	 */
	public void setDefaultMaxInactiveInterval(int defaultMaxInactiveInterval) {
		this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
	}
	
	public void save(RedisSession session) {
		session.saveDelta();
		//保存session时,更新防多会话key
		String userName = (String) session.getAttribute(PRFIX);
		if (userName != null) {
			sessionRedisOperations.boundValueOps(PRFIX + userName).expire(defaultMaxInactiveInterval, TimeUnit.SECONDS);
		}
	}
	
	@Scheduled(cron = "0 * * * * *")
	public void cleanupExpiredSessions() {
		this.expirationPolicy.cleanExpiredSessions();
	}
	
	public RedisSession getSession(String id) {
		return getSession(id, false);
	}
	
	/**
	 *
	 * @param id the session id
	 * @param allowExpired if true, will also include expired sessions that have not been deleted. If false, will ensure
	 * expired sessions are not returned.
	 * @return
	 */
	private RedisSession getSession(String id, boolean allowExpired) {
		Map<Object, Object> entries = getSessionBoundHashOperations(id).entries();
		if (entries.isEmpty()) {
			return null;
		}
		MapSession loaded = new MapSession();
		loaded.setId(id);
		for (Map.Entry<Object, Object> entry : entries.entrySet()) {
			String key = (String) entry.getKey();
			if (CREATION_TIME_ATTR.equals(key)) {
				loaded.setCreationTime((Long) entry.getValue());
			} else if (MAX_INACTIVE_ATTR.equals(key)) {
				loaded.setMaxInactiveIntervalInSeconds((Integer) entry.getValue());
			} else if (LAST_ACCESSED_ATTR.equals(key)) {
				loaded.setLastAccessedTime((Long) entry.getValue());
			} else if (key.startsWith(SESSION_ATTR_PREFIX)) {
				loaded.setAttribute(key.substring(SESSION_ATTR_PREFIX.length()), entry.getValue());
			}
		}
		if (!allowExpired && loaded.isExpired()) {
			return null;
		}
		RedisSession result = new RedisSession(loaded);
		result.originalLastAccessTime = loaded.getLastAccessedTime()
										+ TimeUnit.SECONDS.toMillis(loaded.getMaxInactiveIntervalInSeconds());
		result.setLastAccessedTime(System.currentTimeMillis());
		return result;
	}
	
	public void delete(String sessionId) {
		ExpiringSession session = getSession(sessionId, true);
		if (session == null) {
			return;
		}
		
		String key = getKey(sessionId);
		expirationPolicy.onDelete(session);
		
		// always delete they key since session may be null if just expired
		this.sessionRedisOperations.delete(key);
		
		//删除时,同时删除防多会话登录key
		String userName = session.getAttribute(PRFIX);
		if (userName != null) {
			sessionRedisOperations.delete(PRFIX + userName);
		}
	}
	
	public RedisSession createSession() {
		RedisSession redisSession = new RedisSession();
		if (defaultMaxInactiveInterval != null) {
			redisSession.setMaxInactiveIntervalInSeconds(defaultMaxInactiveInterval);
		}
		return redisSession;
	}
	
	/**
	 * Gets the Hash key for this session by prefixing it appropriately.
	 *
	 * @param sessionId the session id
	 * @return the Hash key for this session by prefixing it appropriately.
	 */
	static String getKey(String sessionId) {
		return BOUNDED_HASH_KEY_PREFIX + sessionId;
	}
	
	/**
	 * Gets the key for the specified session attribute
	 *
	 * @param attributeName
	 * @return
	 */
	static String getSessionAttrNameKey(String attributeName) {
		return SESSION_ATTR_PREFIX + attributeName;
	}
	
	/**
	 * Gets the {@link BoundHashOperations} to operate on a {@link Session}
	 * @param sessionId the id of the {@link Session} to work with
	 * @return the {@link BoundHashOperations} to operate on a {@link Session}
	 */
	private BoundHashOperations<String, Object, Object> getSessionBoundHashOperations(String sessionId) {
		String key = getKey(sessionId);
		return this.sessionRedisOperations.boundHashOps(key);
	}
	
	@SuppressWarnings("rawtypes")
	private static RedisTemplate createDefaultTemplate(RedisConnectionFactory connectionFactory) {
		Assert.notNull(connectionFactory, "connectionFactory cannot be null");
		RedisTemplate<String, ExpiringSession> template = new RedisTemplate<String, ExpiringSession>();
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setConnectionFactory(connectionFactory);
		template.afterPropertiesSet();
		return template;
	}
	
	/**
	 * A custom implementation of {@link Session} that uses a {@link MapSession} as the basis for its mapping. It keeps
	 * track of any attributes that have changed. When
	 * {@link org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession#saveDelta()} is
	 * invoked all the attributes that have been changed will be persisted.
	 *
	 * @since 1.0
	 * @author Rob Winch
	 */
	final class RedisSession implements ExpiringSession {
		private final MapSession cached;
		private Long originalLastAccessTime;
		private Map<String, Object> delta = new HashMap<String, Object>();
		
		/**
		 * Creates a new instance ensuring to mark all of the new attributes to be persisted in the next save operation.
		 */
		RedisSession() {
			this(new MapSession());
			delta.put(CREATION_TIME_ATTR, getCreationTime());
			delta.put(MAX_INACTIVE_ATTR, getMaxInactiveIntervalInSeconds());
			delta.put(LAST_ACCESSED_ATTR, getLastAccessedTime());
		}
		
		/**
		 * Creates a new instance from the provided {@link MapSession}
		 *
		 * @param cached the {@MapSession} that represents the persisted session that was retrieved. Cannot
		 * be null.
		 */
		RedisSession(MapSession cached) {
			Assert.notNull("MapSession cannot be null");
			this.cached = cached;
		}
		
		public void setLastAccessedTime(long lastAccessedTime) {
			cached.setLastAccessedTime(lastAccessedTime);
			delta.put(LAST_ACCESSED_ATTR, getLastAccessedTime());
		}
		
		public boolean isExpired() {
			return cached.isExpired();
		}
		
		public long getCreationTime() {
			return cached.getCreationTime();
		}
		
		public String getId() {
			return cached.getId();
		}
		
		public long getLastAccessedTime() {
			return cached.getLastAccessedTime();
		}
		
		public void setMaxInactiveIntervalInSeconds(int interval) {
			cached.setMaxInactiveIntervalInSeconds(interval);
			delta.put(MAX_INACTIVE_ATTR, getMaxInactiveIntervalInSeconds());
		}
		
		public int getMaxInactiveIntervalInSeconds() {
			return cached.getMaxInactiveIntervalInSeconds();
		}
		
		public Object getAttribute(String attributeName) {
			return cached.getAttribute(attributeName);
		}
		
		public Set<String> getAttributeNames() {
			return cached.getAttributeNames();
		}
		
		public void setAttribute(String attributeName, Object attributeValue) {
			cached.setAttribute(attributeName, attributeValue);
			delta.put(getSessionAttrNameKey(attributeName), attributeValue);
		}
		
		public void removeAttribute(String attributeName) {
			cached.removeAttribute(attributeName);
			delta.put(getSessionAttrNameKey(attributeName), null);
		}
		
		/**
		 * Saves any attributes that have been changed and updates the expiration of this session.
		 */
		private void saveDelta() {
			String sessionId = getId();
			getSessionBoundHashOperations(sessionId).putAll(delta);
			delta = new HashMap<String, Object>(delta.size());
			
			expirationPolicy.onExpirationUpdated(originalLastAccessTime, this);
		}
	}
}
