package com.example.controladoresCliente;

public class DetalleHistorial {
//Atributos de la clase
    private Integer idDetallePedido = null;
    private Integer cantidad = null;
    private Integer subtotal = null;
    private Integer precio = null;
    private String restaurante = null;
    private String producto = null;

//Metodo constructor
    public DetalleHistorial(Integer id, Integer cantidad, Integer subtotal,
            Integer precio, String restaurante, String producto){
        this.idDetallePedido = id;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.precio = precio;
        this.restaurante = restaurante;
        this.producto = producto;
    }

//Metodos de retorno
    public Integer getId(){
        return idDetallePedido;
    }

    public Integer getCantidad(){
        return cantidad;
    }

    public Integer getSubtotal(){
        return subtotal;
    }

    public Integer getPrecio(){
        return precio;
    }

    public String getRestaurante(){
        return restaurante;
    }

    public String getProducto(){
        return producto;
    }

}
