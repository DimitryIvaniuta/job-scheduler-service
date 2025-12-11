package com.github.dimitryivaniuta.scheduler.contact.service;

import com.github.dimitryivaniuta.scheduler.contact.domain.Contact;
import com.github.dimitryivaniuta.scheduler.contact.infrastructure.repository.ContactRepository;
import com.github.dimitryivaniuta.scheduler.contact.spec.ContactSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;

    // ---------- CRUD ----------

    public Contact create(Contact toCreate) {
        normalize(toCreate);
        if (toCreate.getBounceCount() == 0) {
            toCreate.setBounceCount(0);
        }
        return contactRepository.save(toCreate);
    }

    @Transactional(readOnly = true)
    public Contact get(UUID id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found: " + id));
    }

    public Contact update(UUID id, Contact changes) {
        Contact existing = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found: " + id));

        applyChanges(existing, changes);
        normalize(existing);

        // entity is managed; changes are flushed on transaction commit
        return existing;
    }

    public void delete(UUID id) {
        if (!contactRepository.existsById(id)) {
            return; // idempotent delete
        }
        contactRepository.deleteById(id);
    }

    // ---------- Bulk / filter-based operations ----------

    /**
     * Iterate over all contacts matching the given filter using Specifications.
     * Used by MailingService for sending mailings to a filtered audience.
     */
    @Transactional(readOnly = true)
    public void forEachMatching(ContactFilterRequest filter, Consumer<Contact> consumer) {
        contactRepository.findAll(ContactSpecifications.fromFilter(filter))
                .forEach(consumer);
    }

    // ---------- helpers ----------

    private void applyChanges(Contact target, Contact changes) {
        target.setEmail(changes.getEmail());
        target.setSecondaryEmail(changes.getSecondaryEmail());
        target.setFirstName(changes.getFirstName());
        target.setMiddleName(changes.getMiddleName());
        target.setLastName(changes.getLastName());

        target.setMobilePhone(changes.getMobilePhone());
        target.setWorkPhone(changes.getWorkPhone());
        target.setHomePhone(changes.getHomePhone());

        target.setCompanyName(changes.getCompanyName());
        target.setJobTitle(changes.getJobTitle());

        target.setAddressLine1(changes.getAddressLine1());
        target.setAddressLine2(changes.getAddressLine2());
        target.setCity(changes.getCity());
        target.setStateRegion(changes.getStateRegion());
        target.setPostalCode(changes.getPostalCode());
        target.setCountryCode(changes.getCountryCode());

        target.setTimeZone(changes.getTimeZone());
        target.setLocale(changes.getLocale());
        target.setPreferredChannel(changes.getPreferredChannel());
        target.setTags(changes.getTags());

        target.setBirthDate(changes.getBirthDate());
        target.setGender(changes.getGender());

        target.setActive(changes.isActive());
        target.setMarketingOptIn(changes.isMarketingOptIn());
        target.setUnsubscribed(changes.isUnsubscribed());

        // bounce count & engagement timestamps are usually system-driven;
        // only update if explicitly set (non-default) on changes
        if (changes.getBounceCount() != 0) {
            target.setBounceCount(changes.getBounceCount());
        }
        if (changes.getMarketingOptInAt() != null) {
            target.setMarketingOptInAt(changes.getMarketingOptInAt());
        }
        if (changes.getUnsubscribedAt() != null) {
            target.setUnsubscribedAt(changes.getUnsubscribedAt());
        }
        if (changes.getLastEmailedAt() != null) {
            target.setLastEmailedAt(changes.getLastEmailedAt());
        }
        if (changes.getLastOpenedAt() != null) {
            target.setLastOpenedAt(changes.getLastOpenedAt());
        }
        if (changes.getLastClickedAt() != null) {
            target.setLastClickedAt(changes.getLastClickedAt());
        }
        if (changes.getLastActivityAt() != null) {
            target.setLastActivityAt(changes.getLastActivityAt());
        }
    }

    private void normalize(Contact c) {
        c.setEmail(trimToNull(c.getEmail()));
        c.setSecondaryEmail(trimToNull(c.getSecondaryEmail()));
        c.setFirstName(trimToNull(c.getFirstName()));
        c.setMiddleName(trimToNull(c.getMiddleName()));
        c.setLastName(trimToNull(c.getLastName()));

        c.setMobilePhone(trimToNull(c.getMobilePhone()));
        c.setWorkPhone(trimToNull(c.getWorkPhone()));
        c.setHomePhone(trimToNull(c.getHomePhone()));

        c.setCompanyName(trimToNull(c.getCompanyName()));
        c.setJobTitle(trimToNull(c.getJobTitle()));

        c.setAddressLine1(trimToNull(c.getAddressLine1()));
        c.setAddressLine2(trimToNull(c.getAddressLine2()));
        c.setCity(trimToNull(c.getCity()));
        c.setStateRegion(trimToNull(c.getStateRegion()));
        c.setPostalCode(trimToNull(c.getPostalCode()));
        c.setCountryCode(trimToNull(c.getCountryCode()));

        c.setTimeZone(trimToNull(c.getTimeZone()));
        c.setLocale(trimToNull(c.getLocale()));
        c.setPreferredChannel(trimToNull(c.getPreferredChannel()));
        c.setTags(trimToNull(c.getTags()));
        c.setGender(trimToNull(c.getGender()));
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
