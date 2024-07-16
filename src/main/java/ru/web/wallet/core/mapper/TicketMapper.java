package ru.web.wallet.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.web.wallet.api.dto.TicketDto;
import ru.web.wallet.core.entity.Ticket;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface TicketMapper {
    TicketDto map(Ticket ticket);

    List<TicketDto> map(List<Ticket> tickets);
}
