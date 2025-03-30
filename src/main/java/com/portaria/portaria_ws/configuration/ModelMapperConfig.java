package com.portaria.portaria_ws.configuration;

import com.portaria.portaria_ws.dto.request.AccessRequest;
import com.portaria.portaria_ws.dto.response.VisitaResponse;
import com.portaria.portaria_ws.entity.VisitaEntity;
import com.portaria.portaria_ws.entity.VisitanteEntity;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        // Não mapear campos que não tenham correspondência exata
        modelMapper.getConfiguration().setFieldMatchingEnabled(false);

        // Não mapear valores nulos
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        modelMapper.typeMap(AccessRequest.class, VisitanteEntity.class).addMappings(mapper -> {
            mapper.map(src -> src.getCpf(), VisitanteEntity::setCpf);
            mapper.map(src -> src.getNome(), VisitanteEntity::setNomeCompleto);
        });

        modelMapper.addMappings(new PropertyMap<VisitaEntity, VisitaResponse>() {
            @Override
            protected void configure() {
                skip(destination.getAcessos()); // Ignora o campo
            }
        });

        return modelMapper;
    }

}
