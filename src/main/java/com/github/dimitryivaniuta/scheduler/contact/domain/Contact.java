package com.github.dimitryivaniuta.scheduler.contact.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "contacts",
        indexes = {
                @Index(name = "idx_contacts_email", columnList = "email"),
                @Index(name = "idx_contacts_secondary_email", columnList = "secondary_email"),
                @Index(name = "idx_contacts_last_first", columnList = "last_name, first_name"),
                @Index(name = "idx_contacts_company", columnList = "company_name"),
                @Index(name = "idx_contacts_country_city", columnList = "country_code, city"),
                @Index(name = "idx_contacts_active_marketing", columnList = "is_active, marketing_opt_in"),
                @Index(name = "idx_contacts_created_at", columnList = "created_at"),
                @Index(name = "idx_contacts_last_activity_at", columnList = "last_activity_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    /** primary email – in DB use citext + unique index for case-insensitive uniqueness */
    @Column(nullable = false, length = 320, unique = true)
    private String email;

    /** secondary email, optional */
    @Column(name = "secondary_email", length = 320)
    private String secondaryEmail;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    /** phones */
    @Column(name = "mobile_phone", length = 50)
    private String mobilePhone;

    @Column(name = "work_phone", length = 50)
    private String workPhone;

    @Column(name = "home_phone", length = 50)
    private String homePhone;

    /** company / job */
    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "job_title", length = 150)
    private String jobTitle;

    /** address */
    @Column(name = "address_line1", length = 200)
    private String addressLine1;

    @Column(name = "address_line2", length = 200)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(name = "state_region", length = 100)
    private String stateRegion;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /** ISO 3166-1 alpha-2 country code */
    @Column(name = "country_code", length = 2)
    private String countryCode;

    /** profile & segmentation */
    @Column(name = "time_zone", length = 64)
    private String timeZone;          // e.g. "Europe/Warsaw"

    @Column(name = "locale", length = 10)
    private String locale;            // e.g. "en_US"

    @Column(name = "preferred_channel", length = 32)
    private String preferredChannel;  // e.g. "EMAIL", "SMS"

    @Column(name = "tags", length = 512)
    private String tags; // comma-separated tags for simple use; or later switch to JSONB

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", length = 20)
    private String gender;

    /** consent & engagement */
    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "marketing_opt_in", nullable = false)
    private boolean marketingOptIn;

    @Column(name = "marketing_opt_in_at")
    private OffsetDateTime marketingOptInAt;

    @Column(name = "unsubscribed", nullable = false)
    private boolean unsubscribed;

    @Column(name = "unsubscribed_at")
    private OffsetDateTime unsubscribedAt;

    @Column(name = "bounce_count", nullable = false)
    private int bounceCount;

    @Column(name = "last_emailed_at")
    private OffsetDateTime lastEmailedAt;

    @Column(name = "last_opened_at")
    private OffsetDateTime lastOpenedAt;

    @Column(name = "last_clicked_at")
    private OffsetDateTime lastClickedAt;

    @Column(name = "last_activity_at")
    private OffsetDateTime lastActivityAt;

    /** audit */
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (!this.active) {
            this.active = true;
        }
        // default marketingOptIn/unsubscribed can stay false
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
        this.lastActivityAt = OffsetDateTime.now();
    }
}
