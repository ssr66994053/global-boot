#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * www.yiji.com Inc.
 * Copyright (c) 2015 All Rights Reserved
 */

package ${package}.test;

import com.yiji.boot.core.Apps;
import com.yiji.boot.test.AppWebTestBase;
import org.springframework.boot.test.SpringApplicationConfiguration;
import ${package}.Main;

/**
 * @author qiubo@yiji.com
 */
@SpringApplicationConfiguration(classes = Main.class)
public abstract class TestBase extends AppWebTestBase {
	protected static final String PROFILE = "sdev";
	
	static {
		Apps.setProfileIfNotExists(PROFILE);
	}
	
}
