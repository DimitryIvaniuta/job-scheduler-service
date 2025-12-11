package com.github.dimitryivaniuta.scheduler.contact.infrastructure.repository.impl;

import com.github.dimitryivaniuta.jooq.tables.Contacts;
import com.github.dimitryivaniuta.scheduler.contact.domain.Contact;
import com.github.dimitryivaniuta.scheduler.contact.dto.ContactFilterRequest;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ContactSearchJooqRepositoryImpl implements ContactSearchJooqRepository {

    private static final Contacts C = Contacts.CONTACTS;

    private final DSLContext dsl;

    @Override
    public Page<Contact> search(ContactFilterRequest filter, Pageable pageable) {
        Condition condition = buildCondition(filter);

        Long total = dsl.selectCount()
                .from(C)
                .where(condition)
                .fetchOne(0, Long.class);

        if (total == null || total == 0L) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        SelectConditionStep<Record> base = dsl
                .selectFrom(C)
                .where(condition);

        List<SortField<?>> sortFields = toSortFields(pageable.getSort());
        if (!sortFields.isEmpty()) {
            base.orderBy(sortFields);
        } else {
            base.orderBy(C.CREATED_AT.desc());
        }

        List<Contact> content = base
                .offset((int) pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(this::mapRecordToContact);

        return new PageImpl<>(content, pageable, total);
    }

    // ---------------- condition builder ----------------

    private Condition buildCondition(ContactFilterRequest f) {
        Condition condition = DSL.trueCondition();

        // emails (citext in DB, equality here)
        if (hasText(f.email())) {
            condition = condition.and(C.EMAIL.eq(f.email()));
        }
        if (hasText(f.secondaryEmail())) {
            condition = condition.and(C.SECONDARY_EMAIL.eq(f.secondaryEmail()));
        }

        // names / company
        if (hasText(f.firstName())) {
            condition = condition.and(ilike(C.FIRST_NAME, f.firstName()));
        }
        if (hasText(f.middleName())) {
            condition = condition.and(ilike(C.MIDDLE_NAME, f.middleName()));
        }
        if (hasText(f.lastName())) {
            condition = condition.and(ilike(C.LAST_NAME, f.lastName()));
        }
        if (hasText(f.companyName())) {
            condition = condition.and(ilike(C.COMPANY_NAME, f.companyName()));
        }
        if (hasText(f.jobTitle())) {
            condition = condition.and(ilike(C.JOB_TITLE, f.jobTitle()));
        }

        // phones
        if (hasText(f.mobilePhone())) {
            condition = condition.and(ilike(C.MOBILE_PHONE, f.mobilePhone()));
        }
        if (hasText(f.workPhone())) {
            condition = condition.and(ilike(C.WORK_PHONE, f.workPhone()));
        }
        if (hasText(f.homePhone())) {
            condition = condition.and(ilike(C.HOME_PHONE, f.homePhone()));
        }

        // address
        if (hasText(f.addressLine1())) {
            condition = condition.and(ilike(C.ADDRESS_LINE1, f.addressLine1()));
        }
        if (hasText(f.addressLine2())) {
            condition = condition.and(ilike(C.ADDRESS_LINE2, f.addressLine2()));
        }
        if (hasText(f.city())) {
            condition = condition.and(ilike(C.CITY, f.city()));
        }
        if (hasText(f.stateRegion())) {
            condition = condition.and(ilike(C.STATE_REGION, f.stateRegion()));
        }
        if (hasText(f.postalCode())) {
            condition = condition.and(ilike(C.POSTAL_CODE, f.postalCode()));
        }
        if (hasText(f.countryCode())) {
            condition = condition.and(DSL.lower(C.COUNTRY_CODE).eq(f.countryCode().toLowerCase()));
        }

        // profile
        if (hasText(f.timeZone())) {
            condition = condition.and(C.TIME_ZONE.eq(f.timeZone()));
        }
        if (hasText(f.locale())) {
            condition = condition.and(C.LOCALE.eq(f.locale()));
        }
        if (hasText(f.preferredChannel())) {
            condition = condition.and(C.PREFERRED_CHANNEL.eq(f.preferredChannel()));
        }

        // tags
        if (hasText(f.tag())) {
            String pattern = "%," + f.tag().toLowerCase() + ",%";
            condition = condition.and(
                    DSL.lower(DSL.concat(",", C.TAGS, ",")).like(pattern)
            );
        }
        if (hasText(f.tagsContains())) {
            condition = condition.and(ilike(C.TAGS, f.tagsContains()));
        }

        // demographics
        if (f.birthDateFrom() != null) {
            condition = condition.and(C.BIRTH_DATE.ge(f.birthDateFrom()));
        }
        if (f.birthDateTo() != null) {
            condition = condition.and(C.BIRTH_DATE.le(f.birthDateTo()));
        }
        if (hasText(f.gender())) {
            condition = condition.and(DSL.lower(C.GENDER).eq(f.gender().toLowerCase()));
        }

        // booleans / metrics
        if (f.active() != null) {
            condition = condition.and(C.IS_ACTIVE.eq(f.active()));
        }
        if (f.marketingOptIn() != null) {
            condition = condition.and(C.MARKETING_OPT_IN.eq(f.marketingOptIn()));
        }
        if (f.unsubscribed() != null) {
            condition = condition.and(C.UNSUBSCRIBED.eq(f.unsubscribed()));
        }
        if (f.minBounceCount() != null) {
            condition = condition.and(C.BOUNCE_COUNT.ge(f.minBounceCount()));
        }
        if (f.maxBounceCount() != null) {
            condition = condition.and(C.BOUNCE_COUNT.le(f.maxBounceCount()));
        }

        // date ranges
        if (f.createdFrom() != null) {
            condition = condition.and(C.CREATED_AT.ge(f.createdFrom()));
        }
        if (f.createdTo() != null) {
            condition = condition.and(C.CREATED_AT.le(f.createdTo()));
        }
        if (f.lastActivityFrom() != null) {
            condition = condition.and(C.LAST_ACTIVITY_AT.ge(f.lastActivityFrom()));
        }
        if (f.lastActivityTo() != null) {
            condition = condition.and(C.LAST_ACTIVITY_AT.le(f.lastActivityTo()));
        }
        if (f.lastEmailedFrom() != null) {
            condition = condition.and(C.LAST_EMAILED_AT.ge(f.lastEmailedFrom()));
        }
        if (f.lastEmailedTo() != null) {
            condition = condition.and(C.LAST_EMAILED_AT.le(f.lastEmailedTo()));
        }
        if (f.lastOpenedFrom() != null) {
            condition = condition.and(C.LAST_OPENED_AT.ge(f.lastOpenedFrom()));
        }
        if (f.lastOpenedTo() != null) {
            condition = condition.and(C.LAST_OPENED_AT.le(f.lastOpenedTo()));
        }
        if (f.lastClickedFrom() != null) {
            condition = condition.and(C.LAST_CLICKED_AT.ge(f.lastClickedFrom()));
        }
        if (f.lastClickedTo() != null) {
            condition = condition.and(C.LAST_CLICKED_AT.le(f.lastClickedTo()));
        }

        // free text: backed by pg_trgm on email/name/company/tags
        if (hasText(f.freeText())) {
            String pattern = "%" + f.freeText().toLowerCase() + "%";
            condition = condition.and(
                    DSL.or(
                            DSL.lower(C.EMAIL).like(pattern),
                            DSL.lower(C.SECONDARY_EMAIL).like(pattern),
                            DSL.lower(C.FIRST_NAME).like(pattern),
                            DSL.lower(C.LAST_NAME).like(pattern),
                            DSL.lower(C.COMPANY_NAME).like(pattern),
                            DSL.lower(C.TAGS).like(pattern)
                    )
            );
        }

        return condition;
    }

    // ---------------- sorting ----------------

    private List<SortField<?>> toSortFields(Sort sort) {
        List<SortField<?>> fields = new ArrayList<>();
        if (sort == null || sort.isUnsorted()) {
            return fields;
        }

        for (Sort.Order o : sort) {
            boolean asc = o.isAscending();
            String prop = o.getProperty();

            SortField<?> sf = switch (prop) {
                case "email" -> asc ? C.EMAIL.asc() : C.EMAIL.desc();
                case "lastName" -> asc ? C.LAST_NAME.asc() : C.LAST_NAME.desc();
                case "firstName" -> asc ? C.FIRST_NAME.asc() : C.FIRST_NAME.desc();
                case "companyName" -> asc ? C.COMPANY_NAME.asc() : C.COMPANY_NAME.desc();
                case "createdAt" -> asc ? C.CREATED_AT.asc() : C.CREATED_AT.desc();
                case "lastActivityAt" -> asc ? C.LAST_ACTIVITY_AT.asc() : C.LAST_ACTIVITY_AT.desc();
                default -> null;
            };

            if (sf != null) {
                fields.add(sf);
            }
        }

        return fields;
    }

    // ---------------- mapper ----------------

    private Contact mapRecordToContact(Record r) {
        return Contact.builder()
                .id(r.get(C.ID, UUID.class))
                .email(r.get(C.EMAIL))
                .secondaryEmail(r.get(C.SECONDARY_EMAIL))
                .firstName(r.get(C.FIRST_NAME))
                .middleName(r.get(C.MIDDLE_NAME))
                .lastName(r.get(C.LAST_NAME))
                .mobilePhone(r.get(C.MOBILE_PHONE))
                .workPhone(r.get(C.WORK_PHONE))
                .homePhone(r.get(C.HOME_PHONE))
                .companyName(r.get(C.COMPANY_NAME))
                .jobTitle(r.get(C.JOB_TITLE))
                .addressLine1(r.get(C.ADDRESS_LINE1))
                .addressLine2(r.get(C.ADDRESS_LINE2))
                .city(r.get(C.CITY))
                .stateRegion(r.get(C.STATE_REGION))
                .postalCode(r.get(C.POSTAL_CODE))
                .countryCode(r.get(C.COUNTRY_CODE))
                .timeZone(r.get(C.TIME_ZONE))
                .locale(r.get(C.LOCALE))
                .preferredChannel(r.get(C.PREFERRED_CHANNEL))
                .tags(r.get(C.TAGS))
                .birthDate(r.get(C.BIRTH_DATE, LocalDate.class))
                .gender(r.get(C.GENDER))
                .active(Boolean.TRUE.equals(r.get(C.IS_ACTIVE)))
                .marketingOptIn(Boolean.TRUE.equals(r.get(C.MARKETING_OPT_IN)))
                .marketingOptInAt(r.get(C.MARKETING_OPT_IN_AT, OffsetDateTime.class))
                .unsubscribed(Boolean.TRUE.equals(r.get(C.UNSUBSCRIBED)))
                .unsubscribedAt(r.get(C.UNSUBSCRIBED_AT, OffsetDateTime.class))
                .bounceCount(coalesce(r.get(C.BOUNCE_COUNT), 0))
                .lastEmailedAt(r.get(C.LAST_EMAILED_AT, OffsetDateTime.class))
                .lastOpenedAt(r.get(C.LAST_OPENED_AT, OffsetDateTime.class))
                .lastClickedAt(r.get(C.LAST_CLICKED_AT, OffsetDateTime.class))
                .lastActivityAt(r.get(C.LAST_ACTIVITY_AT, OffsetDateTime.class))
                .createdAt(r.get(C.CREATED_AT, OffsetDateTime.class))
                .updatedAt(r.get(C.UPDATED_AT, OffsetDateTime.class))
                .build();
    }

    // ---------------- helpers ----------------

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static Condition ilike(org.jooq.TableField<?, String> field, String value) {
        String pattern = "%" + value.toLowerCase() + "%";
        return DSL.lower(field).like(pattern);
    }

    private static int coalesce(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }
}
