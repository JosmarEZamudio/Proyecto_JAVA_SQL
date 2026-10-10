package com.example.controladoresRestaurante;

public class Producto {
    private int id;
    private String nombre;
    private int precio;
    private int disponibilidad;
//Metodo constructor
    public Producto(int id, String nombre, int precio, int disponibilidad){
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.disponibilidad =disponibilidad;
    }
    
    public int getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    } 

    public int getPrecio(){
        return precio;
    }

    public int getDisponibilidad(){
        return disponibilidad;
    }

}
