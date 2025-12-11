package com.github.dimitryivaniuta.scheduler.contact.web.dto.contact;

import lombok.Builder;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * All fields are optional; only non-null / non-blank ones are applied.
 */
@Builder
public record ContactFilterRequest(

        // identity / basic
        String email,
        String secondaryEmail,
        String firstName,
        String middleName,
        String lastName,

        // phones
        String mobilePhone,
        String workPhone,
        String homePhone,

        // company / job
        String companyName,
        String jobTitle,

        // address
        String addressLine1,
        String addressLine2,
        String city,
        String stateRegion,
        String postalCode,
        String countryCode,

        // profile
        String timeZone,
        String locale,
        String preferredChannel,
        String tag,              // search by single tag
        String tagsContains,     // substring search across all tags

        // demographics
        LocalDate birthDateFrom,
        LocalDate birthDateTo,
        String gender,

        // consent / engagement
        Boolean active,
        Boolean marketingOptIn,
        Boolean unsubscribed,
        Integer minBounceCount,
        Integer maxBounceCount,

        OffsetDateTime createdFrom,
        OffsetDateTime createdTo,

        OffsetDateTime lastActivityFrom,
        OffsetDateTime lastActivityTo,

        OffsetDateTime lastEmailedFrom,
        OffsetDateTime lastEmailedTo,

        OffsetDateTime lastOpenedFrom,
        OffsetDateTime lastOpenedTo,

        OffsetDateTime lastClickedFrom,
        OffsetDateTime lastClickedTo,

        // global free-text (name/email/company/tags)
        String freeText
) {
}
