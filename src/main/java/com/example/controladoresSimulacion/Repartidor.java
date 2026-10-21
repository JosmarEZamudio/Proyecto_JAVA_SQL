package com.example.controladoresSimulacion;

public class Repartidor {
//Variables para guardar los datos de la lista de repartidores
    private Double coordenadaX = null;
    private Double coordenadaY = null;
    private Integer idRepartidor = null;
//Metodo constructor
    public Repartidor(Double x, Double y, Integer id){
        coordenadaX = x;
        coordenadaY = y;
        idRepartidor = id;
    }
//Metodos de retorno
    public Double getCoordenadaX(){
        return coordenadaX;
    }

    public Double getCoordenadaY(){
        return coordenadaY;
    }

    public Integer getId(){
        return idRepartidor;
    }

}
