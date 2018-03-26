package com.global.boot.test;/*
* 修订记录:
* qzhanbo@yiji.com 2015-05-28 17:59 创建
*
*/

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author qiubo@yiji.com
 */
@Component
public class DemoCommandLineRunner implements CommandLineRunner {
	
	public void run(String... args) {
		// Do something...
		System.out.println(Arrays.toString(args));
	}
	
}
