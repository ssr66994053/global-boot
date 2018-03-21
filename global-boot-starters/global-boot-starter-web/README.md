## 1. 组件介绍
此组件提供spring mvc 的能力

## 2. 配置

不需要配置

## 3. FAQ

### 3.1 如何添加HandlerInterceptor？

使用`@SpringHandlerInterceptor`标注的类会添加到spring mvc HandlerInterceptor中。也可以通过`@SpringHandlerInterceptor`设置过滤的路径和包括的路径。

    @SpringHandlerInterceptor
    public class TestHandlerInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            return true;
        }
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            System.out.println("xxx");
        }
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        }
    }

### 3.2 我没有添加money的Converter<String,Money>，但是也可以实现把String转换为Money，这是怎么实现的？

spring实现了一个比较牛逼的`org.springframework.core.convert.support.ObjectToObjectConverter`，使用反射去找转换方法(通过money类的构造器找到了)。

### 3.3 我不需要使用velocity，如何禁用？

通过配置`yiji.velocity.enable=false`禁用velocity

### 3.4 异常怎么处理？

异常通过[GlobalExceptionHandler](src/main/java/com/yiji/boot/web/GlobalExceptionHandler.java)来处理，具体详情请看javadoc。
可以通过`yiji.web.globalExceptionHandlerEnable`禁用掉默认的异常处理器,使用自己提供异常处理器.

### 3.5 通过配置直接访问模板页面(vm)

通过配置`yiji.web.simplePageMap=/xxIndex.html->test,/test/xxxIndex.html->error`    
通过上面的例子配置，当用户访问/xxIndex.html地址时，会直接返回test.vm, 多个地址映射，用逗号分隔

### 3.6 如何自定义响应header?

配置key前缀为`yiji.resp.headers.`的配置项会作为自定义响应header,可以通过如下配置设置响应header:

    yiji.resp.headers.xxx=ooo
    yiji.resp.headers.Content-Security-Policy=xxxx
    
上面的配置会在响应header中增加`xxx=ooo`和`Content-Security-Policy=xxxx`,出于默认安全的策略考虑,我们默认配置:

* `x-xss-protection`

    控制浏览器的xss检查策略

* `x-frame-options`
    
    控制浏览器的iframe,默认为同源网站可以嵌入.[参考](https://developer.mozilla.org/zh-CN/docs/Web/HTTP/X-Frame-Options?redirectlocale=en-US&redirectslug=The_X-FRAME-OPTIONS_response_header)
    
### 3.7 在yiji-boot框架下，重定向页面传参数

       @RequestMapping("/hello.htm")
       public ModelAndView demo(RedirectAttributes redirectAttributes) {
           RedirectView redirectView =  new RedirectView("world.htm");
           redirectAttributes.addAttribute("testKey", "testValue" );
           ModelAndView view = new ModelAndView(redirectView);
           return view;
       }
   
       @RequestMapping("/world.htm")
       public String index(HttpServletRequest request, ModelMap modelMap){
           String userName = request.getParameter("testKey");
           modelMap.size();
           return "index";
       }
       
### 3.8 如何使用jsp

1. 增加依赖：

	     <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.tomcat.embed</groupId>
            <artifactId>tomcat-embed-jasper</artifactId>
        </dependency>
        
2. 启用配置

		yiji.tomcat.jsp.enable=true

3. 增加controller

		
		@RequestMapping("/jsp")
    	public String welcome1(Map<String, Object> model) {
        	model.put("time", new Date());
        	model.put("message", "xxxx");
        	return "welcome";
    	}
    	
4. 增加jsp页面

	页面需放到`src/main/resources/META-INF/resources/jsp/welcome.jsp`路径。
	
### 3.9 如何获取http请求body体?

在filter中调用了request.getParameter,此时,已经对流解析了,如果应用中再调用request.getInputStream会读不到流里的内容。在我们的应用代码中直接解析流获取内容，会遇到数据为空的情况。

可以用下面两种方式解决:

#### 3.9.1 方式一：重新组装InputStrean

    	@RequestMapping(method = RequestMethod.POST, value = "/testGetInput")
    	@ResponseBody
    	public String testGetInput(HttpServletRequest request) throws Exception {
    		ServletServerHttpRequest servletServerHttpRequest = new ServletServerHttpRequest(request);
    		return URLDecoder.decode(Streams.asString(servletServerHttpRequest.getBody(), "utf-8"), "utf-8");
    	}

#### 3.9.2 方式二：使用spring提供的annotation	
    	
    	@RequestMapping(method = RequestMethod.POST, value = "/testGetInput1")
    	@ResponseBody
    	public String testGetInput1(@RequestBody String body) throws Exception {
    		return URLDecoder.decode(body, "utf-8");
    	}
    	
### 3.10 如何自定义http request encoding？

`javax.servlet.ServletRequest#setCharacterEncoding`,This method must be called prior to reading request parameters or reading input using getReader().

所以，我们通常需要把设置编码的filter放在第一位。某些场景，我们需要通过请求中的参数去设置编码，我们需要通过如下步骤来实现。

1. 关闭默认CharacterEncodingFilter

		@Bean
		public FilterRegistrationBean disableOrderedCharacterEncodingFilter(CharacterEncodingFilter characterEncodingFilter) {
			FilterRegistrationBean registrationBean = new FilterRegistrationBean(characterEncodingFilter);
			registrationBean.setEnabled(false);
			return registrationBean;
		}
		
2. 编写自定义filter

		public static class CustomCharacterEncodingFilter extends OncePerRequestFilter {
			//编码服务
			private volatile EncodingService encodingService;
			
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
											FilterChain filterChain) throws ServletException, IOException {
				if (encodingService == null) {
					encodingService = ApplicationContextHolder.get().getBean(EncodingService.class);
				}
				//通过url获取编码
				String encoding = encodingService.findEncodingByUrl(request.getRequestURL().toString());
				request.setCharacterEncoding(encoding);
				filterChain.doFilter(request, response);
			}
		}
		
3. 注册filter

		@Bean
		public FilterRegistrationBean customCharacterEncodingFilter() {
			FilterRegistrationBean registrationBean = new FilterRegistrationBean();
			registrationBean.setFilter(new CustomCharacterEncodingFilter());
			//设置为最高优先级
			registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
			return registrationBean;
		}
		
	
### 3.11 如何修改http缓存时间？
    
通过`yiji.web.cacheMaxAge`修改http 缓存时间,-1=不设置,0=第二次请求需要和服务器协商,大于0=经过多少秒后才过期。