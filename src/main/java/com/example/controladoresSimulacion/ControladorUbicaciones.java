package com.example.controladoresSimulacion;

public class ControladorUbicaciones {
//Id del pedido
    public static Integer idPedido;
//Coordenadas de la direccion del negocio
    public static Double coordenadaXNegocio;
    public static Double coordenadaYNegocio;
    public static String direccionNegocio;
//Coordenadas de la ubicacion del repartidor
    public static Double coordenadaXRepartidor;
    public static Double coordenadaYRepartidor;
//Coordenadas de la ubicacion del cliente
    public static Double coordenadaXCliente;
    public static Double coordenadaYCliente;
    public static String direccionCliente;

//Metodos para setear ubicaciones 
    public static void setIdPedido(Integer id){
        idPedido = id;
    }

    public static void setUbicacionNegocio(Double x, Double y,String nombre){
        coordenadaXNegocio =  x;
        coordenadaYNegocio =  y;
        direccionNegocio = nombre;
    }
    
    public static void setUbicacionCliente(Double x, Double y, String nombre){
        coordenadaXCliente =  x;
        coordenadaYCliente =  y;
        direccionCliente = nombre;
    }

    public static void setUbicacionRepartidor(Double x, Double y){
        coordenadaXRepartidor =  x;
        coordenadaYRepartidor =  y;
    }

//Metodos de retorno de variables
    public static Integer getIdPedido(){
        return idPedido;
    }
//*******Negocio */
    public static Double getCoordenadaXNegocio(){
        return coordenadaXNegocio;
    }

    public static Double getCoordenadaYNegocio(){
        return coordenadaYNegocio;
    }

    public static String getDireccionNegocio(){
        return direccionNegocio;
    }
//*******Cliente */
    public static Double getCoordenadaXCliente(){
        return coordenadaXCliente;
    }

    public static Double getCoordenadaYCliente(){
        return coordenadaYCliente;
    }
    
    public static String getDireccionCliente(){
        return direccionCliente;
    }
//*****Repartidor */
    public static Double getCoordenadaXRepartidor(){
        return coordenadaXRepartidor;
    }

    public static Double getCoordenadaYRepartidor(){
        return coordenadaYRepartidor;
    }

}
