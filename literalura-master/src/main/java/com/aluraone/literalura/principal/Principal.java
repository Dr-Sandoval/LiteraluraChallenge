package com.aluraone.literalura.principal;

import com.aluraone.literalura.model.*;
import com.aluraone.literalura.repository.AutorRepository;
import com.aluraone.literalura.repository.LibroRepository;
import com.aluraone.literalura.service.ConsumoAPI;
import com.aluraone.literalura.service.ConvierteDatos;

import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;
import java.util.List;


public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos conversor = new ConvierteDatos();
    private ConsumoAPI consulta = new ConsumoAPI();
    private int opcionUsuario = -1;
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    List<Autor> autores;
    List<Libro> libros;
    Scanner teclado = new Scanner(System.in);

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void consultaEjemplo(){

        do{
            mostrarMenu();
            opcionUsuario = Integer.valueOf(teclado.nextLine());
            switch(opcionUsuario){
                case 1:
                   buscarLibroWeb();
                    break;
                case 2:
                    mostrarLibrosBuscados();
                    break;
                case 3:
                    mostrarAutoresBuscados();
                    break;
                case 4:
                    mostrarAutoresPorAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Finalizando el programa, hasta luego");
                    break;
                default:
                    System.out.println("Disculpe, opcion no valida");
            }
        } while  (opcionUsuario != 0);


    }

    public void mostrarMenu(){
        System.out.println("""
                Seleccione una opción:             
                1- Buscar libro por titulo
                2- Hacer lista de libros registrados
                3- Hacer lista de autores registrados
                4- Hacer lista de autores vivos en un determinado año
                5- Hacer lista de libros por idioma
                    
                0- Salir - Hasta Luego.
                """);
    }

    public void muestraMenuIdionas(){
        System.out.println("""
                Ingrese el idioma en el que desee buscar los libros:
                en- ingles
                es- español
                fr- frances
                pt- portugués
                """);
    }

    private void buscarLibroWeb(){
        System.out.println("Ingrese el nombre del libro que desea buscar");
        String libroUsuario = teclado.nextLine();

        String busqueda = "?search=" + libroUsuario.replace(" ","+");
        var json = consulta.obtenerDatos(URL_BASE + busqueda);
//        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);

        DatosLibros datoslibro = datos.resultados().get(0);
        Libro libro = new Libro(datoslibro);
        Autor autor = new Autor().obtenerPrimerAutor(datoslibro);

        System.out.println(libro);
        guardarLibroConAutor(libro, autor);
    }

    private void guardarLibroConAutor(Libro libro, Autor autor){
        //buscar autores en vase de datos por nombre
        Optional<Autor> autorBuscado = autorRepository.findByNombreContains(autor.getNombre());

        //guarda el autor si no existe
        if(autorBuscado.isPresent()){
            System.out.println("Lo siento, el autor ingresado ya existe");
            libro.setAutor(autorBuscado.get());
        } else {
            System.out.println("Ingrese un nuevo autor");
            autorRepository.save(autor);
            libro.setAutor(autor);
        }

        //guarda el libro
        try {
            libroRepository.save(libro);
        } catch (Exception e) {
            // Manejar otras excepciones
            System.out.println("Lo siento, ocurrió un error al guardar el libro: " + e.getMessage());
        }
    }


    // listar autores buscados
    private void mostrarLibrosBuscados() {
        //Aqui se consulta nuestra base de datos postreSql
        libros = libroRepository.findAll();

        //imprimia las series guardadas en la lista series
        imprimeLibrosOrdenadosPorNombre(libros);
    }


    private void mostrarAutoresBuscados() {
        //Aqui se consulta nuestra base de datos postreSql
        autores = autorRepository.findAll();

        //imprimia las series guardadas en la lista autores
        imprimeAutoresOrdenadosPorNombre(autores);
    }

    //llistar autores vivos en un determinado año
    private void mostrarAutoresPorAnio(){
        System.out.println("Indica desde que año deseas ver a los actores");
        Integer anio = Integer.valueOf(teclado.nextLine());

        autores = autorRepository
                .findByFechaDeNacimientoLessThanEqualAndFechaDeMuerteGreaterThanEqual
                        (anio, anio);

        if (autores.isEmpty()) {
            System.out.println("Lo siento, no se encontraron autores vivos en ese año");
        } else {
            imprimeAutoresOrdenadosPorNombre(autores);
        }
    }

    private void imprimeAutoresOrdenadosPorNombre(List<Autor> autores){
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNombre))
                .forEach(System.out::println);
    }

    private void imprimeLibrosOrdenadosPorNombre(List<Libro> libros) {
        libros.stream()
                .sorted(Comparator.comparing(Libro::getNombreAutor))
                .forEach(System.out::println);
    }

    private void listarLibrosPorIdioma(){
        muestraMenuIdionas();
        String idioma = teclado.nextLine();

        String claveIdioma;
        if (idioma.length() >= 2) {
            claveIdioma = idioma.substring(0, 2);
        } else {
            // Manejar el caso donde el usuario no ingrese suficientes caracteres
            claveIdioma = idioma; // o manejar el error como prefieras
        }

        libros = libroRepository.findByIdiomasContains(claveIdioma);

        if (libros.isEmpty()) {
            System.out.println("Lo siento, no se encontraron libros en ese idioma");
        } else {
            imprimeLibrosOrdenadosPorNombre(libros);
        }

    }

}
