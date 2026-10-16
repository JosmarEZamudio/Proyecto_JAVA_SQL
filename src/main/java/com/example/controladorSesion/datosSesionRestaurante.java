package com.example.controladorSesion;

public class datosSesionRestaurante {
//Variables estaticas para todas nuestras clases
    private static int idRestaurante;
    private static int idProductoRegistroActual;
    private static String nombreRestaurante; 
//Variables para guardar la ubicacion
    private static int coordenadaX;
    private static int coordenadaY;
//Funcion donde se le da un valor a un atributo
    public static void iniciar(int id, String nombre){
        idRestaurante = id;
        nombreRestaurante = nombre;
    }
//Funcion donde se le da valor a un atributo
    public static void productoActual(int id){
        idProductoRegistroActual = id;
    }

    public static void guardarUbicacion(int x, int y){
        coordenadaX = x;
        coordenadaY = y;
    }
    
//Funcion que mantiene el id de restaurante en todo el tiempo que dura la sesion
    public static int getIdRestaurante(){
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

    public static int getCoordenadaX(){
        return coordenadaX;
    }

    public static int getCoordenadaY(){
        return coordenadaY;
    }
}
