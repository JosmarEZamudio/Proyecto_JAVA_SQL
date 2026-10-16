package com.example.controladoresRestaurante;

public class Pendiente {
//Atributos
    private int idPedido;
    private int idCliente;
    private String nombreCliente;
    private int estado;
//Metodo constructor
    public Pendiente(int idPedido,int idCliente, String nombreCliente, int estado){
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.estado = estado;
    }
//Metodos de retorno de valores
    public int getId(){
        return idPedido;
    }

    public int getCliente(){
        return idCliente;
    }

    public String getNombre(){
        return nombreCliente;
    }

    public int getEstado(){
        return estado;
    }


}
