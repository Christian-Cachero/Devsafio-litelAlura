package com.aluradevsafios.LiterAlura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "libros") // Nombre de la tabla en la base de datos
public class Libros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generación automática del ID
    private Long id;

    @JsonAlias("title")
    private String titulo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_authors", joinColumns = @JoinColumn(name = "libros_id"),
            inverseJoinColumns = @JoinColumn(name = "personas_id")
    ) // Relación con la entidad Persona
    @JsonAlias("authors")
    private List<Persona> autor;

    @ElementCollection(fetch = FetchType.EAGER) // Para almacenar una lista de elementos simples
    @JsonAlias("languages")
    private List<String> idioma;

    @JsonAlias("download_count")
    private Integer numeroDescargas;

    @Override
    public String toString() {
        return "titulo: '" + titulo + '\'' +
                ", autor: " + autor +
                ", idioma: " + idioma +
                ", numeroDescargas: " + numeroDescargas;
    }

    /*public Libros(DatosLibros datosLibros) {
        this.titulo = datosLibros.titulo();
        this.autor = datosLibros.autor();
        this.idioma = datosLibros.idioma();
        this.numeroDescargas = datosLibros.numeroDescargas();
    }*/
}
