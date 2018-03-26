package com.global.boot.test;/*
* 修订记录:
* qzhanbo@yiji.com 2015-05-28 15:52 创建
*
*/

import com.alibaba.fastjson.annotation.JSONField;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author qiubo@yiji.com
 */
@XmlRootElement
public class TestBean {
	private String name;
	private Date date = new Date();
	private String valueFormHera;
	private String valueFormHera1;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@JSONField(name = TestContansts.FAST_JSON_PROP_NAME)
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getValueFormHera() {
		return valueFormHera;
	}
	
	public void setValueFormHera(String valueFormHera) {
		this.valueFormHera = valueFormHera;
	}
	
	public String getValueFormHera1() {
		return valueFormHera1;
	}
	
	public void setValueFormHera1(String valueFormHera1) {
		this.valueFormHera1 = valueFormHera1;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TestBean{");
		sb.append("date=").append(date);
		sb.append(", name='").append(name).append('\'');
		sb.append(", valueFormHera='").append(valueFormHera).append('\'');
		sb.append(", valueFormHera1='").append(valueFormHera1).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
