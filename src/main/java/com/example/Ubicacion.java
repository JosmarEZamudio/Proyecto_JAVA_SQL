package com.example;

public class Ubicacion {

    private Integer id = null;
    private String direccion;

    public Ubicacion(Integer id, String direccion) {
        this.id = id;
        this.direccion = direccion;
    }

    public Integer getId() {
        return id;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public String toString() {
        return direccion;
    }
}
