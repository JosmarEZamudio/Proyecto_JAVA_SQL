package com.example.controladoresCliente;

public class Producto {
    private int idProducto;
    private int cantidad;
    private String nombre;
    private int precio;

//Metodo constructor
    public Producto(int idP, String nombre,int cantidad, int precio){
        this.idProducto = idP;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
    }

//Metodos de retorno
    public int getId(){
        return idProducto;
    }

    public String getNombre(){
        return nombre;
    }

    public int getCantidad(){
        return cantidad;
    }

    public int getPrecio(){
        return precio;
    }
    
//Metodo donde se guarda la cantidad
    public void setCantidad(int cantidad){
        this.cantidad = cantidad;
    }

      
    
}
