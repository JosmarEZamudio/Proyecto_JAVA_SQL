package com.example.controladorSesion;
import java.io.IOException;
//Librerias para usar sql y mensajes
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias adicionales
import com.example.controladorConectorPool;
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
    //Incializar varible para verificar cuantos registros fueron agregados
        int resultado = 0;
    //Validar txt en blanco
        if (txtNombre.getText().isBlank() ||
            txtTelefono.getText().isBlank() ||
            txtCorreo.getText().isBlank() ||
            txtPassword.getText().isBlank()
        ){
            dialogosMensajes.mensajeError("Dato en blanco", 
            "Todos los datos son necesarios, ¡¡intente de nuevo!!");
            return;
        }
    //Obtener datos de txt
        String nombre = txtNombre.getText();
        String telefono = txtTelefono.getText();
        String correo = txtCorreo.getText();
        String password = txtPassword.getText();
    //Sentencias sql
        String campos = "nombre,telefono,correo,contrasena";
        String sqlInsertar = "INSERT INTO restaurante("+campos+") VALUES(?,?,?,?)";
        String sqlConsultar = "SELECT COUNT(*) AS resultado FROM restaurante WHERE correo = ?";
    //Empieza try Connection
        try(Connection conexion = controladorConectorPool.conectar();){
            conexion.setAutoCommit(false);
        //***************** Empieza consulta
            try(PreparedStatement consultar = conexion.prepareStatement(sqlConsultar);){
                consultar.setString(1, correo);
                try(ResultSet consulta = consultar.executeQuery();){
                    consulta.next();
                    int existe = consulta.getInt("resultado");
                    if (existe == 1){
                        dialogosMensajes.mensajeInformacion("Informacion error", 
                        "Este correo "+correo+" ya esta en uso.");
                        return;
                    }
                }//Termina segundo try ResultSet
            }//Termina primer try Prepared
        //************Termina consulta
        //Empieza try Prepared (insertar)
            try{
            PreparedStatement agregar = conexion.prepareStatement(sqlInsertar);
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
        //Validar si se registro el usuario
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
            "No se pudo cargar la interfaz, intente de nuevo.");
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
