package com.aluradevsafios.LiterAlura.serviceImpl;

import com.aluradevsafios.LiterAlura.service.ConvierteDatos;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ConvierteDatosImpl implements ConvierteDatos {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /* Básicamente, le pasas el JSON y el "filtro" para que
     * te devuelva solo lo que necesitas.*/

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json, clase);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}
