package br.com.croa.api.service;

import br.com.croa.api.helper.MessageHelper;
import br.com.croa.api.modules.croa.repository.ClientRepository;
import br.com.croa.api.modules.croa.repository.spec.ClientSpecification;
import br.com.croa.api.util.creator.ClientCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(SpringExtension.class)
class ClientServiceTest {

    @InjectMocks
    private ClientService service;
    @Mock
    private ClientRepository repository;
    @Mock
    private MessageHelper messageHelper;

    @Test
    void findById_ReturnClient_WhenSuccessful() {
        when(repository.findById(ClientCreator.client.getId())).thenReturn(Optional.of(ClientCreator.client));
        Assertions.assertEquals(ClientCreator.validClientDTO, service.findById(ClientCreator.client.getId()));
    }

    @Test
    void findById_ReturnError_WhenEmptyIsReturned() {
        when(repository.findById(ClientCreator.client.getId())).thenReturn(Optional.empty());
        assertEquals(NOT_FOUND, assertThrows(ResponseStatusException.class, () -> service.findById(ClientCreator.client.getId())).getStatus());
    }

    @Test
    void findAll_ReturnClients_WhenSuccessful() {
        when(repository.findAll(any(ClientSpecification.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(ClientCreator.client)));
        final var all = service.findAll(Optional.of(ClientCreator.client.getName()), Optional.of(ClientCreator.client.getBirthDate()), PageRequest.of(0, 5));
        assertEquals(1L, all.getTotalElements());
        Assertions.assertEquals(ClientCreator.validClientDTO, all.getContent().get(0));
    }

    @Test
    void save_CreateClient_WhenSuccessful() {
        final var clienteId = ClientCreator.client.getId();
        when(repository.save(any())).thenReturn(ClientCreator.client.withId(clienteId));
        Assertions.assertEquals(ClientCreator.validClientDTO, service.create(ClientCreator.createClientCreateRequestDTOToBeSaved));
    }

    @Test
    void update_UpdateClient_WhenSuccessful() {
        when(repository.findById(ClientCreator.validClientDTO.getId())).thenReturn(Optional.of(ClientCreator.client));
        when(repository.save(ClientCreator.createClientToBeUpdated())).thenReturn(ClientCreator.client);
        Assertions.assertEquals(ClientCreator.validClientDTO, service.update(ClientCreator.validClientDTO.getId(), ClientCreator.createClientCreateRequestDTOToBeSaved));
    }

    @Test
    void update_ReturnError_WhenEmptyIsReturned() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertEquals(NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> service.update(ClientCreator.validClientDTO.getId(), ClientCreator.createClientCreateRequestDTOToBeSaved)).getStatus());
    }

    @Test
    void delete_RemovesClient_WhenSuccessful() {
        when(repository.findById(ClientCreator.client.getId())).thenReturn(Optional.of(ClientCreator.client));
        doNothing().when(repository).delete(ClientCreator.client);
        service.delete(ClientCreator.client.getId());
    }

    @Test
    void delete_ReturnError_WhenClientNotExist() {
        when(repository.findById(ClientCreator.client.getId())).thenReturn(Optional.empty());
        assertEquals(NOT_FOUND, assertThrows(ResponseStatusException.class, () -> service.delete(ClientCreator.client.getId())).getStatus());
    }

}