package ru.web.wallet.core.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import ru.web.wallet.api.TicketType;
import ru.web.wallet.core.exception.AppError;
import ru.web.wallet.core.exception.ServiceException;

public class StringToEnumConverterTicketType implements Converter<String, TicketType> {
    @Override
    public TicketType convert(String source) {
        try {
            return TicketType.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(new AppError(HttpStatus.BAD_REQUEST.value(), "Invalid transfer type"));
        }
    }
}