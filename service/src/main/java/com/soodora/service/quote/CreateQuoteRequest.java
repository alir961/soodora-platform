package com.soodora.service.quote;

import com.soodora.domain.quote.CreateQuoteCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.validateQuote;

public record CreateQuoteRequest(
        @Schema(description = "Quote name", example = "Quote for Company") String name,
        @Schema(description = "Quote Client Name", example = "Company") String clientName,
        @Schema(description = "Quote Client Email", example = "company@example.com") String clientEmail,
        @Schema(description = "Quote Client Address", example = "123 2Pac Road Los Angeles CA") String clientAddress,
        @Schema(description = "Quote Client Phone", example = "+1234567889") String clientPhone,
        @Schema(description = "Quote description", example = "My Service") String description)
        implements
            Request<CreateQuoteCommand> {

    @Override
    public CreateQuoteCommand toCommand() throws InvalidRequestException {
        validateQuote(name, clientName, clientEmail, clientAddress, clientPhone, description);

        return new CreateQuoteCommand(name.trim(), clientName.trim(), clientEmail.trim(), clientAddress.trim(),
                clientPhone.trim(), description.trim());
    }

}
