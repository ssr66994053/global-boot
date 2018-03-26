package com.global.boot.test.core;

import com.global.boot.test.TestBase;
import com.global.boot.test.TestBean;
import com.global.common.ds.druid.Env;
import com.global.framework.watcher.spring.SpringApplicationContextHolder;
import com.yjf.common.spring.ApplicationContextHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitTests extends TestBase {
	
	@Autowired(required = false)
	private Environment environment;
	
	@Autowired(required = false)
	private TestBean testBean;
	
	@Test
	public void testProfile() throws Exception {
		assertThat(Env.getEnv()).isEqualTo(PROFILE);
	}
	
	@Test
	public void testHera() throws Exception {
		assertThat(testBean.getValueFormHera()).isNotEmpty();
		assertThat(testBean.getValueFormHera1()).isNotEmpty();
	}
	
	@Test
	public void testWatcherSpringApplicationContextHolder() throws Exception {
		assertThat(SpringApplicationContextHolder.get()).isNotNull();
	}
	
	@Test
	public void testSpringApplicationContextHolder() throws Exception {
		assertThat(ApplicationContextHolder.get()).isNotNull();
	}
	
}
