package com.aluradevsafios.LiterAlura.principal;

import com.aluradevsafios.LiterAlura.models.DatosLibros;
import com.aluradevsafios.LiterAlura.models.Libros;
import com.aluradevsafios.LiterAlura.repository.LibrosRepository;
import com.aluradevsafios.LiterAlura.serviceImpl.ConsumoAPImpl;
import com.aluradevsafios.LiterAlura.serviceImpl.ConvierteDatosImpl;
import com.aluradevsafios.LiterAlura.utilities.EncodearBusquedas;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

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
    private LibrosRepository repository;

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
            System.out.println(json);
            DatosLibros datos = conversor.obtenerDatos(json, DatosLibros.class);
            System.out.println(datos); //necesario recoger los datos de los contenedores y asignarlos a un nuevo objeto libro.
            return datos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void buscarLibro() {
        Optional<DatosLibros> optionalDatos = Optional.ofNullable(getDatosLibro());

        if (optionalDatos.isPresent()) {
            DatosLibros datosLibros = optionalDatos.get();
            List<Libros> datos = datosLibros.resultado();
            repository.saveAll(datos);
            System.out.println(datos);
        } else {
            System.out.println("Error al obtener los datos del libro o no se encontraron resultados.");
        }
    }

}
