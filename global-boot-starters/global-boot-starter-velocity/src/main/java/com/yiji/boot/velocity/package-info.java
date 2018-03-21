/*
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */

/*
 * 修订记录:
 * qzhanbo@yiji.com 2014-12-31 01:25 创建
 *
 */
/**
 * web容器velocity模板组件：
 * <li>1.优化toolbox加载，所以可以缓存，每次从文件加载很费时啊。</li>
 * <li>2.支持从classpath中加载toolbox</li>
 * <li>3.鉴于分布式session的性能考虑，禁止使用session scope的toolbox</li>
 * <li>4.鉴于分布式session的性能考虑，不准许暴露session属性到model</li>
 * <li>5.引用处理失败时打印warn日志</li>
 * <li>6.支持在vm页面中使用$!esc.html("<script>alert(111)</script>")代码escape dom，防止xss.
 * 更多参考 {@link org.apache.velocity.tools.generic.EscapeTool}</li>
 * <li>7.html escape 参考{@link com.yiji.boot.core.velocity.HtmlEscapeReference}</li>
 * @author qzhanbo@yiji.com
 */
package com.yiji.boot.velocity;