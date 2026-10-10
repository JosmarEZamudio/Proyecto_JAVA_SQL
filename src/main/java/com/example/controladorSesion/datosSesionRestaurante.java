package com.example.controladorSesion;

public class datosSesionRestaurante {
//Variables estaticas para todas nuestras clases
    private static int idRestaurante;
    private static int idProductoRegistroActual;
    private static String nombreRestaurante; 
//Funcion donde se le da un valor a un atributo
    public static void iniciar(int id, String nombre){
        idRestaurante = id;
        nombreRestaurante = nombre;
    }
//Funcion donde se le da valor a un atributo
    public static void productoActual(int id){
        idProductoRegistroActual = id;
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
}
