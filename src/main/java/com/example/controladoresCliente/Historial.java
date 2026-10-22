package com.example.controladoresCliente;

import java.time.LocalDate;

public class Historial {
    private int id_pedido;
    private LocalDate fecha;
    private String estado;

    public Historial(int idP, LocalDate fecha, String estado){
        this.id_pedido = idP;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getId(){
        return id_pedido;
    }

    public LocalDate getFecha(){
        return fecha;
    }

    public String getEstado(){
        return estado;
    }
    
}
