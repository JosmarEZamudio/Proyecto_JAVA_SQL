package com.example.controladoresCliente;
//Objetos de fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
//Trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//Librerias de java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias adicionales
import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionCliente;
import java.io.IOException;

public class ControladorInformacion {
//Botones
    @FXML 
    private Button btnEditar;
    @FXML 
    private Button btnGuardar;
    @FXML 
    private Button btnCancelar;
//Text
    @FXML 
    private TextField txtNombre;
    @FXML 
    private TextField txtApellido;
    @FXML 
    private TextField txtTelefono;
    @FXML 
    private TextField txtCorreo;

//Metodo para consultar los datos del cliente y setear en text
    private void consultaDatos() throws SQLException{
    //Cargar id del cliente
        int idCliente = datosSesionCliente.getIdCliente();
    //Sentencias sql 
        String campos = "nombre,apellido_paterno,telefono,correo";
        String sql = "SELECT "+campos+" FROM cliente WHERE id_cliente = ?";
    //Abre primer try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql)){
                consultar.setInt(1, idCliente);
                try(ResultSet resultado = consultar.executeQuery();){
                    if(resultado.next()){
                        txtNombre.setText(resultado.getString("nombre"));
                        txtApellido.setText(resultado.getString("apellido_paterno"));
                        txtTelefono.setText(resultado.getString("telefono"));
                        txtCorreo.setText(resultado.getString("correo"));
                    }
                }//Cierra segundo try ResultSet
            }//Cierra primer try
    }
//Metodo para guardar la actualizacion de datos
    @FXML 
    private void guardar() throws SQLException{
    //Obtener id del cliente
        int id = datosSesionCliente.getIdCliente();
    //Setencia de sql 
        String sql = "UPDATE cliente SET nombre = ?, apellido_paterno = ?,telefono = ?, correo = ? WHERE id_cliente = ?";
    //Abre try Connection y PreparedStatment
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement actualizar = conexion.prepareStatement(sql)){
                actualizar.setString(1, txtNombre.getText());
                actualizar.setString(2, txtApellido.getText());
                actualizar.setString(3, txtTelefono.getText());
                actualizar.setString(4, txtCorreo.getText());
                actualizar.setInt(5, id);

                actualizar.executeUpdate();
        }//Cierra try

        dialogosMensajes.mensajeInformacion("Informacion actualizacion", 
        "Sus datos fueron actualizados correctamente.");
        //Desactivar elementos
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtTelefono.setDisable(true);
        txtCorreo.setDisable(true);
        btnGuardar.setDisable(true);
        btnEditar.setDisable(false);
    }

//Metodo para regresar
    @FXML 
    private void cancelar() throws IOException{
    //Cargar FXML
    try{ FXMLLoader loaderPrincipal = new FXMLLoader(
            getClass().getResource("/com/example/interfazcliente/interfazPrincipal.fxml")
        );
    //Cargar pane y obtener scene
        Pane principal = loaderPrincipal.load();
        Scene escenaActual = btnCancelar.getScene();
    //Encontrar scroll y setear
        ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
        contenedor.setContent(principal);
        }catch(IOException e){
            dialogosMensajes.mensajeError("Mensaje error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo para el boton de editar
    @FXML 
    private void editar(){
        txtApellido.setDisable(false);
        txtNombre.setDisable(false);
        txtTelefono.setDisable(false);
        txtCorreo.setDisable(false);
        btnGuardar.setDisable(false);
        btnEditar.setDisable(true);
    }

//Metodo de incializacion
    @FXML 
    private void initialize() throws SQLException{
        txtNombre.setDisable(true);
        txtApellido.setDisable(true);
        txtTelefono.setDisable(true);
        txtCorreo.setDisable(true);
        btnGuardar.setDisable(true);
        
        try{
            consultaDatos();
        } //Atrapar excepcion
        catch(SQLException e){
            dialogosMensajes.mensajeInformacion("Informacion error", 
            "No se pudo realizar la consulta de datos, intente de nuevo.");
        }
    }

    
}
