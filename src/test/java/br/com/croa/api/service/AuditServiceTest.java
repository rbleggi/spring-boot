package br.com.croa.api.service;

import br.com.croa.api.event.ClientEvent;
import br.com.croa.api.modules.croa.entity.AuditClient;
import br.com.croa.api.modules.croa.repository.AuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuditServiceTest {

    @InjectMocks
    private AuditService service;
    @Mock
    private AuditRepository repository;

    @Test
    void save_AuditClient_WhenSuccessful() {
        when(repository.save(any())).thenReturn(AuditClient.builder().build());
        service.audit(ClientEvent.builder().build());
        verify(repository, times(1)).save(any());
    }

}