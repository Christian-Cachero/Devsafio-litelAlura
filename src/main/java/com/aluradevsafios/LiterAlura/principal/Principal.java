package com.aluradevsafios.LiterAlura.principal;

import com.aluradevsafios.LiterAlura.models.DatosLibros;
import com.aluradevsafios.LiterAlura.models.Libros;
import com.aluradevsafios.LiterAlura.models.Persona;
import com.aluradevsafios.LiterAlura.repository.LibrosRepository;
import com.aluradevsafios.LiterAlura.repository.PersonaRepository;
import com.aluradevsafios.LiterAlura.serviceImpl.ConsumoAPImpl;
import com.aluradevsafios.LiterAlura.serviceImpl.ConvierteDatosImpl;
import com.aluradevsafios.LiterAlura.utilities.EncodearBusquedas;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

//@AllArgsConstructor
@Service
public class Principal {

    private EncodearBusquedas encoder = new EncodearBusquedas();

    private Scanner s = new Scanner(System.in);

    private ConsumoAPImpl consumoApi = new ConsumoAPImpl();

    private ConvierteDatosImpl conversor = new ConvierteDatosImpl();

    private String URL_BASE = "https://gutendex.com/books/?search=";

    private List<Libros> libros;
    //private final String TEXTO_BUSCAR = "?search=";


    public Principal(LibrosRepository libroRepository, PersonaRepository personaRepository) {
        this.libroRepository = libroRepository;
        this.personaRepository = personaRepository;
    }

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
                case 2:
                    mostrarLibros();
                    break;
                case 3:
                    mostrarLibrosPorIdioma();
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
            //System.out.println("datos después de la conversion " + datos);
            return conversor.obtenerDatos(json, DatosLibros.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
                    System.out.println(libro);
                    Optional<Persona> personaExiste = personaRepository.findByNombre(autor.getNombre());

                    if (personaExiste.isPresent()) {
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

    /*@Transactional*/
    public void mostrarLibros(){
        libros = libroRepository.findAll();
        libros.forEach(System.out::println);
    }

    public void mostrarLibrosPorIdioma(){
        System.out.println("Ingresa el prefijo del idioma del libro que querés buscar: ");
        var libroIdioma = s.nextLine();
        libros = libroRepository.findByIdioma(libroIdioma);
        if (!libros.isEmpty()){
            libros.forEach(System.out::println);
        } else {
            System.out.println("Ningún libro tiene tal prefijo.");
        }
    }
}
