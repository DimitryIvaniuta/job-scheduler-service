package com.github.dimitryivaniuta.scheduler.contact.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ContactUpdateRequest(
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
        Boolean active,
        Boolean marketingOptIn,
        Boolean unsubscribed
) {
}
