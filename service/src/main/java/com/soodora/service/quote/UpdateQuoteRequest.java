package com.soodora.service.quote;

import com.soodora.domain.quote.UpdateQuoteCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

public record UpdateQuoteRequest(
        String name,
        String clientName,
        String clientEmail,
        String clientAddress,
        String clientPhone,
        String description) implements Request<UpdateQuoteCommand> {

    @Override
    public UpdateQuoteCommand toCommand() throws InvalidRequestException {
        return new UpdateQuoteCommand(name, clientName, clientEmail, clientAddress, clientPhone, description);
    }

}
