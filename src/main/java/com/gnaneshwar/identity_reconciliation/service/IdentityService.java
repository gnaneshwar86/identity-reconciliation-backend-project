package com.gnaneshwar.identity_reconciliation.service;

import com.gnaneshwar.identity_reconciliation.dto.*;
import com.gnaneshwar.identity_reconciliation.entity.*;
import com.gnaneshwar.identity_reconciliation.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final ContactRepository repository;

    @Transactional
    public IdentifyResponse identify(IdentifyRequest request) {

        if (request.getEmail() == null && request.getPhoneNumber() == null)
            throw new RuntimeException("Email or Phone required");

        List<Contact> matched =
                repository.findByEmailOrPhoneNumber(
                        request.getEmail(),
                        request.getPhoneNumber()
                );

        if (matched.isEmpty()) {
            Contact newContact = repository.save(
                    Contact.builder()
                            .email(request.getEmail())
                            .phoneNumber(request.getPhoneNumber())
                            .linkPrecedence(LinkPrecedence.PRIMARY)
                            .build()
            );

            return buildResponse(newContact, List.of(newContact));
        }

        Set<Contact> cluster = new HashSet<>(matched);

        matched.forEach(contact -> {
            if (contact.getLinkedId() != null)
                cluster.addAll(repository.findByLinkedId(contact.getLinkedId()));
            cluster.addAll(repository.findByLinkedId(contact.getId()));
        });

        Contact primary = cluster.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.PRIMARY)
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElseThrow();

        cluster.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.PRIMARY)
                .filter(c -> !c.getId().equals(primary.getId()))
                .forEach(c -> {
                    c.setLinkPrecedence(LinkPrecedence.SECONDARY);
                    c.setLinkedId(primary.getId());
                    repository.save(c);
                });

        boolean emailExists = cluster.stream()
                .anyMatch(c -> Objects.equals(c.getEmail(), request.getEmail()));

        boolean phoneExists = cluster.stream()
                .anyMatch(c -> Objects.equals(c.getPhoneNumber(), request.getPhoneNumber()));

        if (!emailExists || !phoneExists) {
            repository.save(
                    Contact.builder()
                            .email(request.getEmail())
                            .phoneNumber(request.getPhoneNumber())
                            .linkedId(primary.getId())
                            .linkPrecedence(LinkPrecedence.SECONDARY)
                            .build()
            );
        }

        List<Contact> finalCluster = repository.findAll().stream()
                .filter(c ->
                        c.getId().equals(primary.getId()) ||
                                Objects.equals(c.getLinkedId(), primary.getId()))
                .toList();

        return buildResponse(primary, finalCluster);
    }

    private IdentifyResponse buildResponse(Contact primary, List<Contact> cluster) {

        List<String> emails = cluster.stream()
                .map(Contact::getEmail)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<String> phones = cluster.stream()
                .map(Contact::getPhoneNumber)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<Long> secondaryIds = cluster.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.SECONDARY)
                .map(Contact::getId)
                .toList();

        return IdentifyResponse.builder()
                .contact(
                        IdentifyResponse.ContactData.builder()
                                .primaryContactId(primary.getId())
                                .emails(emails)
                                .phoneNumbers(phones)
                                .secondaryContactIds(secondaryIds)
                                .build()
                )
                .build();
    }
}