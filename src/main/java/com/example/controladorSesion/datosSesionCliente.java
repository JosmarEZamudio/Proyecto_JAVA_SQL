package com.example.controladorSesion;

public class datosSesionCliente{
    private static int idCliente;
    private static String nombre;
    private static Integer coordenadax = null;
    private static Integer coordenaday = null;

    public static void iniciar(int id, String nombreCliente){
        idCliente = id;
        nombre = nombreCliente;
    }

    public static void ubicacion(int coordenadaX, int coordenadaY){
        coordenadax = coordenadaX;
        coordenaday = coordenadaY;
    }

    public static int getIdCliente(){
        return idCliente;
    }

    public static String getNombreCliente(){
        return nombre;
    }

    public static Integer getCoordenadaX(){
        return coordenadax;
    }

    public static Integer getCoordenadaY(){
        return coordenaday;
    }

}
