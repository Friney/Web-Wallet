package ru.web.wallet.core.converter;

import org.springframework.core.convert.converter.Converter;
import ru.web.wallet.api.TicketStatus;
import org.springframework.http.HttpStatus;
import ru.web.wallet.core.exception.AppError;
import ru.web.wallet.core.exception.ServiceException;


public class StringToEnumConverterTicketStatus implements Converter<String, TicketStatus> {
    @Override
    public TicketStatus convert(String source) {
        try {
            return TicketStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(new AppError(HttpStatus.BAD_REQUEST.value(), "Invalid transfer status"));
        }

    }

}