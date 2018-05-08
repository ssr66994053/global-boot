package com.global.boot.test.param.converter;

import com.yjf.common.lang.util.money.Money;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;

public class MoneyEditor extends PropertyEditorSupport {
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			Money m = new Money();
			m.setCent(Long.parseLong(text));
			setValue(m);
		} else {
			setValue(null);
		}
	}
	
	@Override
	public String getAsText() {
		return getValue().toString();
	}
}
