package com.example.controladoresRestaurante;
//Librerias
import java.time.LocalDate;

public class Historial {
    private int idCliente;
    private String nombre;
    private LocalDate fecha;
    private String estado;
    private Integer total;

    public Historial(int id, String nombre, LocalDate fecha, String estado, Integer total) {
        this.idCliente = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.estado = estado;
        this.total = total;
    }

    public int getId() {
        return idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public Integer getTotal() {
        return total;
    }

}
