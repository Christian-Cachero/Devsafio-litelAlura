package com.aluradevsafios.LiterAlura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public record DatosLibros(

        @JsonAlias("results")
        List<Libros> resultado/*,

        @JsonAlias("title")
        String titulo,

        @JsonAlias("authors")
        List<Persona> autor,

        @JsonAlias("languages")
        List<String> idioma,

        @JsonAlias("download_count")
        Integer numeroDescargas*/) {
}
