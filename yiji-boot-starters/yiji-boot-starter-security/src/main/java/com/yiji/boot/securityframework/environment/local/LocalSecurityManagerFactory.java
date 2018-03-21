/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月8日 下午3:54:36 创建
 */
package com.yiji.boot.securityframework.environment.local;

import com.yiji.common.security.AlgorithmMetaHolder;
import com.yiji.common.security.AlgorithmMetaHolderImpl;
import com.yiji.common.security.ModeDistinguishSecurityManager;
import com.yiji.common.security.SecurityModeFactory;
import com.yiji.common.security.mode.bidirection.*;
import com.yiji.common.security.mode.messagedigest.MessageDigestSecurityBuilderFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <code>local</code>环境创建 {@link ModeDistinguishSecurityManager} 的工厂。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class LocalSecurityManagerFactory {
	
	/**
	 * 创建一个 {@link ModeDistinguishSecurityManager} 的实例。
	 * @param securityConfigRepository 配置仓库。
	 * @return 创建的实例。
	 */
	public ModeDistinguishSecurityManager createSecurityManager(SecurityConfigRepository securityConfigRepository) {
		LocalSecurityManagerImpl securityManager = new LocalSecurityManagerImpl();
		securityManager.setSecurityConfigRepository(securityConfigRepository);
		// 算法元数据持有者
		AlgorithmMetaHolder cipherAlgorithmMetaHolder = new AlgorithmMetaHolderImpl(new RSAAlgorithmMeta(),
			new AESAlgorithmMeta());
		AlgorithmMetaHolder signatureAlgorithmMetaHolder = new AlgorithmMetaHolderImpl(new RSAAlgorithmMeta(),
			new AESAlgorithmMeta());
		// 支持的安全模式的工厂列表
		ConcurrentMap<String, SecurityModeFactory> securityModeFactorys = new ConcurrentHashMap<>();
		// 双向（加密、解密）的“双向加密解密/双向签名/密文签名/直接对比签名验证模式的支持
		LocalBidirectionalCipherSignatureSecurityModeFactory localBidirectionalCipherSignatureSecurityModeFactory = new LocalBidirectionalCipherSignatureSecurityModeFactory();
		localBidirectionalCipherSignatureSecurityModeFactory.setLogOriginal(true);
		localBidirectionalCipherSignatureSecurityModeFactory.setSecurityConfigRepository(securityConfigRepository);
		localBidirectionalCipherSignatureSecurityModeFactory.setCipherAlgorithmMetaHolder(cipherAlgorithmMetaHolder);
		localBidirectionalCipherSignatureSecurityModeFactory
			.setSignatureAlgorithmMetaHolder(signatureAlgorithmMetaHolder);
		securityModeFactorys.put(BidirectionalCipherSignatureSecurityBuilderFactory.MODE,
			localBidirectionalCipherSignatureSecurityModeFactory);
		// 双向（加密、解密）的“双向加密解密/单向签名/原文签名/使用密钥验证模式的支持
		LocalBidirectionalCipherUnidirectionSignatureSecurityModeFactory localBidirectionalCipherUnidirectionSignatureSecurityModeFactory = new LocalBidirectionalCipherUnidirectionSignatureSecurityModeFactory();
		localBidirectionalCipherUnidirectionSignatureSecurityModeFactory.setLogOriginal(true);
		localBidirectionalCipherUnidirectionSignatureSecurityModeFactory
			.setSecurityConfigRepository(securityConfigRepository);
		localBidirectionalCipherUnidirectionSignatureSecurityModeFactory
			.setCipherAlgorithmMetaHolder(cipherAlgorithmMetaHolder);
		localBidirectionalCipherUnidirectionSignatureSecurityModeFactory
			.setSignatureAlgorithmMetaHolder(signatureAlgorithmMetaHolder);
		securityModeFactorys.put(BidirectionalCipherSignatureVerifySecurityBuilderFactory.MODE,
			localBidirectionalCipherUnidirectionSignatureSecurityModeFactory);
		// 双向（加密、解密）的“双向加密解密/对比原文验证”模式的支持
		LocalBidirectionalCipherSecurityModeFactory localBidirectionalCipherSecurityModeFactory = new LocalBidirectionalCipherSecurityModeFactory();
		localBidirectionalCipherSecurityModeFactory.setLogOriginal(true);
		localBidirectionalCipherSecurityModeFactory.setSecurityConfigRepository(securityConfigRepository);
		localBidirectionalCipherSecurityModeFactory.setCipherAlgorithmMetaHolder(cipherAlgorithmMetaHolder);
		localBidirectionalCipherSecurityModeFactory.setSignatureAlgorithmMetaHolder(signatureAlgorithmMetaHolder);
		securityModeFactorys.put(BidirectionalCipherSecurityBuilderFactory.MODE,
			localBidirectionalCipherSecurityModeFactory);
		// 单独消息摘要模式的支持
		LocalMessageDigestSecurityModeFactory localMessageDigestSecurityModeFactory = new LocalMessageDigestSecurityModeFactory();
		localMessageDigestSecurityModeFactory.setLogOriginal(true);
		localMessageDigestSecurityModeFactory.setSecurityConfigRepository(securityConfigRepository);
		securityModeFactorys.put(MessageDigestSecurityBuilderFactory.MODE, localMessageDigestSecurityModeFactory);
		
		securityManager.setSecurityModeFactorys(securityModeFactorys);
		return securityManager;
	}
	
}
