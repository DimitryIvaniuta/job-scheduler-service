package com.github.dimitryivaniuta.scheduler.contact.service;

import com.github.dimitryivaniuta.scheduler.contact.domain.Contact;
import com.github.dimitryivaniuta.scheduler.contact.infrastructure.repository.ContactRepository;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactFilterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactSearchServiceQuerydsl {

    private final ContactRepository repository;

    public Page<Contact> search(ContactFilterRequest filter, Pageable pageable) {
        return repository.searchByFilterQuerydsl(filter, pageable);
    }
}
