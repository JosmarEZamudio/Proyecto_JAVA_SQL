package com.example.controladoresRepartidor;
//Libreria objetos de fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
//Librerias de sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias para trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//Librerias adicionales
import java.io.IOException;
import com.example.controladorConectorPool;
import com.example.dialogosMensajes;

public class ControladorRegistro {
//Objetos txt de fxml
    @FXML 
    private TextField txtNombre;
    @FXML 
    private TextField txtApellidoPaterno;
    @FXML 
    private TextField txtApellidoMaterno;
    @FXML 
    private TextField txtVehiculo;
    @FXML 
    private TextField txtPlacas;
    @FXML 
    private TextField txtPassword;
    @FXML 
    private TextField txtCorreo;
//Objetos button de fxml
    @FXML 
    private Button btnRegistrar;
    @FXML 
    private Button btnCancelar;


//Metodo para registrar
    @FXML 
    private void registrar()throws SQLException{
    //Obtener escena Actual
        Scene escenaActual = btnRegistrar.getScene();
    //Validar txt en blanco
        if(txtNombre.getText().isBlank() || txtApellidoPaterno.getText().isBlank() ||
            txtApellidoMaterno.getText().isEmpty() || txtPassword.getText().isBlank()){
                dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                "Todos los datos son requeridos.");
                return;
        }
    //Obtener datos personas de txt
        String nombre = txtNombre.getText();
        String apellidoPaterno = txtApellidoPaterno.getText();
        String apellidoMaterno = txtApellidoMaterno.getText();
        String correo = txtCorreo.getText();
        String password = txtPassword.getText();
    //Obtener datos del vehiculo de txt
        //String vehiculo = txtVehiculo.getText();
        //String placas = txtPlacas.getText();
    //Inicializa la verificacion en 0
        int verificado = 0;
        int contador = 0;
    //Sentencia sql  
        String sentenciaSqlInsertar = "INSERT INTO repartidor(nombre,apellido_paterno,apellido_materno,correo,contrasena,verificado)"+ 
                    " VALUES(?,?,?,?,?,?) ";
        String setenciaSqlconsulta = "SELECT COUNT(*) AS resultado FROM repartidor WHERE correo = ?";
    //Inicia try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement agregar = conexion.prepareStatement(sentenciaSqlInsertar);
            PreparedStatement consultar = conexion.prepareStatement(setenciaSqlconsulta)){

                consultar.setString(1, correo);
            //Iniciar segundo try
                try(ResultSet resultado = consultar.executeQuery();){
                //Recorrer la consulta  
                    resultado.next();
                    contador = resultado.getInt("resultado");
                        if(contador==1){
                            dialogosMensajes.mensajeInformacion("Informacion error", 
                            "Este correo "+correo+" ya esta en uso.");
                            return;
                        }
                }//Termina segundo try ResultSet
            //Agregar datos
                agregar.setString(1, nombre);
                agregar.setString(2, apellidoPaterno);
                agregar.setString(3, apellidoMaterno);
                agregar.setString(4,correo);
                agregar.setString(5, password);
                agregar.setInt(6, verificado);
            //Ejecutar insersion
                int i = agregar.executeUpdate();  
            //Validar si el usuario se registro
                if (i == 1){
                    dialogosMensajes.mensajeInformacion("Informacion", 
                    "Registro realizado exitosamente, espere a ser verificado.");
                    interfazPrincipal(escenaActual);
                }
            }//Termina primer try
            catch(SQLException e){
                dialogosMensajes.mensajeError("Informacion error", 
                "No se pudo realizar el registro, intente de nuevo o contacte a soporte.");
           
            }
            catch(IOException e){
                dialogosMensajes.mensajeError("Informacion error", 
                "No se pudo cargar la interfaz, intente de nuevo.");
            }
    }

//Metodo para el boton cancelar
    @FXML 
    private void cancelar() throws IOException{
        try{
            Scene escenaActual = btnCancelar.getScene();
            interfazPrincipal(escenaActual);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz.");
            return;
        }
    }

//Metodo para registrar por medio de boton
    @FXML 
    private void registrarPressed(KeyEvent event) throws SQLException{
        if(event.getCode()==KeyCode.ENTER){
            registrar();
        }

    }


//Metodo para ver la interfaz del repartidor
    @FXML 
    private void interfazPrincipal(Scene escenaActual) throws IOException{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderRepartidor = new FXMLLoader(
                getClass().getResource("/com/example/interfazrepartidor/interfazPrincipal.fxml")
            );
    //Cargar raiz y encontrar la escena actual
            Pane repartidor = loaderRepartidor.load();
    //Encontrar contenedor y setear contenido
            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(repartidor);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz.");
            return;
        }
    }

    
}
