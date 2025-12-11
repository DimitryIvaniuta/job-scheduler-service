package com.github.dimitryivaniuta.scheduler.contact.web;

import com.github.dimitryivaniuta.scheduler.contact.domain.Contact;
//import com.github.dimitryivaniuta.scheduler.contact.repository.ContactSearchJooqRepository;
import com.github.dimitryivaniuta.scheduler.contact.service.ContactSearchServiceQuerydsl;
import com.github.dimitryivaniuta.scheduler.contact.service.ContactSearchServiceSpecifications;
//import com.github.dimitryivaniuta.scheduler.contact.service.ContactService;
import com.github.dimitryivaniuta.scheduler.contact.service.ContactService;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactCreateRequest;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactDto;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactFilterRequest;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;
    private final ContactSearchServiceSpecifications specSearchService;
    private final ContactSearchServiceQuerydsl querydslSearchService;
    private final ContactSearchJooqRepository jooqSearchRepository;

    // ---------------- CRUD ----------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactDto create(@RequestBody @Valid ContactCreateRequest request) {
        Contact contact = contactService.create(fromCreateRequest(request));
        return toDto(contact);
    }

    @GetMapping("/{id}")
    public ContactDto get(@PathVariable UUID id) {
        Contact contact = contactService.get(id);
        return toDto(contact);
    }

    @PutMapping("/{id}")
    public ContactDto update(@PathVariable UUID id,
                             @RequestBody @Valid ContactUpdateRequest request) {
        Contact updated = contactService.update(id, fromUpdateRequest(request));
        return toDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        contactService.delete(id);
    }

    // ---------------- SEARCH: Specifications ----------------

    /**
     * Advanced search via Spring Data Specifications.
     * POST body: ContactFilterRequest, pageable via query params (?page=&size=&sort=).
     */
    @PostMapping("/search/spec")
    public Page<ContactDto> searchWithSpecifications(@RequestBody ContactFilterRequest filter,
                                                     Pageable pageable) {
        return specSearchService.search(filter, pageable).map(this::toDto);
    }

    // ---------------- SEARCH: QueryDSL ----------------

    /**
     * Advanced search via QueryDSL JPA (JPAQueryFactory).
     */
    @PostMapping("/search/querydsl")
    public Page<ContactDto> searchWithQuerydsl(@RequestBody ContactFilterRequest filter,
                                               Pageable pageable) {
        return querydslSearchService.search(filter, pageable).map(this::toDto);
    }

    // ---------------- SEARCH: jOOQ ----------------

    /**
     * Advanced search via jOOQ (PostgreSQL-optimized).
     */
    @PostMapping("/search/jooq")
    public Page<ContactDto> searchWithJooq(@RequestBody ContactFilterRequest filter,
                                           Pageable pageable) {
        return jooqSearchRepository.search(filter, pageable).map(this::toDto);
    }

    // ---------------- Mapping helpers ----------------

    private Contact fromCreateRequest(ContactCreateRequest r) {
        return Contact.builder()
                .email(r.email())
                .secondaryEmail(r.secondaryEmail())
                .firstName(r.firstName())
                .middleName(r.middleName())
                .lastName(r.lastName())
                .mobilePhone(r.mobilePhone())
                .workPhone(r.workPhone())
                .homePhone(r.homePhone())
                .companyName(r.companyName())
                .jobTitle(r.jobTitle())
                .addressLine1(r.addressLine1())
                .addressLine2(r.addressLine2())
                .city(r.city())
                .stateRegion(r.stateRegion())
                .postalCode(r.postalCode())
                .countryCode(r.countryCode())
                .timeZone(r.timeZone())
                .locale(r.locale())
                .preferredChannel(r.preferredChannel())
                .tags(r.tags())
                .birthDate(r.birthDate())
                .gender(r.gender())
                .active(r.active() != null ? r.active() : true)
                .marketingOptIn(Boolean.TRUE.equals(r.marketingOptIn()))
                .unsubscribed(Boolean.TRUE.equals(r.unsubscribed()))
                .bounceCount(0)
                .build();
    }

    private Contact fromUpdateRequest(ContactUpdateRequest r) {
        // Full replace; service merges into existing entity.
        return Contact.builder()
                .email(r.email())
                .secondaryEmail(r.secondaryEmail())
                .firstName(r.firstName())
                .middleName(r.middleName())
                .lastName(r.lastName())
                .mobilePhone(r.mobilePhone())
                .workPhone(r.workPhone())
                .homePhone(r.homePhone())
                .companyName(r.companyName())
                .jobTitle(r.jobTitle())
                .addressLine1(r.addressLine1())
                .addressLine2(r.addressLine2())
                .city(r.city())
                .stateRegion(r.stateRegion())
                .postalCode(r.postalCode())
                .countryCode(r.countryCode())
                .timeZone(r.timeZone())
                .locale(r.locale())
                .preferredChannel(r.preferredChannel())
                .tags(r.tags())
                .birthDate(r.birthDate())
                .gender(r.gender())
                .active(r.active() != null && r.active())
                .marketingOptIn(Boolean.TRUE.equals(r.marketingOptIn()))
                .unsubscribed(Boolean.TRUE.equals(r.unsubscribed()))
                .build();
    }

    private ContactDto toDto(Contact c) {
        UUID id = c.getId();
        String email = c.getEmail();
        String secondaryEmail = c.getSecondaryEmail();
        String firstName = c.getFirstName();
        String middleName = c.getMiddleName();
        String lastName = c.getLastName();
        String mobilePhone = c.getMobilePhone();
        String workPhone = c.getWorkPhone();
        String homePhone = c.getHomePhone();
        String companyName = c.getCompanyName();
        String jobTitle = c.getJobTitle();
        String addressLine1 = c.getAddressLine1();
        String addressLine2 = c.getAddressLine2();
        String city = c.getCity();
        String stateRegion = c.getStateRegion();
        String postalCode = c.getPostalCode();
        String countryCode = c.getCountryCode();
        String timeZone = c.getTimeZone();
        String locale = c.getLocale();
        String preferredChannel = c.getPreferredChannel();
        String tags = c.getTags();
        LocalDate birthDate = c.getBirthDate();
        String gender = c.getGender();
        boolean active = c.isActive();
        boolean marketingOptIn = c.isMarketingOptIn();
        boolean unsubscribed = c.isUnsubscribed();
        int bounceCount = c.getBounceCount();
        OffsetDateTime marketingOptInAt = c.getMarketingOptInAt();
        OffsetDateTime unsubscribedAt = c.getUnsubscribedAt();
        OffsetDateTime lastEmailedAt = c.getLastEmailedAt();
        OffsetDateTime lastOpenedAt = c.getLastOpenedAt();
        OffsetDateTime lastClickedAt = c.getLastClickedAt();
        OffsetDateTime lastActivityAt = c.getLastActivityAt();
        OffsetDateTime createdAt = c.getCreatedAt();
        OffsetDateTime updatedAt = c.getUpdatedAt();

        return ContactDto.builder()
                .id(id)
                .email(email)
                .secondaryEmail(secondaryEmail)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .mobilePhone(mobilePhone)
                .workPhone(workPhone)
                .homePhone(homePhone)
                .companyName(companyName)
                .jobTitle(jobTitle)
                .addressLine1(addressLine1)
                .addressLine2(addressLine2)
                .city(city)
                .stateRegion(stateRegion)
                .postalCode(postalCode)
                .countryCode(countryCode)
                .timeZone(timeZone)
                .locale(locale)
                .preferredChannel(preferredChannel)
                .tags(tags)
                .birthDate(birthDate)
                .gender(gender)
                .active(active)
                .marketingOptIn(marketingOptIn)
                .unsubscribed(unsubscribed)
                .bounceCount(bounceCount)
                .marketingOptInAt(marketingOptInAt)
                .unsubscribedAt(unsubscribedAt)
                .lastEmailedAt(lastEmailedAt)
                .lastOpenedAt(lastOpenedAt)
                .lastClickedAt(lastClickedAt)
                .lastActivityAt(lastActivityAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
