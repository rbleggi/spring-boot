package br.com.croa.api.util.mapper;

import br.com.croa.api.modules.croa.entity.Client;
import br.com.croa.api.resource.v1.dto.ClientCreateRequestDTO;
import br.com.croa.api.resource.v1.dto.ClientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ClientMapper {

    @Mapping(target = "age", expression = "java(java.time.Period.between(client.getBirthDate(), java.time.LocalDate.now()).getYears())")
    ClientDTO buildClientDTO(Client client);

    @Mapping(target = "id", ignore = true)
    Client buildClient(ClientCreateRequestDTO clientCreateRequestDTO);

}
