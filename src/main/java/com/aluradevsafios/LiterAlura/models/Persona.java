package com.aluradevsafios.LiterAlura.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import jakarta.persistence.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonAlias("birth_year")
    private Integer fechaNacimiento;

    @JsonAlias("death_year")
    private Integer fechaMuerte;

    @JsonAlias("name")
    private String nombre;

    @ManyToMany(mappedBy = "autor")
    private List<Libros> libros;

    @Override
    public String toString() {
        return " nombre: " + nombre +
                ", fechaNacimiento: " + fechaNacimiento +
                ", fechaMuerte: " + fechaMuerte;
    }
}
