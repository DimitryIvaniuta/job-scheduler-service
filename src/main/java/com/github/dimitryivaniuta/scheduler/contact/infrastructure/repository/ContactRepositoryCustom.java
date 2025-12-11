package com.github.dimitryivaniuta.scheduler.contact.infrastructure.repository;

import com.github.dimitryivaniuta.scheduler.contact.domain.Contact;
import com.github.dimitryivaniuta.scheduler.contact.web.dto.contact.ContactFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactRepositoryCustom {

    Page<Contact> searchByFilterQuerydsl(ContactFilterRequest filter, Pageable pageable);
}
