package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.CreateMediaKitCommand;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.common.Request;

public record CreateMediaKitRequest(String title,
        String shortBio,
        String longBio,
        String contactEmail,
        String contactPhone) implements Request<CreateMediaKitCommand> {

    @Override
    public CreateMediaKitCommand toCommand() {
        if (title == null || title.isEmpty())
            throw new InvalidRequestException("Title is not valid");
        if (shortBio == null || shortBio.isEmpty())
            throw new InvalidRequestException("Short Bio is not valid");
        if (longBio == null || longBio.isEmpty())
            throw new InvalidRequestException("Long Bio is not valid");
        if (contactEmail == null || contactEmail.isEmpty())
            throw new InvalidRequestException("Contact Email is not valid");
        if (contactPhone == null || contactPhone.isEmpty())
            throw new InvalidRequestException("Contact Phone is not valid");

        return new CreateMediaKitCommand(title.trim(), shortBio.trim(), longBio.trim(), contactEmail.trim(),
                contactPhone.trim());
    }
}
