package com.example.controladoresRepartidor;
//Importar librerias para trabajar con objetos fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
//Importar librerias para trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//Librerias,clases adicionales
import java.io.IOException;

import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRepartidor;

public class ControladorPrincipal {
//Objetos txt 
    @FXML 
    private TextField txtCoordenadaX;
    @FXML 
    private TextField txtCoordenadaY;
//Objetos de menu fxml
    @FXML 
    private MenuButton mnbOpciones;
    @FXML 
    private MenuItem mniInformacion;
    @FXML 
    private MenuItem mniHistorial;
//Objetos boton fxml
    @FXML
    private Button btnRegistrar;
    @FXML 
    private Button btnIniciar;
    @FXML 
    private Button btnActivar;
    @FXML 
    private Button btnDesactivar;

//Metodo de menu desplegable para ir a la interfaz informacion personal
    @FXML 
    private void interfazPersonal() throws IOException {
    //Cargar archivo fxml
        try{
            FXMLLoader loaderInformacion = new FXMLLoader(
                getClass().getResource("/com/example/interfazrepartidor/interfazInformacion.fxml")
            );
        //Cargar raiz
            Pane informacion = loaderInformacion.load();
            Scene escenaNueva = mnbOpciones.getScene();
        //Obtener contenedor y setear el contenido
            ScrollPane contenedor = (ScrollPane) escenaNueva.lookup("#contenedorprincipal");
            contenedor.setContent(informacion);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo de menu desplegable para ir a la interfaz historial pedidos
    @FXML 
    private void interfazHistorial () throws IOException{
    //Cargar fxml
        try{
            FXMLLoader loaderHistorial = new FXMLLoader(
                getClass().getResource("")
            );
        //Cargar raiz
            Pane historial = loaderHistorial.load();
            Scene escenaNueva = mnbOpciones.getScene();
        //Encontrar contenedor y setear contenido
            ScrollPane contenedor = (ScrollPane) escenaNueva.lookup("#contenedorprincipal");
            contenedor.setContent(historial);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo para mostar la interfaz de registro
    @FXML
    private void registrar() throws IOException{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderRegistrar = new FXMLLoader(
                getClass().getResource("/com/example/interfazrepartidor/interfazRegistro.fxml")
            );
        //Cargar raiz y obtener escena actual
            Pane registrar = loaderRegistrar.load();
            Scene escenaActual = btnRegistrar.getScene();
        //Encontrar el contenedor y setear
            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(registrar);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo para mostrar la interfaz de inicio de sesion
    @FXML 
    private void iniciar() throws IOException{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderSesion = new FXMLLoader(
                getClass().getResource("/com/example/interfazrepartidor/interfazSesion.fxml")
            );
        //Cargar escena
            Scene escenaNueva = new Scene(loaderSesion.load());
        //Crear nueva ventana y setear escena
            Stage ventanaNueva = new Stage();
            ventanaNueva.setTitle("Iniciar sesion repartidor");
            ventanaNueva.setScene(escenaNueva);
            ventanaNueva.initModality(Modality.APPLICATION_MODAL);
            ventanaNueva.setOnHidden(event ->{
                initialize();
            });
            ventanaNueva.showAndWait();
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo para guardar ubicacion del repartidor
    @FXML 
    private void guardarUbicacion() throws SQLException{
    //Validar txt en blanco
        if(txtCoordenadaX.getText().isBlank() || txtCoordenadaY.getText().isBlank()){
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "La ubicacion no existe!!");
            return;
        }
    //Obtener variables a utilizar
        int coordenadaX = Integer.valueOf(txtCoordenadaX.getText());
        int coordenadaY = Integer.valueOf(txtCoordenadaY.getText());
        int idRepartidor = datosSesionRepartidor.getIdRepartidor();
    //Sentencia sql
        String sql = """
                    UPDATE repartidor
                    SET x_actual = ?, y_actual = ?, activo = ?, disponibilidad = ?
                    WHERE id_repartidor = ?
                """;
    //Declarar conexion como nula
        Connection conexion = null;
    //Empieza try
        try{
            conexion = controladorConector.conectar();
            conexion.setAutoCommit(false);
        //Empieza try Prepared
            try(PreparedStatement guardar = conexion.prepareStatement(sql);){
                guardar.setInt(1, coordenadaX);
                guardar.setInt(2, coordenadaY);
                guardar.setInt(3, 1);
                guardar.setInt(4, 1);
                guardar.setInt(5, idRepartidor);
            //Ejecutar update
                guardar.executeUpdate();
            //Mostrar mensaje de exito
                dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                "Ubicacion guardada exitosamente.");
            //Guardar ubicacion en una variable statica
                datosSesionRepartidor.setUbicacion(coordenadaX, coordenadaY);
            }//Cierra try Prepared
            conexion.commit();  //Relizar commit
            validacionUbicacion();
        }//Finaliza try
        catch(SQLException e){
            if(conexion!=null){
                try{
                    conexion.rollback();
                }
                catch(SQLException v){
                    e.addSuppressed(v);
                }//Termina catch v
            }
        }//Termina catch e
        finally{
            if(conexion!=null){
                conexion.close();
            }
        }//Termina finally   
    }

//Metodo para desactivar repartidor
    @FXML
    private void desactivar() throws SQLException{
    //Obtener id de repartidor
        int idRepartidor = datosSesionRepartidor.getIdRepartidor();
    //Sentencia sql
        String sql = """
            UPDATE repartidor
            SET activo = ?, disponibilidad = ?
            WHERE id_repartidor = ?
                """;
        
        Connection conexion = null;
        try{
            conexion = controladorConector.conectar();
            conexion.setAutoCommit(false);
            try(PreparedStatement editar = conexion.prepareStatement(sql);){
                editar.setInt(1, 0);
                editar.setInt(2,0);
                editar.setInt(3,idRepartidor);

                editar.executeUpdate();
            }
        
            conexion.commit();
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Cesion cerrada, hasta luego.");
            txtCoordenadaX.clear();
            txtCoordenadaY.clear();
        //Resetear valores de inicio de sesion
            datosSesionRepartidor.setRepartidor(0, "");
            datosSesionRepartidor.setUbicacion(null, null);
            validacionId();
        }
        catch(SQLException e){
            if(conexion!=null){
                try{
                    conexion.rollback();
                }
                catch(SQLException v){
                    e.addSuppressed(v);
                }
            }
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cerrar la sesion.");
        }finally{
            if(conexion!=null){
                conexion.close();
            }
        }
    }

//Metodo validacionId para mostrar ciertas opciones disponibles o no despues del inicio de sesion
    @FXML 
    private void validacionId(){
        if(datosSesionRepartidor.getIdRepartidor() == 0){
            btnIniciar.setVisible(true);
            btnRegistrar.setVisible(true);
            btnActivar.setDisable(true);
            btnDesactivar.setDisable(true);
            mniInformacion.setDisable(true);
        }
        else{
            btnIniciar.setVisible(false);
            btnRegistrar.setVisible(false);
            btnActivar.setDisable(false);
            btnDesactivar.setDisable(false);
            mniInformacion.setDisable(false);
        }
    }

//Metodo para validar ubicacion
    @FXML 
    private void validacionUbicacion(){
        if(datosSesionRepartidor.getCoordenadaX()!=null && datosSesionRepartidor.getCoordenadaY()!=null){
            txtCoordenadaX.setText(String.valueOf(datosSesionRepartidor.getCoordenadaX()));
            txtCoordenadaY.setText(String.valueOf(datosSesionRepartidor.getCoordenadaY()));
            btnActivar.setDisable(true);
            txtCoordenadaX.setDisable(true);
            txtCoordenadaY.setDisable(true);
        }
    }

//Metodo de inicializacion
    @FXML 
    private void initialize(){
        validacionId();
        validacionUbicacion();
    }
    
}
