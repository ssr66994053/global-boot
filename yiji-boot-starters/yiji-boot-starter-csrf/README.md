## 1. 组件介绍
此组件提供对web应用防御CSRF攻击的能力.

CSRF参考：https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)

## 2. 配置

配置参考[com.yiji.boot.csrf.CsrfProperties](src/main/java/com/yiji/boot/csrf/CsrfProperties.java)

必配参数: 
无


## 3.CSRF防范

### 3.1 原理

通过过滤器来防范CSRF。在页面渲染时，生成随机Token，放入页面中。业务请求时携带随机Token，filter来比较随机Token是否一致。(类似于防重复提交的机制，唯一不同的是防重复提交的token在每次`post`时会改变)。

## 3.2 防范范围

根据`CSRF`攻击原理和利用价值，我们只防范**业务操作**请求。所以对于查询请求和静态资源请求，我们不需要防范。

所以，我们只防范`POST`请求.


## 4. 使用说明

此组件会对所有的`POST`请求过滤，如果需要排除某些请求，可以配置`yiji.csrf.ignore-uris`,多个uri用逗号隔开。

### 4.1 vm模板中的`form`表单使用

在vm中模板form表单中加入：

	#csrfInputHidden()

页面渲染结果如下：

	<input type="hidden" name="_csrf" value="130b4693-218e-4406-8b82-8561f095a816"/>


### 4.2 vm模板中的ajax请求中使用


1. 插入token

    首先需要在`html>head`中生成token，在`head`中加入：

	    #csrfMeta()

    页面渲染结果如下：

	    <meta name="X-CSRF-TOKEN" content="130b4693-218e-4406-8b82-8561f095a816"/>

2. 修改ajax请求

    jquery的请求可以做如下修改：

	    var token = $("meta[name='X-CSRF-TOKEN']").attr("content");//从meta中获取token
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader("X-CSRF-TOKEN", token);//每次ajax提交请求都会加入此token
        });

## 5. 前后端分离后的方案

如果我们实施前后端分离后，不会使用vm模板，根据我们的特点可以按照如下的方式实施。

### 5.1 html资源文件由app server输出

浏览器资源访问路径为：

                      |-> static resource server
    browser -> proxy-> 
                      |-> app server
    
理想情况下，所有浏览器的请求访问到proxy（cdn或者反向代理服务器），由proxy做请求路由分发(更进一步还会做一层cache)。proxy把所有静态请求分发到
静态资源服务器上(html、css、js、etc.)。

这样做非常理想，但是考虑到带来的运维成本和实际收益，我们仅仅将css、js等静态资源放在静态资源服务器，对于html页面的请求，任然由app server来处理。

### 5.2 html资源响应时，写crsf token到cookie

请求html资源文件时，写入crsf token到cookie，cookieName=_crsf

### 5.3 post请求处理

* ajax请求时，从cookie中拿到token，设置到请求header中。
* 表单提交时，js从cookie中读取token，构建hidden input。

## 6. 实现说明

### 6.1 token存储
  
1. 基于cookie的方案

    请求到服务器后,把token写入cookie中和request Attribute中

* 使用模板时,从request中获取token,并构造表单
* 前后端分离场景,在用户请求时,把csrfToken写入cookie,post提交时,js可以从cookie中获取csrfToken.
* 在cookie丢失的场景(safari浏览器会禁止iframe跨域保存cookie\api请求访问),请忽略csrf过滤,用其他方式保证.
