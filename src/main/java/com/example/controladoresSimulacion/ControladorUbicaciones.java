package com.example.controladoresSimulacion;

public class ControladorUbicaciones {
//Id del pedido
    public static int idPedido;
//Coordenadas de la direccion del negocio
    public static double coordenadaXNegocio;
    public static double coordenadaYNegocio;
//Coordenadas de la ubicacion del repartidor
    public static double coordenadaXRepartidor;
    public static double coordenadaYRepartidor;
//Coordenadas de la ubicacion del cliente
    public static double coordenadaXCliente;
    public static double coordenadaYCliente;

//Metodos para setear ubicaciones 
    public static void setIdPedido(int id){
        idPedido = id;
    }

    public static void setUbicacionNegocio(int x, int y){
        coordenadaXNegocio = (double) x;
        coordenadaYNegocio = (double) y;
    }
    
    public static void setUbicacionCliente(int x, int y){
        coordenadaXCliente = (double) x;
        coordenadaYCliente = (double) y;
    }

    public static void setUbicacionRepartidor(int x, int y){
        coordenadaXRepartidor = (double) x;
        coordenadaYRepartidor = (double) y;
    }

//Metodos de retorno de variables
    public static int getIdPedido(){
        return idPedido;
    }

    public static double getCoordenadaXNegocio(){
        return coordenadaXNegocio;
    }

    public static double getCoordenadaYNegocio(){
        return coordenadaYNegocio;
    }

    public static double getCoordenadaXCliente(){
        return coordenadaXCliente;
    }

    public static double getCoordenadaYCliente(){
        return coordenadaYCliente;
    }

    public static double getCoordenadaXRepartidor(){
        return coordenadaXRepartidor;
    }

    public static double getCoordenadaYRepartidor(){
        return coordenadaYRepartidor;
    }

}
