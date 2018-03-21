package com.yiji.boot.test.cas;

import com.yiji.authcenter.factory.CasShiroFilterFactoryBean;
import com.yiji.boot.test.TestBase;
import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by shuijing on 2016/7/7
 */
public class CASTest extends TestBase {

    @Autowired(required = false)
    private CasShiroFilterFactoryBean casShiroFilterFactoryBean;

    private Logger logger = LoggerFactory.getLogger(CASTest.class);

    @Test
    public void testAnno() throws IOException {
        assertGetBodyIs("css/anno", "anno");
    }

    @Test
    public void tesImagesEnable() throws IOException {
        assertGetBodyIs("images/anno", "pass");
    }

    @Test
    public void testBossEnable() throws IOException {
        assertGetBodyIs("boss/anno", "pass");
    }

    @Test
    public void testFilterBean() {
        assertThat(casShiroFilterFactoryBean.getFilters().get("casFilter")).isNotEqualTo(null);
        assertThat(casShiroFilterFactoryBean.getFilters().get("casLogoutFilter")).isNotEqualTo(null);
    }

    @Test
    public void testCasLogin() {
        assertGetBodyIs("/login", "pass");
    }
    /*@Test
    public void testCasLogin() {
        assertGetBodyIs("casLogin?userName=abc", "abc success");
    }*/
}
