package com.aluradevsafios.LiterAlura.principal;

import com.aluradevsafios.LiterAlura.models.DatosLibros;
import com.aluradevsafios.LiterAlura.models.Libros;
import com.aluradevsafios.LiterAlura.models.Persona;
import com.aluradevsafios.LiterAlura.repository.LibrosRepository;
import com.aluradevsafios.LiterAlura.repository.PersonaRepository;
import com.aluradevsafios.LiterAlura.serviceImpl.ConsumoAPImpl;
import com.aluradevsafios.LiterAlura.serviceImpl.ConvierteDatosImpl;
import com.aluradevsafios.LiterAlura.utilities.EncodearBusquedas;

import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@AllArgsConstructor
@Service
public class Principal {

    private final EncodearBusquedas encoder = new EncodearBusquedas();

    private final Scanner s = new Scanner(System.in);

    private final ConsumoAPImpl consumoApi = new ConsumoAPImpl();

    private final ConvierteDatosImpl conversor = new ConvierteDatosImpl();

    private final String URL_BASE = "https://gutendex.com/books/?search=";

    //private final String TEXTO_BUSCAR = "?search=";

    @Autowired
    private LibrosRepository libroRepository;

    @Autowired
    private PersonaRepository personaRepository;

    public void mostrarMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu =
                    """
                     1) Buscar libros
                     2) Ver todos los libros
                     3) Buscar libros por idioma
                     0) Salir
                     """;
            System.out.println(menu);
            opcion = s.nextInt();
            s.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;

            }
        }
    }

    //
    public DatosLibros getDatosLibro() {
        try {
            System.out.println("Por favor indique el titulo del libro que desea buscar: ");
            var tituloLibro = s.nextLine();
            var json = consumoApi.obtenerDatos(URL_BASE + encoder.encodearBusquedas(tituloLibro));
            //System.out.println(json);
            DatosLibros datos = conversor.obtenerDatos(json, DatosLibros.class);
            //System.out.println("datos después de la conversion " + datos); //necesario recoger los datos de los contenedores y asignarlos a un nuevo objeto libro.
            return datos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //@Transactional
    public void buscarLibro() {
        Optional<DatosLibros> optionalDatos = Optional.ofNullable(getDatosLibro());
        if (optionalDatos.isPresent()) {
            DatosLibros datosLibros = optionalDatos.get();
            List<Libros> datos = datosLibros.resultado();

            // Verifica si hay al menos un libro en la lista
            if (!datos.isEmpty()) {
                Libros libro = datos.get(0); // Toma el primer libro
                //List<Persona> autoresGuardados = new ArrayList<>();

                // Procesa los autores del libro

                if (!libro.getAutor().isEmpty()) {
                    Persona autor = libro.getAutor().get(0);
                    Optional<Persona> personaExiste = personaRepository.findByNombre(autor.getNombre());

                    System.out.println(libro);
                    if (personaExiste.isPresent()) {
                        Hibernate.initialize(personaExiste.get().getLibros());
                        libro.setAutor(Collections.singletonList(personaExiste.get()));
                    } else {
                        Persona nuevoAutor = personaRepository.save(autor);
                        libro.setAutor(Collections.singletonList(nuevoAutor));
                    }


                    //libro.setAutor(autoresGuardados); // Establece la lista de autores guardados en el libro
                    libroRepository.save(libro); // Guarda solo el primer libro
                } else {
                    System.out.println("No se encontraron autores para el libro.");
                }
            } else {
                System.out.println("No se encontraron libros en el resultado.");
            }
        } else {
            System.out.println("Error al obtener los datos del libro o no se encontraron resultados.");
        }
    }

}
