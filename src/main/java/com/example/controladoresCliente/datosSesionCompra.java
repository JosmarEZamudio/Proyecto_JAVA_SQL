package com.example.controladoresCliente;
//Clase que mantiene a flote el id del cliente y datos de compra
public class datosSesionCompra {
    public static String nombreRestaurante;
    public static int idRestaurante;
    public static int idPedido;
    public static int cantidad = 0;
    public static int precio = 0 ;
//Metodos de asignacion de valores y calculos
    public static void guardarRestaurante(int idR, String nombre){
        idRestaurante = idR;
        nombreRestaurante = nombre;
    }

    public static void sumaCompra(int cant, int prec){
        cantidad = cantidad + cant;
        precio = precio + prec;
    }

    public static void quitarCompra(int cant, int prec){
        cantidad = cantidad - cant;
        precio = precio - prec;
    }

    public static void inicializarCompra(){
        cantidad = 0;
        precio = 0;
    }

    public static void idpedido(int idP){
        idPedido = idP;
    }

//Metodos de retornos
    public static int getIdRestaurante(){
        return idRestaurante;
    }

    public static String getNombreRestaurante(){
        return nombreRestaurante;
    }

    public static int getCantidad(){
        return cantidad;
    }

    public static int getTotal(){
        return precio;
    }

    public static int getIdPedido(){
        return idPedido;
    }

}
