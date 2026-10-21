package com.example.controladorSesion;

public class datosSesionRestaurante {
//Variables estaticas para todas nuestras clases
    private static Integer idRestaurante = null;
    private static int idProductoRegistroActual;
    private static String nombreRestaurante; 
//Variables para guardar la ubicacion y nombre
    private static String direccion = null;
    private static Double coordenadaX = null;
    private static Double coordenadaY = null;
    private static Integer idUbicacion = null;
//Funcion donde se le da un valor a un atributo
    public static void iniciar(Integer id, String nombre){
        idRestaurante = id;
        nombreRestaurante = nombre;
    }
//Funcion donde se le da valor a un atributo
    public static void productoActual(int id){
        idProductoRegistroActual = id;
    }

    public static void guardarUbicacion(Double x, Double y, String nombre){
        direccion = nombre;
        coordenadaX = x;
        coordenadaY = y;
    }

    public static void idUbicacion(Integer id){
        idUbicacion = id;
    }
    
//Funcion que mantiene el id de restaurante en todo el tiempo que dura la sesion
    public static Integer getIdRestaurante(){
        return idRestaurante;
    }
//Funcion que regresa el id de un producto recientemente registrado (el ultimo)
    public static int getIdProductoRegistroActual(){
        return idProductoRegistroActual;
    }
//Funcion que regresa el nombre del restuarante 
    public static String getNombreRestaurante(){
        return nombreRestaurante;
    }

    public static String getDireccion(){
        return direccion;
    }

    public static Double getCoordenadaX(){
        return coordenadaX;
    }

    public static Double getCoordenadaY(){
        return coordenadaY;
    }

    public static Integer getIdUbicacion(){
        return idUbicacion;
    }
}
