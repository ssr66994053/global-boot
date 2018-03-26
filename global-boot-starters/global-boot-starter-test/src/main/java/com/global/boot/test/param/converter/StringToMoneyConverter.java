package com.global.boot.test.param.converter;

import com.yjf.common.lang.util.money.Money;
import org.springframework.core.convert.converter.Converter;

public class StringToMoneyConverter implements Converter<String, Money> {
    @Override
    public Money convert(String source) {
        Money m = new Money();
        m.setCent(Long.parseLong(source));
        return m;
    }
}
