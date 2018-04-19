/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2015-06-15 14:12 创建
 *
 */
package com.global.boot.test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Maps;
import com.global.boot.core.Apps;
import com.global.boot.core.Versions;
import com.global.boot.test.dubbo.DemoService;
//import com.global.framework.hera.client.support.annotation.AutoConfig;
import com.global.common.concurrent.MonitoredThreadPool;
import com.global.common.customer.enums.CustomerTypeEnum;
import com.global.common.log.BusinessLogger;
import com.global.common.net.HttpUtil;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author qiubo@yiji.com
 */
@Controller
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Value("${app.test}")
    private String valueFormHera;

//    @AutoConfig("app.test1")
//    private volatile String valueFormHera1;
//
//    @AutoConfig("app.test2")
//    private volatile List<String> valueFormHera2;

    @Autowired
    private TestBean testBean;
    @Autowired
    private TestBean1 testBean1;

    @Autowired
    private Environment env;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private MonitoredThreadPool monitoredThreadPool;

    @Reference(version = "1.5")
    private DemoService demoService;

    @Resource
    private RedisTemplate<String,Long> redisTemplate;

    @RequestMapping("/testXss")
    @ResponseBody
    public String testXss(String input) {
        return input;
    }

    @RequestMapping("/test")
    @ResponseBody
    public String home() {
        return "Hello 1 1 World!" + testBean.getName() + testBean1.getName();
    }

    @RequestMapping("/testbean")
    @ResponseBody
    public TestBean1 testbean() {
        return testBean1;
    }

//    @RequestMapping("/testFastjson")
//    @ResponseBody
//    public TestBean fastjson() {
//        testBean.setValueFormHera(valueFormHera);
//        testBean.setValueFormHera1(valueFormHera1);
//        return testBean;
//    }
//
//    @RequestMapping("/testHera")
//    @ResponseBody
//    public TestBean hera() {
//        testBean.setValueFormHera(valueFormHera);
//        testBean.setValueFormHera1(valueFormHera1);
//        return testBean;
//    }

    @RequestMapping("/testvm")
    public String welcome(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "xxx");
        return "test";
    }

    @RequestMapping("/testvm1")
    public String index(ModelMap modelMap, HttpServletRequest request) {
        MDC.put("gid", "xxxxx");
        logger.info("{}", RequestContextHolder.currentRequestAttributes());
        modelMap.put("now", new Date());

        modelMap.put("data", "alert('dfdf')你是谁");
        TestResult orderBase = new TestResult();
        modelMap.put("orderBase", orderBase);
        modelMap.put("sessionId", request.getSession(true).getId());
        EnumUtils.getEnumList(CustomerTypeEnum.class);
        modelMap.put("enum", CustomerTypeEnum.class);
        return "test1";
    }

    @RequestMapping("/testvm2")
    public String index1(ModelMap modelMap, HttpServletRequest request) {
        return "/test2";
    }

    @RequestMapping("/testLogger")
    @ResponseBody
    public String testLogger(Integer count) {
        for (int i = 0; i < count; i++) {
            logger.info("xxxxxxxxxxxxxxxx");
        }
        return "ok";
    }

    @RequestMapping("/testDruidLogger")
    @ResponseBody
    public String testDruidLogger(Integer count) {
        Logger logger = LoggerFactory.getLogger("com.yiji.common.ds.sql");
        for (int i = 0; i < count; i++) {
            logger.info("xxxxxxxxxxxxxxxx");
        }
        return "ok";
    }

    @RequestMapping("/testBusinessLogger")
    @ResponseBody
    public String testBusinessLogger(Integer count) {
        if (count == null) {
            count = 10000;
        }
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            try {
                Thread.sleep(random.nextInt(10));
            } catch (InterruptedException e) {
                // ignore
            }
            BusinessLogger.log("测试", "count", i, "money", random.nextInt(1000));
        }
        return "ok";
    }

    @RequestMapping("/testConditionEvaluationReport")
    @ResponseBody
    public ConditionEvaluationReport testConditionEvaluationReport() {
        ConditionEvaluationReport conditionEvaluationReport = ConditionEvaluationReport
                .get((ConfigurableListableBeanFactory) beanFactory);
        return conditionEvaluationReport;
    }

    @RequestMapping("/testHttpUtil")
    @ResponseBody
    public void testHttpUtil(Integer count) {
        for (int i = 0; i < count; i++) {
            HttpUtil.getInstance().setEnablePoolMonitor(true).setEnableRequestMonitor(true).get("wwww.baidu.com");
        }
    }

    @RequestMapping("/testIp")
    @ResponseBody
    public String testIp(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    //	public String getValueFormHera1() {
    //		return valueFormHera1;
    //	}
//
//    public void setValueFormHera1(String valueFormHera1) {
//        logger.info("value changed");
//        this.valueFormHera1 = valueFormHera1;
//    }
//
//    public List<String> getValueFormHera2() {
//        return valueFormHera2;
//    }
//
//    public void setValueFormHera2(List<String> valueFormHera2) {
//        this.valueFormHera2 = valueFormHera2;
//    }

    @RequestMapping(method = RequestMethod.POST, value = "/testUpload")
    @ResponseBody
    public String handleFileUpload(@RequestParam("name") String name, @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(
                        Apps.getAppDataPath() + "/" + name)));
                FileCopyUtils.copy(file.getInputStream(), stream);
                stream.close();
                redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + name + "!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message",
                        "You failed to upload " + name + " => " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "You failed to upload " + name
                    + " because the file was empty");
        }

        return "name:" + name;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/testGetInput")
    @ResponseBody
    public String testGetInput(HttpServletRequest request) throws Exception {
        ServletServerHttpRequest servletServerHttpRequest = new ServletServerHttpRequest(request);
        return URLDecoder.decode(Streams.asString(servletServerHttpRequest.getBody(), "utf-8"), "utf-8");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/testVersion")
    @ResponseBody
    public Map<String, String> testVersion(HttpServletRequest request) throws Exception {
        Map<String, String> result = Maps.newHashMap();
        result.put("version", Versions.getVersion());
        result.put("compileTime", Versions.getComplieTime());
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/testGetInput1")
    @ResponseBody
    public String testGetInput1(@RequestBody String body) throws Exception {
        return URLDecoder.decode(body, "utf-8");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/testRedirect")
    public void testRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.sendRedirect("/");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/testProxyIp")
    @ResponseBody
    public Map<String, String> testProxyIp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> map = Maps.newHashMap();
        map.put("getRemoteAddr", request.getRemoteAddr());
        map.put("getRequestURI", request.getRequestURI());
        map.put("getLocalName", request.getLocalAddr());
        map.put("getLocalName", request.getLocalName());
        map.put("getProtocol", request.getProtocol());
        map.put("getServerName", request.getServerName());
        map.put("getScheme", request.getScheme());
        return map;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/testDataForm")
    @ResponseBody
    public Map<String, String[]> testDataForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        return request.getParameterMap();
    }

    @RequestMapping(value = "/testDubbot")
    @ResponseBody
    public String testDubbot() throws Exception {
        return demoService.echo("xxx");
    }

    @RequestMapping(value = "/testServletContext")
    @ResponseBody
    public String testServletContext(HttpServletRequest request) throws Exception {
        return request.getServletContext().getAttribute("app.test2").toString();
    }

    @RequestMapping(value = "/testMonitoredThreadPool")
    @ResponseBody
    public Integer testServletContext(Integer n) throws Exception {
        if (n == null) {
            n = new Random().nextInt(10000);
        }
        for (int i = 0; i < n; i++) {
            monitoredThreadPool.submit(() -> {
                try {
                    TimeUnit.SECONDS.sleep(new Random().nextInt(30));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        return n;
    }

	@RequestMapping(value = "/testRedis")
	@ResponseBody
	public Object testRedis(Integer n) throws Exception {
		String key = "12345678901234567890";
		redisTemplate.delete(key);
		Long value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			RedisSerializer serializer = this.redisTemplate.getKeySerializer();
			byte[] bytes = serializer.serialize(key);
			redisTemplate.getConnectionFactory().getConnection().set(bytes, Long.toString(1l).getBytes());
		} else {
			redisTemplate.opsForValue().increment(key, 1l);
		}
		return redisTemplate.opsForValue();
	}
}
