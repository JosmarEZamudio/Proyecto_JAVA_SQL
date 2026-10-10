package com.example.controladoresCliente;

import java.time.LocalDate;

public class Historial {
    private int id_pedido;
    private LocalDate fecha;
    private int estado;

    public Historial(int idP, LocalDate fecha, int estado){
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

    public int getEstado(){
        return estado;
    }
    
}
