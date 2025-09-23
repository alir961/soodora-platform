package com.soodora.service.quote;

import com.soodora.domain.common.Currency;
import com.soodora.domain.quote.CreateQuoteCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

public record CreateQuoteRequest(
        String name,
        String clientName,
        String clientEmail,
        String clientAddress,
        String clientPhone,
        String description,
        Double price,
        Currency currency) implements Request<CreateQuoteCommand> {

    @Override
    public CreateQuoteCommand toCommand() throws InvalidRequestException {
        if (name == null || name.isBlank())
            throw new InvalidRequestException("Quote name is required");
        if (clientName == null || clientName.isBlank())
            throw new InvalidRequestException("Client name is required");
        if (price == null || price <= 0)
            throw new InvalidRequestException("Price is not valid");
        if (currency == null)
            throw new InvalidRequestException("Currency is not valid");

        return new CreateQuoteCommand(name, clientName, clientEmail, clientAddress, clientPhone, description);
    }

}
