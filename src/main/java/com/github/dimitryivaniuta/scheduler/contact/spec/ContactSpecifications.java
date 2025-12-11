package com.github.dimitryivaniuta.scheduler.contact.spec;

import com.github.dimitryivaniuta.scheduler.contact.domain.Contact;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ContactSpecifications {

    private ContactSpecifications() {
    }

    public static Specification<Contact> fromFilter(ContactFilterRequest f) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(f.email())) {
                predicates.add(cb.equal(root.get("email"), f.email()));
            }
            if (hasText(f.secondaryEmail())) {
                predicates.add(cb.equal(root.get("secondaryEmail"), f.secondaryEmail()));
            }

            if (hasText(f.firstName())) {
                predicates.add(ilike(cb, root.get("firstName"), f.firstName()));
            }
            if (hasText(f.middleName())) {
                predicates.add(ilike(cb, root.get("middleName"), f.middleName()));
            }
            if (hasText(f.lastName())) {
                predicates.add(ilike(cb, root.get("lastName"), f.lastName()));
            }

            if (hasText(f.companyName())) {
                predicates.add(ilike(cb, root.get("companyName"), f.companyName()));
            }
            if (hasText(f.jobTitle())) {
                predicates.add(ilike(cb, root.get("jobTitle"), f.jobTitle()));
            }

            if (hasText(f.mobilePhone())) {
                predicates.add(ilike(cb, root.get("mobilePhone"), f.mobilePhone()));
            }
            if (hasText(f.workPhone())) {
                predicates.add(ilike(cb, root.get("workPhone"), f.workPhone()));
            }
            if (hasText(f.homePhone())) {
                predicates.add(ilike(cb, root.get("homePhone"), f.homePhone()));
            }

            if (hasText(f.addressLine1())) {
                predicates.add(ilike(cb, root.get("addressLine1"), f.addressLine1()));
            }
            if (hasText(f.addressLine2())) {
                predicates.add(ilike(cb, root.get("addressLine2"), f.addressLine2()));
            }
            if (hasText(f.city())) {
                predicates.add(ilike(cb, root.get("city"), f.city()));
            }
            if (hasText(f.stateRegion())) {
                predicates.add(ilike(cb, root.get("stateRegion"), f.stateRegion()));
            }
            if (hasText(f.postalCode())) {
                predicates.add(ilike(cb, root.get("postalCode"), f.postalCode()));
            }
            if (hasText(f.countryCode())) {
                predicates.add(cb.equal(
                        cb.lower(root.get("countryCode")),
                        f.countryCode().toLowerCase()
                ));
            }

            if (hasText(f.timeZone())) {
                predicates.add(cb.equal(root.get("timeZone"), f.timeZone()));
            }
            if (hasText(f.locale())) {
                predicates.add(cb.equal(root.get("locale"), f.locale()));
            }
            if (hasText(f.preferredChannel())) {
                predicates.add(cb.equal(root.get("preferredChannel"), f.preferredChannel()));
            }

            if (hasText(f.tag())) {
                String pattern = "%," + f.tag().toLowerCase() + ",%";
                predicates.add(cb.like(
                        cb.concat(cb.concat(",", cb.lower(root.get("tags"))), ","),
                        pattern
                ));
            }
            if (hasText(f.tagsContains())) {
                predicates.add(ilike(cb, root.get("tags"), f.tagsContains()));
            }

            if (f.birthDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), f.birthDateFrom()));
            }
            if (f.birthDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), f.birthDateTo()));
            }
            if (hasText(f.gender())) {
                predicates.add(cb.equal(cb.lower(root.get("gender")), f.gender().toLowerCase()));
            }

            if (f.active() != null) {
                predicates.add(cb.equal(root.get("active"), f.active()));
            }
            if (f.marketingOptIn() != null) {
                predicates.add(cb.equal(root.get("marketingOptIn"), f.marketingOptIn()));
            }
            if (f.unsubscribed() != null) {
                predicates.add(cb.equal(root.get("unsubscribed"), f.unsubscribed()));
            }
            if (f.minBounceCount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bounceCount"), f.minBounceCount()));
            }
            if (f.maxBounceCount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("bounceCount"), f.maxBounceCount()));
            }

            if (f.createdFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), f.createdFrom()));
            }
            if (f.createdTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), f.createdTo()));
            }

            if (f.lastActivityFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastActivityAt"), f.lastActivityFrom()));
            }
            if (f.lastActivityTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastActivityAt"), f.lastActivityTo()));
            }

            if (f.lastEmailedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastEmailedAt"), f.lastEmailedFrom()));
            }
            if (f.lastEmailedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastEmailedAt"), f.lastEmailedTo()));
            }

            if (f.lastOpenedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastOpenedAt"), f.lastOpenedFrom()));
            }
            if (f.lastOpenedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastOpenedAt"), f.lastOpenedTo()));
            }

            if (f.lastClickedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastClickedAt"), f.lastClickedFrom()));
            }
            if (f.lastClickedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastClickedAt"), f.lastClickedTo()));
            }

            if (hasText(f.freeText())) {
                String pattern = "%" + f.freeText().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("secondaryEmail")), pattern),
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("companyName")), pattern),
                        cb.like(cb.lower(root.get("tags")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private static Predicate ilike(
            jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Expression<String> path,
            String value
    ) {
        String pattern = "%" + value.toLowerCase() + "%";
        return cb.like(cb.lower(path), pattern);
    }
}
