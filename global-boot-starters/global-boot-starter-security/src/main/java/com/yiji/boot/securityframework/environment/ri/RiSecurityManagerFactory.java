/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月8日 下午3:54:36 创建
 */
package com.yiji.boot.securityframework.environment.ri;

import com.yiji.common.security.AlgorithmMetaHolder;
import com.yiji.common.security.AlgorithmMetaHolderImpl;
import com.yiji.common.security.ModeDistinguishSecurityManager;
import com.yiji.common.security.SecurityModeFactory;
import com.yiji.common.security.mode.bidirection.*;
import com.yiji.common.security.mode.messagedigest.MessageDigestSecurityBuilderFactory;
import com.yiji.common.security.referenceimplements.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <code>ri</code>环境创建 {@link ModeDistinguishSecurityManager} 的工厂。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
public class RiSecurityManagerFactory {
	
	/**
	 * 创建一个 {@link ModeDistinguishSecurityManager} 的实例。
	 * @param securityConfigs 配置参数的印射。
	 * @return 创建的实例。
	 */
	public ModeDistinguishSecurityManager createSecurityManager(Map<String, SecurityConfig> securityConfigs) {
		RISecurityManager securityManager = new RISecurityManager();
		securityManager.setSecurityConfigMap(securityConfigs);
		// 算法元数据持有者
		AlgorithmMetaHolder cipherAlgorithmMetaHolder = new AlgorithmMetaHolderImpl(new RSAAlgorithmMeta(),
			new AESAlgorithmMeta());
		AlgorithmMetaHolder signatureAlgorithmMetaHolder = new AlgorithmMetaHolderImpl(new RSAAlgorithmMeta(),
			new AESAlgorithmMeta());
		// 支持的安全模式的工厂列表
		ConcurrentMap<String, SecurityModeFactory> securityModeFactorys = new ConcurrentHashMap<>();
		// 双向（加密、解密）的“双向加密解密/双向签名/密文签名/直接对比签名验证模式的支持
		RIBidirectionalCipherSignatureSecurityModeFactory rIBidirectionalCipherSignatureSecurityModeFactory = new RIBidirectionalCipherSignatureSecurityModeFactory();
		rIBidirectionalCipherSignatureSecurityModeFactory.setLogOriginal(true);
		rIBidirectionalCipherSignatureSecurityModeFactory.setSecurityConfigMap(securityConfigs);
		rIBidirectionalCipherSignatureSecurityModeFactory.setCipherAlgorithmMetaHolder(cipherAlgorithmMetaHolder);
		rIBidirectionalCipherSignatureSecurityModeFactory.setSignatureAlgorithmMetaHolder(signatureAlgorithmMetaHolder);
		securityModeFactorys.put(BidirectionalCipherSignatureSecurityBuilderFactory.MODE,
			rIBidirectionalCipherSignatureSecurityModeFactory);
		// 双向（加密、解密）的“双向加密解密/单向签名/原文签名/使用密钥验证模式的支持
		RIBidirectionalCipherUnidirectionSignatureSecurityModeFactory rIBidirectionalCipherUnidirectionSignatureSecurityModeFactory = new RIBidirectionalCipherUnidirectionSignatureSecurityModeFactory();
		rIBidirectionalCipherUnidirectionSignatureSecurityModeFactory.setLogOriginal(true);
		rIBidirectionalCipherUnidirectionSignatureSecurityModeFactory.setSecurityConfigMap(securityConfigs);
		rIBidirectionalCipherUnidirectionSignatureSecurityModeFactory
			.setCipherAlgorithmMetaHolder(cipherAlgorithmMetaHolder);
		rIBidirectionalCipherUnidirectionSignatureSecurityModeFactory
			.setSignatureAlgorithmMetaHolder(signatureAlgorithmMetaHolder);
		securityModeFactorys.put(BidirectionalCipherSignatureVerifySecurityBuilderFactory.MODE,
			rIBidirectionalCipherUnidirectionSignatureSecurityModeFactory);
		// 双向（加密、解密）的“双向加密解密/对比原文验证”模式的支持
		RIBidirectionalCipherSecurityModeFactory rIBidirectionalCipherSecurityModeFactory = new RIBidirectionalCipherSecurityModeFactory();
		rIBidirectionalCipherSecurityModeFactory.setLogOriginal(true);
		rIBidirectionalCipherSecurityModeFactory.setSecurityConfigMap(securityConfigs);
		rIBidirectionalCipherSecurityModeFactory.setCipherAlgorithmMetaHolder(cipherAlgorithmMetaHolder);
		rIBidirectionalCipherSecurityModeFactory.setSignatureAlgorithmMetaHolder(signatureAlgorithmMetaHolder);
		securityModeFactorys.put(BidirectionalCipherSecurityBuilderFactory.MODE,
			rIBidirectionalCipherSecurityModeFactory);
		// 单独消息摘要模式的支持
		RIMessageDigestSecurityModeFactory riMessageDigestSecurityModeFactory = new RIMessageDigestSecurityModeFactory();
		riMessageDigestSecurityModeFactory.setLogOriginal(true);
		riMessageDigestSecurityModeFactory.setSecurityConfigMap(securityConfigs);
		securityModeFactorys.put(MessageDigestSecurityBuilderFactory.MODE, riMessageDigestSecurityModeFactory);
		
		securityManager.setSecurityModeFactorys(securityModeFactorys);
		return securityManager;
	}
	
}
