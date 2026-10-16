package com.example.controladoresRestaurante;

public class Detalle {
//Atributos
    private int id;
    private String nombre;
    private String producto;
    private int cantidad;
//Metodo constructor
    public Detalle(int id, String nombre, String producto, int cantidad){
        this.id = id;
        this.nombre = nombre;
        this.producto = producto;
        this.cantidad = cantidad;
    }
    
//Metodo de retorno
    public int getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public String getProducto(){
        return producto;
    }

    public int getCantidad(){
        return cantidad;
    }

}
