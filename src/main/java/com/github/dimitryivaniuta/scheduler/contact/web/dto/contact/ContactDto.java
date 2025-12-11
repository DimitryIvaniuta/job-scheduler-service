package com.github.dimitryivaniuta.scheduler.contact.web.dto.contact;

import lombok.Builder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ContactDto(
        UUID id,
        String email,
        String secondaryEmail,
        String firstName,
        String middleName,
        String lastName,
        String mobilePhone,
        String workPhone,
        String homePhone,
        String companyName,
        String jobTitle,
        String addressLine1,
        String addressLine2,
        String city,
        String stateRegion,
        String postalCode,
        String countryCode,
        String timeZone,
        String locale,
        String preferredChannel,
        String tags,
        LocalDate birthDate,
        String gender,
        boolean active,
        boolean marketingOptIn,
        boolean unsubscribed,
        int bounceCount,
        OffsetDateTime marketingOptInAt,
        OffsetDateTime unsubscribedAt,
        OffsetDateTime lastEmailedAt,
        OffsetDateTime lastOpenedAt,
        OffsetDateTime lastClickedAt,
        OffsetDateTime lastActivityAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
