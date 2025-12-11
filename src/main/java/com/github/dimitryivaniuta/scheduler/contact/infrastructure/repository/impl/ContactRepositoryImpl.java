package com.github.dimitryivaniuta.scheduler.contact.infrastructure.repository.impl;

import com.github.dimitryivaniuta.scheduler.contact.domain.Contact;
import com.github.dimitryivaniuta.scheduler.contact.domain.QContact;
import com.github.dimitryivaniuta.scheduler.contact.infrastructure.repository.ContactRepositoryCustom;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactFilterRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContactRepositoryImpl implements ContactRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Contact> searchByFilterQuerydsl(ContactFilterRequest f, Pageable pageable) {
        QContact c = QContact.contact;

        BooleanBuilder predicate = buildPredicate(f, c);

        // base query
        JPAQuery<Contact> baseQuery = queryFactory
                .selectFrom(c)
                .where(predicate);

        // total count
        Long total = queryFactory
                .select(c.id.count())
                .from(c)
                .where(predicate)
                .fetchOne();

        if (total == null || total == 0L) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // apply pagination + sorting
        List<OrderSpecifier<?>> orderSpecifiers = toOrderSpecifiers(pageable.getSort(), c);

        if (!orderSpecifiers.isEmpty()) {
            baseQuery.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
        }

        List<Contact> content = baseQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder buildPredicate(ContactFilterRequest f, QContact c) {
        BooleanBuilder builder = new BooleanBuilder();

        if (hasText(f.email())) {
            builder.and(c.email.eq(f.email()));
        }
        if (hasText(f.secondaryEmail())) {
            builder.and(c.secondaryEmail.eq(f.secondaryEmail()));
        }

        if (hasText(f.firstName())) {
            builder.and(c.firstName.toLowerCase().like("%" + f.firstName().toLowerCase() + "%"));
        }
        if (hasText(f.middleName())) {
            builder.and(c.middleName.toLowerCase().like("%" + f.middleName().toLowerCase() + "%"));
        }
        if (hasText(f.lastName())) {
            builder.and(c.lastName.toLowerCase().like("%" + f.lastName().toLowerCase() + "%"));
        }

        if (hasText(f.companyName())) {
            builder.and(c.companyName.toLowerCase().like("%" + f.companyName().toLowerCase() + "%"));
        }
        if (hasText(f.jobTitle())) {
            builder.and(c.jobTitle.toLowerCase().like("%" + f.jobTitle().toLowerCase() + "%"));
        }

        if (hasText(f.mobilePhone())) {
            builder.and(c.mobilePhone.like("%" + f.mobilePhone() + "%"));
        }
        if (hasText(f.workPhone())) {
            builder.and(c.workPhone.like("%" + f.workPhone() + "%"));
        }
        if (hasText(f.homePhone())) {
            builder.and(c.homePhone.like("%" + f.homePhone() + "%"));
        }

        if (hasText(f.addressLine1())) {
            builder.and(c.addressLine1.toLowerCase().like("%" + f.addressLine1().toLowerCase() + "%"));
        }
        if (hasText(f.addressLine2())) {
            builder.and(c.addressLine2.toLowerCase().like("%" + f.addressLine2().toLowerCase() + "%"));
        }
        if (hasText(f.city())) {
            builder.and(c.city.toLowerCase().like("%" + f.city().toLowerCase() + "%"));
        }
        if (hasText(f.stateRegion())) {
            builder.and(c.stateRegion.toLowerCase().like("%" + f.stateRegion().toLowerCase() + "%"));
        }
        if (hasText(f.postalCode())) {
            builder.and(c.postalCode.toLowerCase().like("%" + f.postalCode().toLowerCase() + "%"));
        }
        if (hasText(f.countryCode())) {
            builder.and(c.countryCode.equalsIgnoreCase(f.countryCode()));
        }

        if (hasText(f.timeZone())) {
            builder.and(c.timeZone.eq(f.timeZone()));
        }
        if (hasText(f.locale())) {
            builder.and(c.locale.eq(f.locale()));
        }
        if (hasText(f.preferredChannel())) {
            builder.and(c.preferredChannel.eq(f.preferredChannel()));
        }

        if (hasText(f.tag())) {
            String tagPattern = "," + f.tag().toLowerCase() + ",";
            builder.and(c.tags.toLowerCase().contains(tagPattern));
        }
        if (hasText(f.tagsContains())) {
            builder.and(c.tags.toLowerCase().like("%" + f.tagsContains().toLowerCase() + "%"));
        }

        if (f.birthDateFrom() != null) {
            builder.and(c.birthDate.goe(f.birthDateFrom()));
        }
        if (f.birthDateTo() != null) {
            builder.and(c.birthDate.loe(f.birthDateTo()));
        }

        if (hasText(f.gender())) {
            builder.and(c.gender.toLowerCase().eq(f.gender().toLowerCase()));
        }

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

        if (hasText(f.freeText())) {
            String pattern = "%" + f.freeText().toLowerCase() + "%";
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<OrderSpecifier<?>> toOrderSpecifiers(Sort sort, QContact c) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sort == null || sort.isUnsorted()) {
            return orders;
        }

        // use QContact metadata so alias matches the generated Q-type
        PathBuilder<Contact> entityPath = new PathBuilder<>(Contact.class, c.getMetadata());

        for (Sort.Order order : sort) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String property = order.getProperty();

            // Dynamic property -> generic type is unknown at compile-time
            Expression<?> path = entityPath.get(property);  // SimpleExpression / Path

            // Cast is required because OrderSpecifier<T> expects Expression<? extends Comparable<?>>
            orders.add(new OrderSpecifier(direction, path));
        }

        return orders;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
