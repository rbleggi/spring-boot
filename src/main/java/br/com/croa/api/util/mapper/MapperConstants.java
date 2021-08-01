package br.com.croa.api.util.mapper;

import org.mapstruct.factory.Mappers;

public class MapperConstants {

    private MapperConstants() {
    }

    public static final ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);

}
