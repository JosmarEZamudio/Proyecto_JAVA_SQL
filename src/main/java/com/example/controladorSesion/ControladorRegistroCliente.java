//Paquete donde esta el controlador
package com.example.controladorSesion;
import java.io.IOException;
//Importar librerias de sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//Importar objetos de fxml
import javafx.fxml.FXML;
//import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
//Importar librerias para cambios de ventana
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//Import conector y dialogos de mensajes
import com.example.controladorConector;
import com.example.dialogosMensajes;

public class ControladorRegistroCliente {
//Botones
    @FXML 
    private Button btnCancelar;
    @FXML 
    private Button btnRegistrar;
//Text Field
    @FXML 
    private TextField txtNombre;
    @FXML 
    private TextField txtApellido;
    @FXML 
    private TextField txtTelefono;
    @FXML 
    private TextField txtCorreo;
    @FXML 
    private TextField txtPassword;
//Metodo para registrar un cliente nuevo
    @FXML 
    private void registrarCliente() throws SQLException{
        //Obtener textos
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String telefono = txtTelefono.getText();
            String correo = txtCorreo.getText();
            String password = txtPassword.getText();
        //Validacion Text vacios
        if (txtNombre.getText().isBlank() ||
            txtApellido.getText().isBlank() ||
            txtTelefono.getText().isBlank() ||
            txtCorreo.getText().isBlank() ||
            txtPassword.getText().isBlank()){
            dialogosMensajes.mensajeInformacion("Confimar registro.",
                "Todos los datos son necesarios, ¡¡intente de nuevo!!"
            );
                return;        
        }
        //Inicia consulta, aqui se crea la sentencia sql
            int resultado = 0;
        String campos = "nombre,apellido_paterno,telefono,correo,contrasena";
        String sql = "INSERT INTO cliente("+campos+") VALUES(?,?,?,?,?)";
//***** Empieza conexion e insersion      
        try(Connection conexion = controladorConector.conectar();){
            conexion.setAutoCommit(false);
        //Segundo try
            try{
            PreparedStatement agregar = conexion.prepareStatement(sql);
        
            agregar.setString(1, nombre);
            agregar.setString(2, apellido);
            agregar.setString(3, telefono);
            agregar.setString(4, correo);
            agregar.setString(5, password);
        //Cuantas filas fueron afectadas 
            resultado = agregar.executeUpdate();
        conexion.commit();
            }catch(SQLException e){
                if(conexion != null){
                    conexion.rollback();
                    throw e;
                }
            }
            if (resultado == 1){
            //Limpiar txt
                txtApellido.clear();
                txtTelefono.clear();
                txtCorreo.clear();
                txtPassword.clear(); 
            //Mensaje y retorno
                dialogosMensajes.mensajeInformacion("Registo exitoso.", 
                "Usted ha sido registrado correctamente."); 
                cancelar();
                return;
            }
            else {
                dialogosMensajes.mensajeError("Error de registro", 
                "El registro no pudo ser completado.");
            } 
        }//Cierra (connection y prepared) try
        catch(SQLException e){ //Mensaje de error para la insersion de sql
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo conectar con la base de datos, si persiste el error contactar a soporte.");
            return;
        } //Mensaje de error para la carga del fxml
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se cargar la interfaz, intente de nuevo, si persiste el error contactar a soporte.");
            return;
        }
    }

//Metodo para regresar a la ventana de saluda
   @FXML 
     private void cancelar() throws IOException{
    FXMLLoader loaderSaludo = new FXMLLoader(
            getClass().getResource("/com/example/saludoInicial.fxml")
        );
    Pane saludo = loaderSaludo.load();
    Scene escenaActual = btnCancelar.getScene();

    ScrollPane contenedor1 = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
    contenedor1.setContent(saludo);
    } 
    
}
