/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月9日 下午12:37:00 创建
 */
package com.yiji.boot.test.security;

import com.yiji.common.security.KeyType;
import com.yiji.common.security.mode.bidirection.BidirectionalCipherSecurityBuilderFactory;
import com.yiji.common.security.mode.bidirection.BidirectionalCipherSignatureVerifySecurityBuilderFactory;
import com.yiji.common.security.mode.messagedigest.MessageDigestSecurityBuilderFactory;
import com.yiji.common.security.referenceimplements.SecurityConfig;
import com.yjf.common.lang.security.DigestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于RI环境初始化安全配置的初始化器。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@ConditionalOnProperty(value = "yiji.security.enable",matchIfMissing = true)
@Component
public class RiSecurityConfigsIniter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private Map<String, SecurityConfig> scs;
	
	@PostConstruct
	public void init() throws Exception {
		this.logger.info("初始化RI环境初始化安全配置。");
		// 交易交互使用加密并签名
		String algorithm = "RSA";
		String signatureAlgorithm = "MD5withRSA";
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
		Charset utf8 = Charset.forName("UTF-8");
		SecurityConfig merchants = new SecurityConfig();
		SecurityConfig customer = new SecurityConfig();
		processBUOKVSecurityUserPair(algorithm, signatureAlgorithm, keyPairGenerator, utf8, customer, merchants);
		this.scs.put(merchants.getSecurityUser(), merchants);
		this.scs.put(customer.getSecurityUser(), customer);
		
		// 查看产品列表使用摘要
		byte[] signatureSalt = "!@#$%^&*(".getBytes(utf8);
		signatureAlgorithm = "MD5";
		SecurityConfig customerSignRequest = new SecurityConfig();
		SecurityConfig merchantsSignResponse = new SecurityConfig();
		processMDSecurityUserPair(signatureAlgorithm, utf8, customerSignRequest, merchantsSignResponse, signatureSalt);
		this.scs.put(customerSignRequest.getSecurityUser(), customerSignRequest);
		this.scs.put(merchantsSignResponse.getSecurityUser(), merchantsSignResponse);
		
		// 站内内容保护
		String protectAlgorithm = "AES";
		// A方
		SecurityConfig merchantsProtect = new SecurityConfig();
		merchantsProtect.setSecurityUser("AProtect");
		processProtectSecurityUser(utf8, protectAlgorithm, merchantsProtect);
		this.scs.put(merchantsProtect.getSecurityUser(), merchantsProtect);
		// B方
		SecurityConfig customerProtect = new SecurityConfig();
		customerProtect.setSecurityUser("BProtect");
		processProtectSecurityUser(utf8, protectAlgorithm, customerProtect);
		this.scs.put(customerProtect.getSecurityUser(), customerProtect);
	}
	
	private void processProtectSecurityUser(Charset utf8, String protectAlgorithm, SecurityConfig securityConfig)
																													throws NoSuchAlgorithmException {
		securityConfig.setMode(BidirectionalCipherSecurityBuilderFactory.MODE);
		KeyGenerator keyGenerator = KeyGenerator.getInstance(protectAlgorithm);
		SecretKey secretKey = keyGenerator.generateKey();
		securityConfig.setCipherAlgorithm(protectAlgorithm);
		securityConfig.setCipherCharset(utf8);
		securityConfig.setDecryptKey(secretKey.getEncoded());
		securityConfig.setDecryptKeyType(KeyType.SECRET_KEY);
		securityConfig.setEncryptKey(secretKey.getEncoded());
		securityConfig.setEncryptKeyType(KeyType.SECRET_KEY);
		securityConfig.setOriginalCharset(utf8);
	}
	
	// MessageDigestSecurityBuilderFactory.MODE
	private void processMDSecurityUserPair(String signatureAlgorithm, Charset utf8, SecurityConfig customerRequest,
											SecurityConfig merchantsResponse, byte[] signatureSalt) {
		// 安全用户
		customerRequest.setSecurityUser("BSign");
		merchantsResponse.setSecurityUser("ASign");
		// 使用的模式
		customerRequest.setMode(MessageDigestSecurityBuilderFactory.MODE);
		merchantsResponse.setMode(MessageDigestSecurityBuilderFactory.MODE);
		// 签名（实际上为摘要）算法
		customerRequest.setSignatureAlgorithm(signatureAlgorithm);
		merchantsResponse.setSignatureAlgorithm(signatureAlgorithm);
		// 签名使用的编码
		customerRequest.setSignatureCharset(utf8);
		merchantsResponse.setSignatureCharset(utf8);
		// 签名使用的盐
		customerRequest.setSignatureSalt(signatureSalt);
		merchantsResponse.setSignatureSalt(signatureSalt);
		// 原文编码
		customerRequest.setOriginalCharset(utf8);
		merchantsResponse.setOriginalCharset(utf8);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("v1", "asky1");
		params.put("v2", "asky2");
		params.put("v3", "asky3");
		this.logger.info("参数为'v1=asky1&v2=asky2&v3=asky3'的参数，签名结果为：'{}'。",
			DigestUtil.digestWithMD5(params, new String(signatureSalt, utf8)));
	}
	
	// BidirectionalCipherSignatureVerifySecurityBuilderFactory.MODE
	private void processBUOKVSecurityUserPair(String algorithm, String signatureAlgorithm,
												KeyPairGenerator keyPairGenerator, Charset utf8,
												SecurityConfig customer, SecurityConfig merchants) {
		// 安全用户
		customer.setSecurityUser("B");
		merchants.setSecurityUser("A");
		// 使用的模式
		customer.setMode(BidirectionalCipherSignatureVerifySecurityBuilderFactory.MODE);
		merchants.setMode(BidirectionalCipherSignatureVerifySecurityBuilderFactory.MODE);
		// 加密解密的算法
		customer.setCipherAlgorithm(algorithm);
		merchants.setCipherAlgorithm(algorithm);
		// 加密解密的编码
		customer.setCipherCharset(utf8);
		merchants.setCipherCharset(utf8);
		// 商户系统加密（公钥），客户系统解密（私钥）密钥对
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		customer.setDecryptKey(keyPair.getPrivate().getEncoded());
		merchants.setEncryptKey(keyPair.getPublic().getEncoded());
		// 原文编码
		customer.setOriginalCharset(utf8);
		merchants.setOriginalCharset(utf8);
		// 签名算法
		customer.setSignatureAlgorithm(signatureAlgorithm);
		merchants.setSignatureAlgorithm(signatureAlgorithm);
		// 签名使用的编码
		customer.setSignatureCharset(utf8);
		merchants.setSignatureCharset(utf8);
		// 商户系统签名（私钥），客户系统验签名（公钥）密钥对
		keyPair = keyPairGenerator.generateKeyPair();
		customer.setCounterSignatureKey(keyPair.getPublic().getEncoded());
		merchants.setSignatureKey(keyPair.getPrivate().getEncoded());
	}
	
}
