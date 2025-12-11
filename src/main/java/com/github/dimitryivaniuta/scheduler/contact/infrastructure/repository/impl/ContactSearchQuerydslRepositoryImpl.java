package com.github.dimitryivaniuta.scheduler.contact.infrastructure.repository.impl;

import com.github.dimitryivaniuta.scheduler.contact.domain.Contact;
import com.github.dimitryivaniuta.scheduler.contact.domain.QContact;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactFilterRequest;
import com.github.dimitryivaniuta.scheduler.contact.infrastructure.repository.ContactSearchQuerydslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContactSearchQuerydslRepositoryImpl implements ContactSearchQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Contact> search(ContactFilterRequest f, Pageable pageable) {
        QContact c = QContact.contact;
        BooleanBuilder predicate = buildPredicate(f, c);

        // count
        Long total = queryFactory
                .select(c.id.count())
                .from(c)
                .where(predicate)
                .fetchOne();

        if (total == null || total == 0L) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // main query
        JPAQuery<Contact> query = queryFactory
                .selectFrom(c)
                .where(predicate);

        List<OrderSpecifier<?>> orderSpecifiers = toOrderSpecifiers(pageable.getSort(), c);
        if (!orderSpecifiers.isEmpty()) {
            query.orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new));
        } else {
            // default sort
            query.orderBy(c.createdAt.desc());
        }

        List<Contact> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    // ---------------- predicate builder ----------------

    private BooleanBuilder buildPredicate(ContactFilterRequest f, QContact c) {
        BooleanBuilder builder = new BooleanBuilder();

        // email
        if (hasText(f.email())) {
            builder.and(c.email.eq(f.email()));
        }
        if (hasText(f.secondaryEmail())) {
            builder.and(c.secondaryEmail.eq(f.secondaryEmail()));
        }

        // names / company
        if (hasText(f.firstName())) {
            builder.and(c.firstName.toLowerCase().like(like(f.firstName())));
        }
        if (hasText(f.middleName())) {
            builder.and(c.middleName.toLowerCase().like(like(f.middleName())));
        }
        if (hasText(f.lastName())) {
            builder.and(c.lastName.toLowerCase().like(like(f.lastName())));
        }
        if (hasText(f.companyName())) {
            builder.and(c.companyName.toLowerCase().like(like(f.companyName())));
        }
        if (hasText(f.jobTitle())) {
            builder.and(c.jobTitle.toLowerCase().like(like(f.jobTitle())));
        }

        // phones
        if (hasText(f.mobilePhone())) {
            builder.and(c.mobilePhone.like(like(f.mobilePhone())));
        }
        if (hasText(f.workPhone())) {
            builder.and(c.workPhone.like(like(f.workPhone())));
        }
        if (hasText(f.homePhone())) {
            builder.and(c.homePhone.like(like(f.homePhone())));
        }

        // address
        if (hasText(f.addressLine1())) {
            builder.and(c.addressLine1.toLowerCase().like(like(f.addressLine1())));
        }
        if (hasText(f.addressLine2())) {
            builder.and(c.addressLine2.toLowerCase().like(like(f.addressLine2())));
        }
        if (hasText(f.city())) {
            builder.and(c.city.toLowerCase().like(like(f.city())));
        }
        if (hasText(f.stateRegion())) {
            builder.and(c.stateRegion.toLowerCase().like(like(f.stateRegion())));
        }
        if (hasText(f.postalCode())) {
            builder.and(c.postalCode.toLowerCase().like(like(f.postalCode())));
        }
        if (hasText(f.countryCode())) {
            builder.and(c.countryCode.toLowerCase().eq(f.countryCode().toLowerCase()));
        }

        // profile
        if (hasText(f.timeZone())) {
            builder.and(c.timeZone.eq(f.timeZone()));
        }
        if (hasText(f.locale())) {
            builder.and(c.locale.eq(f.locale()));
        }
        if (hasText(f.preferredChannel())) {
            builder.and(c.preferredChannel.eq(f.preferredChannel()));
        }

        // tags
        if (hasText(f.tag())) {
            String tag = f.tag().toLowerCase();
            builder.and(c.tags.toLowerCase().contains("," + tag + ","));
        }
        if (hasText(f.tagsContains())) {
            builder.and(c.tags.toLowerCase().like(like(f.tagsContains())));
        }

        // demographics
        if (f.birthDateFrom() != null) {
            builder.and(c.birthDate.goe(f.birthDateFrom()));
        }
        if (f.birthDateTo() != null) {
            builder.and(c.birthDate.loe(f.birthDateTo()));
        }
        if (hasText(f.gender())) {
            builder.and(c.gender.toLowerCase().eq(f.gender().toLowerCase()));
        }

        // booleans / metrics
        if (f.active() != null) {
            builder.and(c.active.eq(f.active()));
        }
        if (f.marketingOptIn() != null) {
            builder.and(c.marketingOptIn.eq(f.marketingOptIn()));
        }
        if (f.unsubscribed() != null) {
            builder.and(c.unsubscribed.eq(f.unsubscribed()));
        }
        if (f.minBounceCount() != null) {
            builder.and(c.bounceCount.goe(f.minBounceCount()));
        }
        if (f.maxBounceCount() != null) {
            builder.and(c.bounceCount.loe(f.maxBounceCount()));
        }

        // dates
        if (f.createdFrom() != null) {
            builder.and(c.createdAt.goe(f.createdFrom()));
        }
        if (f.createdTo() != null) {
            builder.and(c.createdAt.loe(f.createdTo()));
        }
        if (f.lastActivityFrom() != null) {
            builder.and(c.lastActivityAt.goe(f.lastActivityFrom()));
        }
        if (f.lastActivityTo() != null) {
            builder.and(c.lastActivityAt.loe(f.lastActivityTo()));
        }
        if (f.lastEmailedFrom() != null) {
            builder.and(c.lastEmailedAt.goe(f.lastEmailedFrom()));
        }
        if (f.lastEmailedTo() != null) {
            builder.and(c.lastEmailedAt.loe(f.lastEmailedTo()));
        }
        if (f.lastOpenedFrom() != null) {
            builder.and(c.lastOpenedAt.goe(f.lastOpenedFrom()));
        }
        if (f.lastOpenedTo() != null) {
            builder.and(c.lastOpenedAt.loe(f.lastOpenedTo()));
        }
        if (f.lastClickedFrom() != null) {
            builder.and(c.lastClickedAt.goe(f.lastClickedFrom()));
        }
        if (f.lastClickedTo() != null) {
            builder.and(c.lastClickedAt.loe(f.lastClickedTo()));
        }

        // free text
        if (hasText(f.freeText())) {
            String pattern = like(f.freeText());
            builder.andAnyOf(
                    c.email.toLowerCase().like(pattern),
                    c.secondaryEmail.toLowerCase().like(pattern),
                    c.firstName.toLowerCase().like(pattern),
                    c.lastName.toLowerCase().like(pattern),
                    c.companyName.toLowerCase().like(pattern),
                    c.tags.toLowerCase().like(pattern)
            );
        }

        return builder;
    }

    // ---------------- sorting (safe, whitelisted) ----------------

    private List<OrderSpecifier<?>> toOrderSpecifiers(Sort sort, QContact c) {
        List<OrderSpecifier<?>> list = new ArrayList<>();
        if (sort == null || sort.isUnsorted()) {
            return list;
        }

        for (Sort.Order order : sort) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();

            OrderSpecifier<?> spec = switch (property) {
                case "email"          -> new OrderSpecifier<>(direction, c.email);
                case "lastName"       -> new OrderSpecifier<>(direction, c.lastName);
                case "firstName"      -> new OrderSpecifier<>(direction, c.firstName);
                case "companyName"    -> new OrderSpecifier<>(direction, c.companyName);
                case "createdAt"      -> new OrderSpecifier<>(direction, c.createdAt);
                case "lastActivityAt" -> new OrderSpecifier<>(direction, c.lastActivityAt);
                default               -> null; // unknown field -> ignore
            };

            if (spec != null) {
                list.add(spec);
            }
        }

        return list;
    }

    // ---------------- helpers ----------------

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private static String like(String value) {
        return "%" + value.toLowerCase() + "%";
    }
}
