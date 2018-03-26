package com.global.boot.test;

import com.global.boot.core.Apps;
import com.global.boot.core.YijiBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ImportResource;

@YijiBootApplication(sysName = "global-boot-test", httpPort = 9832)
@ImportResource({ "classpath*:spring/test.xml" })
public class Main {
	public static void main(String[] args) {
		Apps.setProfileIfNotExists("stest");
		new SpringApplication(Main.class).run(args);
	}
}
