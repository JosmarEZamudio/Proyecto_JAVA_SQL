package com.example.controladorSesion;

public class datosSesionCliente{
    private static Integer idCliente = null;
    private static String nombre = null;
    private static Double coordenadax = null;
    private static Double coordenaday = null;
    private static Integer idUbicacion = null;
    private static String ubicacion;
//Metodos de asignacion
    public static void iniciar(Integer id, String nombreCliente){
        idCliente = id;
        nombre = nombreCliente;
    }

    public static void ubicacion(Double coordenadaX, Double coordenadaY, String nombre){
        coordenadax = coordenadaX;
        coordenaday = coordenadaY;
        ubicacion = nombre;
    }

    public static void idUbicacion(Integer id){
        idUbicacion = id;
    }

//Metodos de retorno
    public static Integer getIdCliente(){
        return idCliente;
    }

    public static Integer getIdubicacion(){
        return idUbicacion;
    }


    public static String getNombreCliente(){
        return nombre;
    }

    public static Double getCoordenadaX(){
        return coordenadax;
    }

    public static Double getCoordenadaY(){
        return coordenaday;
    }

    public static String getNombreUbicacion(){
        return ubicacion;
    }

}
