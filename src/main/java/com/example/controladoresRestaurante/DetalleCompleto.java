package com.example.controladoresRestaurante;

public class DetalleCompleto{
//Atributos
    private int id;
    private String nombre;
    private String producto;
    private int cantidad;
    private int precio;
//Metodo constructor
    public DetalleCompleto(int id, String nombre, String producto, int cantidad, int precio){
        this.id = id;
        this.nombre = nombre;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
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

    public int getPrecio(){
        return precio;
    }

}
