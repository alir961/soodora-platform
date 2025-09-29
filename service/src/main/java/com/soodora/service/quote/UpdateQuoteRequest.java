package com.soodora.service.quote;

import com.soodora.domain.quote.UpdateQuoteCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.soodora.service.validation.ValidationUtils.isStringValid;

public record UpdateQuoteRequest(
        @Schema(description = "Quote name", example = "Quote for Company") String name,
        @Schema(description = "Quote Client Name", example = "Company") String clientName,
        @Schema(description = "Quote Client Email", example = "company@example.com") String clientEmail,
        @Schema(description = "Quote Client Address", example = "123 2Pac Road Los Angeles CA") String clientAddress,
        @Schema(description = "Quote Client Phone", example = "+1234567889") String clientPhone,
        @Schema(description = "Quote description", example = "My Service") String description)
        implements
            Request<UpdateQuoteCommand> {

    @Override
    public UpdateQuoteCommand toCommand() throws InvalidRequestException {
        if (!isStringValid(name, 255, true))
            throw new InvalidRequestException("Name cannot exceed 255 characters");
        if (!isStringValid(clientName, 255, true))
            throw new InvalidRequestException("Client Name cannot exceed 255 characters");
        if (!isStringValid(clientEmail, 255, true))
            throw new InvalidRequestException("Client Email cannot exceed 255 characters");
        if (!isStringValid(clientAddress, 255, true))
            throw new InvalidRequestException("Client Address cannot exceed 255 characters");
        if (!isStringValid(clientPhone, 50, true))
            throw new InvalidRequestException("Client Phone cannot exceed 255 characters");
        if (!isStringValid(description, 255, true))
            throw new InvalidRequestException("Description cannot exceed 255 characters");

        return new UpdateQuoteCommand(name, clientName, clientEmail, clientAddress, clientPhone, description);
    }

}
