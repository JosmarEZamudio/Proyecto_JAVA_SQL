package com.example.controladorSesion;
import java.io.IOException;
//Librerias para usar sql y mensajes
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.controladorConector;
import com.example.dialogosMensajes;
//Componente de java fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
//Componentes de la ventana actual actual
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class ControladorRegistroRestaurante {
//Objetos de fxml
    @FXML
    private Button btnCancelar;
    @FXML 
    private Button btnRegistrar;

    @FXML 
    private TextField txtNombre;
    @FXML 
    private TextField txtTelefono;
    @FXML 
    private TextField txtCorreo;
    @FXML 
    private TextField txtPassword;

//Metodo para registrar restaurante
    @FXML 
    private void registrarRestaurante() throws Exception{

        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String correo = txtCorreo.getText();
        String password = txtPassword.getText();

        int resultado = 0;

        if (txtNombre.getText().isBlank() ||
            txtTelefono.getText().isBlank() ||
            txtCorreo.getText().isBlank() ||
            txtPassword.getText().isBlank()
        ){
            dialogosMensajes.mensajeError("Dato en blanco", 
            "Todos los datos son necesarios, ¡¡intente de nuevo!!");
            return;
        }

        String campos = "nombre,telefono,correo,contrasena";
        String sql = "INSERT INTO restaurante("+campos+") VALUES(?,?,?,?)";
    //Empieza try Connection
        try(Connection conexion = controladorConector.conectar();){

            conexion.setAutoCommit(false);
        //Empieza try para Prepared
            try{
            PreparedStatement agregar = conexion.prepareStatement(sql);
                agregar.setString(1, nombre);
                agregar.setString(2, telefono);
                agregar.setString(3, correo);
                agregar.setString(4, password);
                resultado = agregar.executeUpdate();
            conexion.commit();
            }catch(SQLException e){
                if(conexion!=null){
                    conexion.rollback();
                    throw e;
                } //Atrapar, ejecutar y escalar error de Prepared
            } 

            if (resultado == 1){
                dialogosMensajes.mensajeInformacion("Registro exitoso.",
                 "Su negocio has sido registrado correctamente");
                 cancelar();
            }else{
                dialogosMensajes.mensajeError("Error en el registro", 
                    "Su registro no ha sido realizado con exito, intente de nuevo."
                );
            }
        }//Ciera primer try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo realizar el registro, intente de nuevo.");
            return;
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la intfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para mostrar saludo
    @FXML 
    private void cancelar() throws IOException{
        FXMLLoader loaderSaludo = new FXMLLoader(
            getClass().getResource("/com/example/saludoInicial.fxml")
        );
        Pane saludo = loaderSaludo.load();
        Scene escenaActual = btnCancelar.getScene();

        AnchorPane contenedor = (AnchorPane) escenaActual.getRoot();
        ScrollPane contenedor1 = (ScrollPane) contenedor.lookup("#contenedorprincipal");

        contenedor1.setContent(saludo);
    }
}
