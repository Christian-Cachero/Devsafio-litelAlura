package com.aluradevsafios.LiterAlura.repository;

import com.aluradevsafios.LiterAlura.models.Libros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibrosRepository extends JpaRepository<Libros, Long> {

    List<Libros> findByIdioma(String idioma);

}
