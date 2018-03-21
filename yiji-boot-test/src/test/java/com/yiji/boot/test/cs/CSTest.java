package com.yiji.boot.test.cs;

import com.yiji.boot.test.TestBase;
import com.yjf.common.id.ID;
import com.yjf.cs.service.api.mq.MailMQClient;
import com.yjf.cs.service.api.mq.ShuntMQClient;
import com.yjf.cs.service.model.MessageType;
import com.yjf.cs.service.order.EmailSenderOrder;
import com.yjf.cs.service.order.ShunMessageOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhouxi@yiji.com
 */
public class CSTest extends TestBase {
	
	@Autowired(required = false)
	private ShuntMQClient shuntMQClient;
	
	@Autowired(required = false)
	private MailMQClient mailMQClient;
	
	@Test
	public void testShuntMessage() {
		ShunMessageOrder messageOrder = new ShunMessageOrder();
		messageOrder.setGid(ID.newID("X0000000"));
		messageOrder.setMerchOrderNo("cs-test");
		messageOrder.setPartnerId("cs-partnerId00000000");
		messageOrder.setType(MessageType.EXETERNAL_HTTP_NOTIFY);
		messageOrder.setCharset("yiji-boot-cs消息测试");
		messageOrder.setUrl("http://www.baidu.com");
		shuntMQClient.notify(messageOrder);
	}
	
	@Test
	public void testMailSend() {
		EmailSenderOrder emailSenderOrder = new EmailSenderOrder();
		emailSenderOrder.setGid(ID.newID("X0000000"));
		emailSenderOrder.setPartnerId("cs-partnerId00000000");
		emailSenderOrder.setMerchOrderNo("cs-test");
		emailSenderOrder.setClientName("yiji_boot_test");
		emailSenderOrder.setContent("yiji-boot-cs邮件测试");
		emailSenderOrder.setSubject("yiji-boot");
		emailSenderOrder.setTo("zhouxi@yiji.com");
		mailMQClient.sendMail(emailSenderOrder);
	}
}
