package com.example;

public class UbicacionesResultado {
//Declarar atributos de la clase
    private final String nombre;
    private final double latitud;
    private final double longitud;
//Metodo constructor
    public UbicacionesResultado(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }
//Metodos de retorno
    public String getNombre() {
        return nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
