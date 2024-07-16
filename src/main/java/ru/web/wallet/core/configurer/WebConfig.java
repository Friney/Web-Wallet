package ru.web.wallet.core.configurer;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.web.wallet.core.converter.StringToEnumConverterTicketStatus;
import ru.web.wallet.core.converter.StringToEnumConverterTicketType;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverterTicketType());
        registry.addConverter(new StringToEnumConverterTicketStatus());
    }
}