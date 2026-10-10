package com.example.controladoresRestaurante;

public class Pendiente {
//Atributos
    private int idPedido;
    private String nombreCliente;
    private int estado;
//Metodo constructor
    public Pendiente(int idPedido,String nombreCliente, int estado){
        this.idPedido = idPedido;
        this.nombreCliente = nombreCliente;
        this.estado = estado;
    }
//Metodos de retorno de valores
    public int getId(){
        return idPedido;
    }

    public String getNombre(){
        return nombreCliente;
    }

    public int getEstado(){
        return estado;
    }


}
