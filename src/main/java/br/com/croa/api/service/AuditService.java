package br.com.croa.api.service;

import br.com.croa.api.event.ClientEvent;
import br.com.croa.api.modules.croa.entity.AuditClient;
import br.com.croa.api.modules.croa.repository.AuditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuditService {

    private final AuditRepository repository;

    public void audit(ClientEvent clientEvent) {
        repository.save(AuditClient.builder()
                .idClient(clientEvent.getIdClient())
                .operationType(clientEvent.getOperationType())
                .build());
    }

}