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

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class Principal {

    private EncodearBusquedas encoder = new EncodearBusquedas();

    private Scanner s = new Scanner(System.in);

    private ConsumoAPImpl consumoApi = new ConsumoAPImpl();

    private ConvierteDatosImpl conversor = new ConvierteDatosImpl();

    private final String URL_BASE = "https://gutendex.com/books/?search=";

    private List<Libros> libros;

    private List<Persona> autores;

    @Autowired
    private LibrosRepository libroRepository;

    @Autowired
    private PersonaRepository personaRepository;

    public Principal(LibrosRepository libroRepository, PersonaRepository personaRepository) {
        this.libroRepository = libroRepository;
        this.personaRepository = personaRepository;
    }
    public void mostrarMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu =
                    """                      
                         1) Buscar libros.
                         2) Ver todos los libros.
                         3) Buscar libros por idioma.
                         4) Listar autores de los libros buscados.
                         5) Listar autores vivos (rango de años).
                         6) Listar cantidad de libros en español o inglés.
                         7) Mostrar estadísticas de descargas.
                         8) Listar top 10 libros mas descargados.
                         9) Buscar Autor por nombre.
                         0) Salir
                         """;
            System.out.println(menu);
            opcion = s.nextInt();
            s.nextLine();

            switch (opcion) {
                case 1 -> buscarLibro();
                case 2 -> mostrarLibros();
                case 3 -> mostrarLibrosPorIdioma();
                case 4 -> mostrarAutores();
                case 5 -> mostrarAutoresVivos();
                case 6 -> cantidadLibrosPorIdioma();
                case 7 -> estadisticasGeneralesDescargas();
                case 8 -> top10LibrosMasDescargados();
                case 9 -> buscarAutorPorNombre();
                case 0 -> System.out.println("Cerrando la aplicación...");
                default -> System.out.println("Opción invalidad vuelva a intentar \n");
            }
        }
    }

    public DatosLibros getDatosLibro() {
        try {
            System.out.println("Por favor indique el titulo del libro que desea buscar: ");
            var tituloLibro = s.nextLine();
            var json = consumoApi.obtenerDatos(URL_BASE + encoder.encodearBusquedas(tituloLibro));
            return conversor.obtenerDatos(json, DatosLibros.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void buscarLibro() {
        DatosLibros datosLibros = getDatosLibro();
        if (datosLibros != null) {
            List<Libros> datos = datosLibros.resultado();
            if (!datos.isEmpty()) {
                Libros libro = datos.get(0);

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

                    libroRepository.save(libro); // Guarda el libro
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


    public void mostrarLibros() {
        libros = libroRepository.findAll();
        if (!libros.isEmpty()) {
            libros.forEach(System.out::println);
            System.out.println();
        } else {
            System.out.println("No hay libros almacenados que encontrar");
        }
    }

    public void mostrarLibrosPorIdioma() {
        System.out.println("Ingresa el prefijo del idioma del libro que querés buscar: ");
        var libroIdioma = s.nextLine();
        libros = libroRepository.findByIdioma(libroIdioma);
        if (!libros.isEmpty()) {
            libros.forEach(System.out::println);
        } else {
            System.out.println("Ningún libro tiene tal prefijo.");
        }
    }

    public void mostrarAutores() {
        autores = personaRepository.findAll();

        if (!autores.isEmpty()) {
            autores.forEach(System.out::println);
            System.out.println();
        } else {
            System.out.println("No hay autores almacenados que encontrar");
        }
    }

    public void mostrarAutoresVivos() {

        System.out.println("por favor indica año inicial del rango: ");
        var periodoNacido = s.nextInt();

        System.out.println("por favor indica año final del rango: ");
        var periodoMuerto = s.nextInt();

        var autoresVivosOptional = personaRepository.// Busca autores vivos entre las fechas dadas.
                findByFechaMuerteGreaterThanEqualAndFechaNacimientoLessThanEqual(periodoMuerto, periodoNacido);

        autoresVivosOptional.ifPresentOrElse(
                autoresVivos -> {
                    if (autoresVivos.isEmpty()) {
                        System.out.println("No hay autores vivos en la fecha especificada.");
                    } else {
                        autoresVivos.forEach(System.out::println);
                    }
                },
                () -> System.out.println("No hay ningún autor nacido ese periodo")
        );
    }

    public void cantidadLibrosPorIdioma() {
        System.out.println("""
                especifica el prefijo del idioma a contar la cantidad eligiendo una opción:
                 1) escoge 'es' para español.\s
                 2) escoge 'en' para ingles.\s""");
        var opcionEscojida = s.nextInt();


        if (opcionEscojida != 1 && opcionEscojida != 2) {
            System.out.println("Opción escogida incorrecta, vuelva a intentarlo \n");
            cantidadLibrosPorIdioma();
        } else {
            System.out.println(" ");
            String idiomaBuscado = opcionEscojida == 1 ? "es" : "en";

            long cantidadLibrosIdiomas = libroRepository.findAll().stream()
                    .filter(lI -> lI.getIdioma().stream()
                            .map(String::trim)
                            .map(String::toLowerCase)
                            .anyMatch(idioma -> idioma.equals(idiomaBuscado))
                    ).count();

            System.out.println("la cantidad de libros actuales en idioma " +
                    (opcionEscojida == 1 ? "español" : "ingles") + " son: " + cantidadLibrosIdiomas + "\n" );
        }
    }

    public void estadisticasGeneralesDescargas() {

        libros = libroRepository.findAll();

        IntSummaryStatistics est = libros.stream()
                .filter(l -> l.getNumeroDescargas() > 0)
                .collect(Collectors.summarizingInt(Libros::getNumeroDescargas));

        System.out.println("Media de las descargas en la biblioteca: " + Math.round(est.getAverage()));
        System.out.println("Pico de descargas de un libro en la biblioteca: " + est.getMax());
        System.out.println("Valle de descargas de un libro en la biblioteca: " + est.getMin());
        System.out.println();
    }

    public void top10LibrosMasDescargados(){
        libros = libroRepository.findTop10ByOrderByNumeroDescargasDesc();

        libros.forEach(l -> System.out.println("Libro: '" + l.getTitulo() + "'" +
                ", \nDescargas: '" + l.getNumeroDescargas() + "'\n"));
    }

    public void buscarAutorPorNombre(){
        System.out.println("Ingresa el nombre del autor que pretendes buscar: ");
        var nombre = s.nextLine();
        autores = personaRepository.findAll();

        Optional<Persona> autor = autores.stream()
                .filter(a -> quitarAcentos(a.getNombre()).toLowerCase().contains(quitarAcentos(nombre).toLowerCase()))
                .findFirst();

        if (autor.isPresent()) {
            System.out.println(autor.get());
            System.out.println();
        }
        else {
            System.out.println("No se encontró tal autor en la base de datos, asegúrese de haber cargado un libro con" +
                    " este autor.");
        }
    }

    public String quitarAcentos(String texto) {
        String normalized = Normalizer.normalize(texto, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}