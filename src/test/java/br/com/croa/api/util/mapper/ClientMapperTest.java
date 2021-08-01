package br.com.croa.api.util.mapper;

import br.com.croa.api.modules.croa.entity.Client;
import br.com.croa.api.resource.v1.dto.ClientCreateRequestDTO;
import br.com.croa.api.resource.v1.dto.ClientDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.wildfly.common.Assert.assertNotNull;

class ClientMapperTest {

    @Test
    void validate_not_null_ClientDTO() {
        Client client = Client
                .builder()
                .id(1L)
                .name("teste")
                .birthDate(LocalDate.of(2021, 7, 27))
                .build();

        ClientDTO clientDTO = MapperConstants.clientMapper.buildClientDTO(client);

        assertNotNull(clientDTO.getId());
        assertNotNull(clientDTO.getName());
        assertNotNull(clientDTO.getAge());
        assertNotNull(clientDTO.getBirthDate());
    }

    @Test
    void validate_not_null_Client() {
        ClientCreateRequestDTO clientCreateRequestDTO = ClientCreateRequestDTO
                .builder()
                .name("teste")
                .birthDate(LocalDate.of(2021, 7, 27))
                .build();

        Client client = MapperConstants.clientMapper.buildClient(clientCreateRequestDTO);

        assertNotNull(client.getName());
        assertNotNull(client.getBirthDate());
    }

    @Test
    void validate_null_ClientDTO() {
        ClientDTO clientDTO = MapperConstants.clientMapper.buildClientDTO(null);
        assertNull(clientDTO);
    }

    @Test
    void validate_null_Client() {
        Client client = MapperConstants.clientMapper.buildClient(null);
        assertNull(client);
    }
}
