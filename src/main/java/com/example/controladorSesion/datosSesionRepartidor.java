package com.example.controladorSesion;

public class datosSesionRepartidor {

    public static int idRepartidor = 0;
    public static String nombreRepartidor;

    public static Integer coordenadaX = null;
    public static Integer coordenadaY = null;
//Metodos de asignacion
    public static void setRepartidor(int id, String nombre){
        idRepartidor = id;
        nombreRepartidor = nombre;
    }

    public static void setUbicacion(Integer x, Integer y){
        coordenadaX = x;
        coordenadaY = y;
    }

//Metodos de retorno
    public static int getIdRepartidor(){
        return idRepartidor;
    }

    public static String getNombreRepartidor(){
        return nombreRepartidor;
    }

    public static Integer getCoordenadaX(){
        return coordenadaX;
    }

    public static Integer getCoordenadaY(){
        return coordenadaY;
    }
    
}
