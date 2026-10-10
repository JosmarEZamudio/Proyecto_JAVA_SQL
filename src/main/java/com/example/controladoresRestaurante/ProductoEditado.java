package com.example.controladoresRestaurante;
//Clase para almacenar temporalmente datos del producto a editar
public class ProductoEditado {
    public static int idProducto;
    public static String nombreProducto;
    public static String precioProducto;
//Solo mantiene a flote el id
    public static void almacenarTemporal(int id){
        idProducto = id;
    } 

    public static int getIdProducto(){
        return idProducto;
    }

}
