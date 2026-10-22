package com.example.controladoresRestaurante;
//Librerias de objetos fxml
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.image.Image;
//Librerias de sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias, clases, objetos adicionales
import com.example.controladorConectorPool;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRestaurante;
import java.io.IOException;
import java.io.File;

public class ControladorDesplegarProducto {
//Objetos label fxml
    @FXML 
    private Label lblBienvenido;
    @FXML 
    private Label lblProducto;
    @FXML 
    private Label lblProductoActivo;
    @FXML 
    private Label lblProductoInactivo;
//Objetos contenedores
    @FXML 
    private ScrollPane contenedorProducto;
    @FXML 
    private GridPane gdpProducto;

//Metodo para mostrar productos en gridpane
    @FXML 
    private void mostrar() throws SQLException{
    //Cargar id_restaurante
        int id = datosSesionRestaurante.getIdRestaurante();
    //Variables donde se guardara los resultados de la consulta
        int id_producto;
        String nombre;
        int precio;
        int disponibilidad;
        int fila=0;
    //Sentencia aqñ
        String sql = "SELECT id_producto,nombre,precio,disponibilidad FROM producto WHERE id_restaurante = ?";
    //Inicia try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql)){
                consultar.setInt(1, id);
                try(ResultSet resultado =  consultar.executeQuery();){
                    while(resultado.next()){
                        id_producto = resultado.getInt("id_producto");
                        nombre = resultado.getString("nombre");
                        precio = resultado.getInt("precio");
                        disponibilidad = resultado.getInt("disponibilidad");
                        crearTarjeta(id, id_producto, nombre, precio, disponibilidad,fila);
                        fila++;
                    }
                }//Cierra segundo try ResultSet
        }//Cierra primer try Connection y Prepared
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
                "No se pudo conectar con la base de datos, en caso de persistir con el problema contactar a soporte."
            );
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudieron cargar las imagenes, en caso de que el error persita contactar a soporte.");
        }
    }
//Metodo para desactivar la disponibilidad del producto
    private void desactivarProducto(int id_producto) throws SQLException{
        //Obtener id_restaurante
        int id = datosSesionRestaurante.getIdRestaurante();
        //Sentencia sql
        String sql = "UPDATE producto SET disponibilidad = 0 WHERE id_restaurante = ? AND id_producto = ?";
        //Incia try Connection y prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement editar = conexion.prepareStatement(sql);){
                editar.setInt(1, id);
                editar.setInt(2, id_producto);
                editar.executeUpdate();
        }//Ciera try
        lblProductoInactivo.setText("Productos inactivos: "+String.valueOf(consultarProductosInactivos()));
        lblProductoActivo.setText("Productos activos: "+String.valueOf(consultarProductosActivos()));   
    }
//Metodo para activar disponibildad del producto 
    private void activarProducto(int id_producto) throws SQLException{
    //Obtener id_restaurante
        int id = datosSesionRestaurante.getIdRestaurante();
    //Sentencia sql
        String sql = "UPDATE producto SET disponibilidad = 1 WHERE id_restaurante = ? AND id_producto=?";
    //Incia try Connection y Preprared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement editar = conexion.prepareStatement(sql)){
                editar.setInt(1, id);
                editar.setInt(2, id_producto);
                editar.executeUpdate();
        }//Cierra try
        lblProductoInactivo.setText("Productos inactivos: "+String.valueOf(consultarProductosInactivos()));
        lblProductoActivo.setText("Productos activos: "+String.valueOf(consultarProductosActivos()));     
    }

//Metodo para crear la tarjeta que ira dentro de cada casilla del GridPane
//Se exapande solo, asi que puede llegar hasta n elementos
    @FXML 
    private void crearTarjeta(int idR, int idP, String nombre, 
        int precio,int disponibilidad, int fila
    ) throws IOException{
        String id_restaurante = String.valueOf(idR);
        String id_producto = String.valueOf(idP);
        String ruta = "datos/imagenes/"+id_restaurante+"/"+id_producto+".jpg";
        //Crear objetos que perteneceran a la casilla correspondiente
            VBox vbResultado = new VBox();
            Label lblNombre = new Label();
            Label lblPrecio = new Label();
            Label lblDisponibilidad = new Label();
            Button btnDisponible = new Button();
            Button btnNoDisponible = new Button();
            Image imagen = new Image(
               new File(ruta).toURI().toString()
            );
            ImageView vista = new ImageView(imagen);
            vista.setFitWidth(100);
            vista.setFitHeight(100);
            vista.setPreserveRatio(false);
        //Dar formato, valores y funciones
            lblNombre.setText(nombre);
            lblPrecio.setText("Precio: "+String.valueOf(precio));
            if(disponibilidad == 0){
                lblDisponibilidad.setText("Disponibilidad: No");
            }else{
                lblDisponibilidad.setText("Disponibilidad: Si");
            }
            btnDisponible.setText("Disponible");
            btnNoDisponible.setText("No disponible");
            //Se le da la funcion para activar producto
            btnDisponible.setOnAction(e ->{
                try{activarProducto(idP);
                    lblDisponibilidad.setText("Disponibilidad: Si");
                }
                catch(SQLException c){
                    dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar informacion adicional.");
                    return;
                }
            });
            //Se le da la funcion desactivar el producto
            btnNoDisponible.setOnAction(e ->{
                try{desactivarProducto(idP);
                    lblDisponibilidad.setText("Disponibilidad: No");
                }
                catch(SQLException c){
                    dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar informacion adicional.");
                    return;
                }
            });

        //Dar objetos al vbox; darlo al grid pane y ajustar posicion
            vbResultado.getChildren().addAll(lblNombre,vista,lblPrecio,lblDisponibilidad,btnDisponible,btnNoDisponible);
            vbResultado.setAlignment(Pos.CENTER);
            vbResultado.setSpacing(3); //Espacio entre elementos del vbox
            gdpProducto.add(vbResultado,fila, 0);
            return;
        }

//Metodo para hacer consulta de cantidad de producto
    private int consultarCantidad() throws SQLException{
    //Cargar id del restaurante y declarar variable de retorno
        int id = datosSesionRestaurante.getIdRestaurante();
        int cantidad;
    //Sentencia sql
        String sql = "SELECT COUNT(*) AS resultado FROM producto WHERE id_restaurante = ?";
    //Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql)){
                consultar.setInt(1, id);
                try(ResultSet resultado = consultar.executeQuery();){
                    if(resultado.next()){
                        cantidad = resultado.getInt("resultado");
                        return cantidad;
                    }
                }//Cierra segundo try ResultSet
        }//Cierra primer try Connection y Prepared
        return 0;
    }
//Metodo para consultar producto activos 
    private int consultarProductosActivos() throws SQLException{
    //Incializar variable de retorno y obtener id_restaurante 
        int valor = 0;
        int id = datosSesionRestaurante.getIdRestaurante();
    //Sentencia sql
        String sql = "SELECT COUNT(id_producto) AS cantidad FROM producto WHERE id_restaurante = ? AND disponibilidad = ?";
    //Inicia try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                consultar.setInt(2, 1);
                try(ResultSet resultado = consultar.executeQuery();){
                    if(resultado.next()){
                        valor = resultado.getInt("cantidad");
                        return valor;
                    }
                }//Cierra segundo try ResultSet
        }//Cierra primer try Connection y Prepared
        return 0;
    }

//Metodo para consultar producto inactivos
    private int consultarProductosInactivos() throws SQLException{
    //Incializar valor de retorno y obtener id_restaurante 
        int valor = 0;
        int id = datosSesionRestaurante.getIdRestaurante();
    //Sentencia sql
        String sql = "SELECT COUNT(id_producto) AS cantidad FROM producto WHERE id_restaurante= ? AND disponibilidad = ?";
        //Iniciar try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                consultar.setInt(2, 0);
                try(ResultSet resultado = consultar.executeQuery();){
                    if(resultado.next()){
                        valor = resultado.getInt("cantidad");
                        return valor;
                    }
                }//Cierar segundo try ResultSet
        }//Cierra primer try Connection y Prepared
        return 0;
    }

//Metodo de inicilizacion
    @FXML 
    public void initialize() throws SQLException{
    //Ajustar separacion vertical de cada casilla del GridPane
        gdpProducto.setHgap(30);
    //Ajustar padding del GridPane
        gdpProducto.setPadding(new Insets(100, 25, 0, 25)); 
        mostrar();
        try{
            lblBienvenido.setText("Bienvenido "+datosSesionRestaurante.getNombreRestaurante());
            lblProducto.setText("Producto: "+consultarCantidad());
            lblProductoInactivo.setText("Productos inactivos: "+consultarProductosInactivos());
            lblProductoActivo.setText("Producto activos: "+consultarProductosActivos());
        }
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar informacion adicional.");
            return;
        }    
    }
    
}
