/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */

/*
 * 修订记录：
 * Agrael 2015年9月7日 下午5:20:18 创建
 */
package com.yiji.boot.securityframework;

import com.yiji.boot.core.AutoConfigurationProperties;
import com.yiji.common.security.annotation.*;
import com.yiji.common.security.annotation.support.ValidSignatureAnnotationSupport;
import com.yiji.common.security.bean.Syphon;
import com.yiji.common.security.spring.web.ConvertBindSecurityResolver;
import com.yiji.common.security.spring.web.ProtectAnnotationAdvice;
import com.yiji.common.security.spring.web.RedirectMethodReturnValueHandler;
import com.yiji.common.security.spring.web.ValidSignatureAnnotationInterceptor;
import com.yiji.common.security.web.Redirect;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 安全框架Web相关的功能的配置属性信息。 如果启用了{@link Protected}、 {@link NeedProtect} 与 {@link Unprotect}的支持，则会创建一个名为
 * <code>yijiBoot_SF_ProtectAnnotationAdvice</code> 的 {@link ProtectAnnotationAdvice}
 * 实例放入容器中。可以对其自行引用用来完成保护。如果开启保护所有的控制器（ {@link ProtectProperties#isProtectAllController()} 为<code>true</code>），则
 * <code>yijiBoot_SF_ProtectAnnotationAdvice</code>会默认用来保护控制器。
 *
 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
 *
 */
@ConfigurationProperties(WebSecurityProperties.PATH)
public class WebSecurityProperties implements AutoConfigurationProperties, Serializable {
	
	private static final long serialVersionUID = -9148080789781041829L;
	
	/** 表达式取得该类实例的路径 */
	public static final String PATH = "yiji.security.web";
	
	/** 是否启用安全框架Web相关的功能，true为启用 */
	private boolean enable = true;
	
	/** 表达式取得该对象的路径 */
	private final String path = PATH;
	
	/** 启用 {@link ConvertBind} 与 {@link InjectBind} 支持的属性 */
	private ConvertBindProperties convertBindProperties = new ConvertBindProperties();
	
	/** 启用 {@link Redirect} 支持的属性 */
	private RedirectProperties redirectProperties = new RedirectProperties();
	
	/** 启用 {@link ValidSignature} 支持的属性 */
	private ValidSignatureProperties validSignatureProperties = new ValidSignatureProperties();
	
	/** 启用 {@link Protected}、 {@link NeedProtect} 与 {@link Unprotect} 支持 的属性 */
	private ProtectProperties protectProperties = new ProtectProperties();
	
	/**
	 * 判定是否启用安全框架Web相关的功能。
	 * @return true为启用。
	 */
	public boolean isEnable() {
		return this.enable;
	}
	
	/**
	 * 设置是否启用安全框架Web相关的功能。
	 * @param enable true为启用。
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	/**
	 * 得到表达式取得该对象的路径。
	 * @return 表达式取得该对象的路径。
	 */
	public String getPath() {
		return this.path;
	}
	
	/**
	 * 得到启用 {@link ConvertBind} 与 {@link InjectBind} 支持的属性。
	 * @return 启用 {@link ConvertBind} 与 {@link InjectBind} 支持的属性。
	 */
	@NotNull
	public ConvertBindProperties getConvertBindProperties() {
		return this.convertBindProperties;
	}
	
	/**
	 * 设置启用 {@link ConvertBind} 与 {@link InjectBind} 支持的属性。
	 * @param convertBindProperties 启用 {@link ConvertBind} 与 {@link InjectBind} 支持的属性。
	 */
	public void setConvertBindProperties(ConvertBindProperties convertBindProperties) {
		this.convertBindProperties = convertBindProperties;
	}
	
	/**
	 * 得到启用 {@link Redirect} 支持的属性。
	 * @return 启用 {@link Redirect} 支持的属性。
	 */
	@NotNull
	public RedirectProperties getRedirectProperties() {
		return this.redirectProperties;
	}
	
	/**
	 * 设置启用 {@link Redirect} 支持的属性。
	 * @param redirectProperties 启用 {@link Redirect} 支持的属性。
	 */
	public void setRedirectProperties(RedirectProperties redirectProperties) {
		this.redirectProperties = redirectProperties;
	}
	
	/**
	 * 得到启用 {@link ValidSignature} 支持的属性。
	 * @return 启用 {@link ValidSignature} 支持的属性。
	 */
	@NotNull
	public ValidSignatureProperties getValidSignatureProperties() {
		return this.validSignatureProperties;
	}
	
	/**
	 * 设置启用 {@link ValidSignature} 支持的属性。
	 * @param validSignatureProperties 启用 {@link ValidSignature} 支持的属性。
	 */
	public void setValidSignatureProperties(ValidSignatureProperties validSignatureProperties) {
		this.validSignatureProperties = validSignatureProperties;
	}
	
	/**
	 * 得到启用 {@link Protected}、 {@link NeedProtect} 与 {@link Unprotect} 支持 的属性。
	 * @return 启用 {@link Protected}、 {@link NeedProtect} 与 {@link Unprotect} 支持 的属性。
	 */
	public ProtectProperties getProtectProperties() {
		return this.protectProperties;
	}
	
	/**
	 * 设置启用 {@link Protected}、 {@link NeedProtect} 与 {@link Unprotect} 支持 的属性。
	 * @param protectProperties 启用 {@link Protected}、 {@link NeedProtect} 与 {@link Unprotect} 支持 的属性。
	 */
	public void setProtectProperties(ProtectProperties protectProperties) {
		this.protectProperties = protectProperties;
	}
	
	/**
	 * 启用 {@link ConvertBind} 与 {@link InjectBind} 支持的属性。该属性对应 {@link ConvertBindSecurityResolver} 设置的默认属性。
	 *
	 *
	 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
	 *
	 */
	public static class ConvertBindProperties implements AutoConfigurationProperties, Serializable {
		
		private static final long serialVersionUID = -4537505948550315795L;
		
		/** 表达式取得该类实例的路径 */
		public static final String PATH = WebSecurityProperties.PATH + ".convertBind";
		
		/** 是否启用 {@link ConvertBind} 与 {@link InjectBind} 支持，true为启用 */
		private boolean enable = true;
		
		/** 表达式取得该对象的路径 */
		private final String path = PATH;
		
		/** 转换所需的加密数据的名称 */
		private String ciphertextName;
		
		/** 转换出的结果的处理器名称 */
		private String defaultResultProcessorName;
		
		/** 使用的安全管理器的引用名称 */
		private String securityManagerRefName = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager";
		
		/** 是否在记录日志时记录原文，为true则需要记录 */
		private boolean logOriginal;
		
		/** 绑定资源的名称 */
		private String name;
		
		/** 绑定值到资源的范围 */
		private String scope;
		
		/** 安全用户的引用名 */
		private String securityUserRef;
		
		/** 转换所需的签名数据的名称 */
		private String signatureName;
		
		/**
		 * 判定是否启用 {@link ConvertBind} 与 {@link InjectBind} 支持。
		 * @return true为启用。
		 */
		public boolean isEnable() {
			return this.enable;
		}
		
		/**
		 * 设置是否启用 {@link ConvertBind} 与 {@link InjectBind} 支持。
		 * @param enable true为启用。
		 */
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		
		/**
		 * 得到表达式取得该对象的路径。
		 * @return 表达式取得该对象的路径。
		 */
		public String getPath() {
			return this.path;
		}
		
		/**
		 * 得到转换所需的加密数据的名称。
		 * @return 转换所需的加密数据的名称。
		 */
		public String getCiphertextName() {
			return this.ciphertextName;
		}
		
		/**
		 * 设置转换所需的加密数据的名称。
		 * @param ciphertextName 转换所需的加密数据的名称。
		 */
		public void setCiphertextName(String ciphertextName) {
			this.ciphertextName = ciphertextName;
		}
		
		/**
		 * 得到转换出的结果的处理器名称。
		 * @return 转换出的结果的处理器名称。
		 */
		public String getDefaultResultProcessorName() {
			return this.defaultResultProcessorName;
		}
		
		/**
		 * 设置转换出的结果的处理器名称。
		 * @param defaultResultProcessorName 转换出的结果的处理器名称。
		 */
		public void setDefaultResultProcessorName(String defaultResultProcessorName) {
			this.defaultResultProcessorName = defaultResultProcessorName;
		}
		
		/**
		 * 得到使用的安全管理器的引用名称。
		 * @return 使用的安全管理器的引用名称。如果没有调用 {@link #setSecurityManagerRefName(String)} 作过修改，则返回默认值
		 * <code>yijiBoot_SF_SecurityManager</code>。
		 */
		public String getSecurityManagerRefName() {
			return this.securityManagerRefName;
		}
		
		/**
		 * 设置使用的安全管理器的引用名称。
		 * @param securityManagerRefName 使用的安全管理器的引用名称。
		 */
		public void setSecurityManagerRefName(String securityManagerRefName) {
			this.securityManagerRefName = securityManagerRefName;
		}
		
		/**
		 * 得到判定是否在记录日志时记录原文。
		 * @return 为true则需要记录，如果没有调用 {@link #setLogOriginal(boolean)} 作过修改，默认为 false 。
		 */
		public boolean isLogOriginal() {
			return this.logOriginal;
		}
		
		/**
		 * 设置判定是否在记录日志时记录原文。
		 * @param logOriginal 为true则需要记录。
		 */
		public void setLogOriginal(boolean logOriginal) {
			this.logOriginal = logOriginal;
		}
		
		/**
		 * 得到绑定资源的名称。
		 * @return 绑定资源的名称。
		 */
		public String getName() {
			return this.name;
		}
		
		/**
		 * 设置绑定资源的名称。
		 * @param name 绑定资源的名称。
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		/**
		 * 得到绑定值到资源的范围。
		 * @return 绑定值到资源的范围。
		 */
		public String getScope() {
			return this.scope;
		}
		
		/**
		 * 设置绑定值到资源的范围。
		 * @param scope 绑定值到资源的范围。
		 */
		public void setScope(String scope) {
			this.scope = scope;
		}
		
		/**
		 * 得到安全用户的引用名。
		 * @return 安全用户的引用名。
		 */
		public String getSecurityUserRef() {
			return this.securityUserRef;
		}
		
		/**
		 * 设置安全用户的引用名。
		 * @param securityUserRef 安全用户的引用名。
		 */
		public void setSecurityUserRef(String securityUserRef) {
			this.securityUserRef = securityUserRef;
		}
		
		/**
		 * 得到转换所需的签名数据的名称。
		 * @return 转换所需的签名数据的名称。
		 */
		public String getSignatureName() {
			return this.signatureName;
		}
		
		/**
		 * 设置转换所需的签名数据的名称。
		 * @param signatureName 转换所需的签名数据的名称。
		 */
		public void setSignatureName(String signatureName) {
			this.signatureName = signatureName;
		}
		
	}
	
	/**
	 * {@link Redirect} 支持的属性。 该属性对应 {@link RedirectMethodReturnValueHandler} 设置的默认属性。
	 *
	 *
	 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
	 *
	 */
	public static class RedirectProperties implements AutoConfigurationProperties, Serializable {
		
		private static final long serialVersionUID = -8447691749120604813L;
		
		/** 表达式取得该类实例的路径 */
		public static final String PATH = WebSecurityProperties.PATH + ".redirect";
		
		/** 是否启用 {@link Redirect} 支持，true为启用 */
		private boolean enable = true;
		
		/** 表达式取得该对象的路径 */
		private final String path = PATH;
		
		/** 密文的名称 */
		private String ciphertextName;
		
		/** Web的范围信息相关服务的引用名称 */
		private String webScopeServiceRefName;
		
		/** Web的加密服务的引用名称 */
		private String webCryptographicServiceRefName;
		
		/** 使用的安全管理器的引用名称 */
		private String securityManagerRefName = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager";
		
		/** 类型转换服务的引用名称 */
		private String conversionServiceRefName;
		
		/** 签名的名称 */
		private String signatureName;
		
		/** 使用的编码 */
		private String encodeCharset;
		
		/**
		 * 判定是否启用 {@link Redirect} 支持。
		 * @return true为启用。
		 */
		public boolean isEnable() {
			return this.enable;
		}
		
		/**
		 * 设置是否启用 {@link Redirect} 支持。
		 * @param enable true为启用。
		 */
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		
		/**
		 * 得到表达式取得该对象的路径。
		 * @return 表达式取得该对象的路径。
		 */
		public String getPath() {
			return this.path;
		}
		
		/**
		 * 得到密文的名称。
		 * @return 密文的名称。
		 */
		public String getCiphertextName() {
			return this.ciphertextName;
		}
		
		/**
		 * 设置密文的名称。
		 * @param ciphertextName 密文的名称。
		 */
		public void setCiphertextName(String ciphertextName) {
			this.ciphertextName = ciphertextName;
		}
		
		/**
		 * 得到Web的范围信息相关服务的引用名称。
		 * @return Web的范围信息相关服务的引用名称。
		 */
		public String getWebScopeServiceRefName() {
			return this.webScopeServiceRefName;
		}
		
		/**
		 * 设置Web的范围信息相关服务的引用名称。
		 * @param webScopeServiceRefName Web的范围信息相关服务的引用名称。
		 */
		public void setWebScopeServiceRefName(String webScopeServiceRefName) {
			this.webScopeServiceRefName = webScopeServiceRefName;
		}
		
		/**
		 * 得到Web的加密服务的引用名称。
		 * @return Web的加密服务的引用名称。
		 */
		public String getWebCryptographicServiceRefName() {
			return this.webCryptographicServiceRefName;
		}
		
		/**
		 * 设置Web的加密服务的引用名称。
		 * @param webCryptographicServiceRefName Web的加密服务的引用名称。
		 */
		public void setWebCryptographicServiceRefName(String webCryptographicServiceRefName) {
			this.webCryptographicServiceRefName = webCryptographicServiceRefName;
		}
		
		/**
		 * 得到使用的安全管理器的引用名称。
		 * @return 使用的安全管理器的引用名称。如果没有调用 {@link #setSecurityManagerRefName(String)} 作过修改，则返回默认值
		 * <code>yijiBoot_SF_SecurityManager</code>。
		 */
		public String getSecurityManagerRefName() {
			return this.securityManagerRefName;
		}
		
		/**
		 * 设置使用的安全管理器的引用名称。
		 * @param securityManagerRefName 使用的安全管理器的引用名称。
		 */
		public void setSecurityManagerRefName(String securityManagerRefName) {
			this.securityManagerRefName = securityManagerRefName;
		}
		
		/**
		 * 得到类型转换服务的引用名称。
		 * @return 类型转换服务的引用名称。
		 */
		public String getConversionServiceRefName() {
			return this.conversionServiceRefName;
		}
		
		/**
		 * 设置类型转换服务的引用名称。
		 * @param conversionServiceRefName 类型转换服务的引用名称。
		 */
		public void setConversionServiceRefName(String conversionServiceRefName) {
			this.conversionServiceRefName = conversionServiceRefName;
		}
		
		/**
		 * 得到签名的名称。
		 * @return 签名的名称。
		 */
		public String getSignatureName() {
			return this.signatureName;
		}
		
		/**
		 * 设置签名的名称。
		 * @param signatureName 签名的名称。
		 */
		public void setSignatureName(String signatureName) {
			this.signatureName = signatureName;
		}
		
		/**
		 * 得到使用的编码。
		 * @return 使用的编码。
		 */
		public String getEncodeCharset() {
			return this.encodeCharset;
		}
		
		/**
		 * 设置使用的编码。
		 * @param encodeCharset 使用的编码。
		 */
		public void setEncodeCharset(String encodeCharset) {
			this.encodeCharset = encodeCharset;
		}
		
	}
	
	/**
	 * {@link ValidSignature} 支持的属性。 该属性对应 {@link ValidSignatureAnnotationInterceptor} 设置的默认属性。
	 *
	 *
	 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
	 *
	 */
	public static class ValidSignatureProperties implements AutoConfigurationProperties, Serializable {
		
		private static final long serialVersionUID = -3709732195456290273L;
		
		/** 表达式取得该类实例的路径 */
		public static final String PATH = WebSecurityProperties.PATH + ".validSignature";
		
		/** 是否启用 {@link ValidSignature} 支持，true为启用 */
		private boolean enable = true;
		
		/** 表达式取得该对象的路径 */
		private final String path = PATH;
		
		/** 使用的安全管理器的引用名称 */
		private String securityManagerRefName = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager";
		
		/** 默认安全用户的引用名 */
		private String securityUserRef;
		
		/** 默认需要验证的签名的数据的名称 */
		private String signatureName;
		
		/**
		 * {@link ValidSignatureAnnotationSupport#invoke(Object, Object, Object)} 验证失败后是抛出异常还是返回false，为true为抛出异常
		 */
		private boolean validThrowException = true;
		
		/** Web的范围信息相关服务的引用名称 */
		private String webScopeServiceRefName;
		
		/** 默认的验证签名的值的范围，多个以<code>,</code>隔开 */
		private String valueScope;
		
		/**
		 * 判定是否启用 {@link ValidSignature} 支持。
		 * @return true为启用。
		 */
		public boolean isEnable() {
			return this.enable;
		}
		
		/**
		 * 设置是否启用 {@link ValidSignature} 支持。
		 * @param enable true为启用。
		 */
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		
		/**
		 * 得到表达式取得该对象的路径。
		 * @return 表达式取得该对象的路径。
		 */
		public String getPath() {
			return this.path;
		}
		
		/**
		 * 得到使用的安全管理器的引用名称。
		 * @return 使用的安全管理器的引用名称。如果没有调用 {@link #setSecurityManagerRefName(String)} 作过修改，则返回默认值
		 * <code>yijiBoot_SF_SecurityManager</code>。
		 */
		public String getSecurityManagerRefName() {
			return this.securityManagerRefName;
		}
		
		/**
		 * 设置使用的安全管理器的引用名称。
		 * @param securityManagerRefName 使用的安全管理器的引用名称。
		 */
		public void setSecurityManagerRefName(String securityManagerRefName) {
			this.securityManagerRefName = securityManagerRefName;
		}
		
		/**
		 * 得到默认安全用户的引用名。
		 * @return 默认安全用户的引用名。
		 */
		public String getSecurityUserRef() {
			return this.securityUserRef;
		}
		
		/**
		 * 设置默认安全用户的引用名。
		 * @param securityUserRef 默认安全用户的引用名。
		 */
		public void setSecurityUserRef(String securityUserRef) {
			this.securityUserRef = securityUserRef;
		}
		
		/**
		 * 得到默认需要验证的签名的数据的名称。
		 * @return 默认需要验证的签名的数据的名称。
		 */
		public String getSignatureName() {
			return this.signatureName;
		}
		
		/**
		 * 设置默认需要验证的签名的数据的名称。
		 * @param signatureName 默认需要验证的签名的数据的名称。
		 */
		public void setSignatureName(String signatureName) {
			this.signatureName = signatureName;
		}
		
		/**
		 * 得到判定 {@link ValidSignatureAnnotationSupport#invoke(Object, Object, Object)} 验证失败后是抛出异常还是返回false。
		 * @return true为抛出异常，如果没有调用 {@link #setValidThrowException(boolean)} 作过修改，默认为 true 。
		 */
		public boolean isValidThrowException() {
			return this.validThrowException;
		}
		
		/**
		 * 设置 {@link ValidSignatureAnnotationSupport#invoke(Object, Object, Object)} 验证失败后是抛出异常还是返回false。
		 * @param validThrowException true为抛出异常
		 */
		public void setValidThrowException(boolean validThrowException) {
			this.validThrowException = validThrowException;
		}
		
		/**
		 * 得到Web的范围信息相关服务的引用名称。
		 * @return Web的范围信息相关服务的引用名称。
		 */
		public String getWebScopeServiceRefName() {
			return this.webScopeServiceRefName;
		}
		
		/**
		 * 设置Web的范围信息相关服务的引用名称。
		 * @param webScopeServiceRefName Web的范围信息相关服务的引用名称。
		 */
		public void setWebScopeServiceRefName(String webScopeServiceRefName) {
			this.webScopeServiceRefName = webScopeServiceRefName;
		}
		
		/**
		 * 得到默认的验证签名的值的范围，多个以<code>,</code>隔开。
		 * @return 默认的验证签名的值的范围，多个以<code>,</code>隔开。
		 */
		public String getValueScope() {
			return this.valueScope;
		}
		
		/**
		 * 设置默认的验证签名的值的范围，多个以<code>,</code>隔开。
		 * @param valueScope 默认的验证签名的值的范围，多个以<code>,</code>隔开。
		 */
		public void setValueScope(String valueScope) {
			this.valueScope = valueScope;
		}
		
	}
	
	/**
	 * {@link Protected}、 {@link NeedProtect} 与 {@link Unprotect}支持的属性。 该属性对应 {@link ProtectAnnotationAdvice} 设置的默认属性。
	 *
	 *
	 * @author Agreal·Lee (e-mail:lixiang@yiji.com)
	 *
	 */
	public static class ProtectProperties implements AutoConfigurationProperties, Serializable {
		
		private static final long serialVersionUID = -3709732195474290273L;
		
		/** 表达式取得该类实例的路径 */
		public static final String PATH = WebSecurityProperties.PATH + ".protect";
		
		/**
		 * 是否启用 {@link Protected}、 {@link NeedProtect} 与 {@link Unprotect} 支持，true为启用
		 */
		private boolean enable = true;
		
		/** 表达式取得该对象的路径 */
		private final String path = PATH;
		
		/** 默认安全用户 */
		private String defaultSecurityUser;
		
		/** 是否在记录日志时记录原文，为true则需要记录 */
		private boolean logOriginal;
		
		/** 使用的安全管理器的引用名称 */
		private String securityManagerRefName = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "SecurityManager";
		
		/** 安全用户的引用名 */
		private String securityUserRef;
		
		/** 使用的 {@link Syphon} 的引用名称 */
		private String syphonRefName = SecurityProperties.BOOT_CREATE_BEAN_NAME_PREFIX + "Syphon";
		
		/** Web的范围信息相关服务的引用名称 */
		private String webScopeServiceRefName;
		
		/** 是否开启保护所有的控制器，为true是开启 */
		private boolean protectAllController = true;
		
		/**
		 * 判定是否启用{@link Protected}、 {@link NeedProtect} 与 {@link Unprotect}支持。
		 * @return true为启用。
		 */
		public boolean isEnable() {
			return this.enable;
		}
		
		/**
		 * 设置是否启用 {@link Protected}、 {@link NeedProtect} 与 {@link Unprotect}支持。
		 * @param enable true为启用。
		 */
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		
		/**
		 * 得到表达式取得该对象的路径。
		 * @return 表达式取得该对象的路径。
		 */
		public String getPath() {
			return this.path;
		}
		
		/**
		 * 得到默认安全用户。
		 * @return 默认安全用户。
		 */
		public String getDefaultSecurityUser() {
			return this.defaultSecurityUser;
		}
		
		/**
		 * 设置默认安全用户。
		 * @param defaultSecurityUser 默认安全用户。
		 */
		public void setDefaultSecurityUser(String defaultSecurityUser) {
			this.defaultSecurityUser = defaultSecurityUser;
		}
		
		/**
		 * 得到判定是否在记录日志时记录原文。
		 * @return 为true则需要记录。如果没有调用 {@link #setLogOriginal(boolean)} 作过修改，默认返回 false 。
		 */
		public boolean isLogOriginal() {
			return this.logOriginal;
		}
		
		/**
		 * 设置是否在记录日志时记录原文。
		 * @param logOriginal 为true则需要记录。
		 */
		public void setLogOriginal(boolean logOriginal) {
			this.logOriginal = logOriginal;
		}
		
		/**
		 * 得到使用的安全管理器的引用名称。
		 * @return 使用的安全管理器的引用名称。如果没有调用 {@link #setSecurityManagerRefName(String)} 作过修改，则返回默认值
		 * <code>yijiBoot_SF_SecurityManager</code>。
		 */
		public String getSecurityManagerRefName() {
			return this.securityManagerRefName;
		}
		
		/**
		 * 设置使用的安全管理器的引用名称。
		 * @param securityManagerRefName 使用的安全管理器的引用名称。
		 */
		public void setSecurityManagerRefName(String securityManagerRefName) {
			this.securityManagerRefName = securityManagerRefName;
		}
		
		/**
		 * 得到安全用户的引用名。
		 * @return 安全用户的引用名。
		 */
		public String getSecurityUserRef() {
			return this.securityUserRef;
		}
		
		/**
		 * 设置安全用户的引用名。
		 * @param securityUserRef 安全用户的引用名。
		 */
		public void setSecurityUserRef(String securityUserRef) {
			this.securityUserRef = securityUserRef;
		}
		
		/**
		 * 得到使用的 {@link Syphon} 的引用名称。
		 * @return 使用的 {@link Syphon} 的引用名称。如果没有调用 {@link #setSyphonRefName(String)} 作过修改，则返回默认值
		 * <code>yijiBoot_SF_Syphon</code>。
		 */
		public String getSyphonRefName() {
			return this.syphonRefName;
		}
		
		/**
		 * 设置使用的 {@link Syphon} 的引用名称。
		 * @param syphonRefName 使用的 {@link Syphon} 的引用名称。
		 */
		public void setSyphonRefName(String syphonRefName) {
			this.syphonRefName = syphonRefName;
		}
		
		/**
		 * 得到Web的范围信息相关服务的引用名称。
		 * @return Web的范围信息相关服务的引用名称。
		 */
		public String getWebScopeServiceRefName() {
			return this.webScopeServiceRefName;
		}
		
		/**
		 * 设置Web的范围信息相关服务的引用名称。
		 * @param webScopeServiceRefName Web的范围信息相关服务的引用名称。
		 */
		public void setWebScopeServiceRefName(String webScopeServiceRefName) {
			this.webScopeServiceRefName = webScopeServiceRefName;
		}
		
		/**
		 * 得到判定是否开启保护所有的控制器。
		 * @return 为true是开启 。如果没有调用 {@link #setProtectAllController(boolean)} 作过修改，则默认返回 true 。
		 */
		public boolean isProtectAllController() {
			return this.protectAllController;
		}
		
		/**
		 * 设置是否开启保护所有的控制器。
		 * @param protectAllController 为true是开启 。
		 */
		public void setProtectAllController(boolean protectAllController) {
			this.protectAllController = protectAllController;
		}
		
	}
	
}
