package com.example.controladoresRepartidor;
//Librerias java
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextField;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias para trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//Librerias adicionales
import java.io.IOException;
import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRepartidor;

public class ControladorInformacion {
//Text
    @FXML 
    private TextField txtNombre;
    @FXML
    private TextField txtApellidoPaterno;
    @FXML 
    private TextField txtApellidoMaterno;
//Botones
    @FXML 
    private Button btnEditar;
    @FXML 
    private Button btnGuardar;
    @FXML 
    private Button btnRegresar;

//Metodo para consultar datos
    @FXML 
    private void consultarDatos() throws SQLException{
        int id = datosSesionRepartidor.getIdRepartidor();
        String nombre="";
        String apellidoPaterno="";
        String apellidoMaterno="";
    //Sentencia sql
        String sql = "SELECT nombre,apellido_paterno,apellido_materno FROM repartidor WHERE id_repartidor = ?";
    //Abre primer try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
        PreparedStatement consultar = conexion.prepareStatement(sql);){
            consultar.setInt(1, id);
        //Abre segundo try ResultSet
            try(ResultSet resultado = consultar.executeQuery();){
                if(resultado.next()){
                    nombre = resultado.getString("nombre");
                    apellidoPaterno = resultado.getString("apellido_paterno");
                    apellidoMaterno = resultado.getString("apellido_materno");
                }
            }//Cierra segundo try
        }//Cierra primer try
        txtNombre.setText(nombre);
        txtApellidoPaterno.setText(apellidoPaterno);
        txtApellidoMaterno.setText(apellidoMaterno);
    }

//Metodo para guardar los cambios
    @FXML 
    private void guardar() throws SQLException{
    //Validar txt blanco
        if(txtNombre.getText().isBlank() ||
            txtApellidoPaterno.getText().isEmpty() ||
            txtApellidoPaterno.getText().isEmpty()){
                dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                "Todos los campos deben tener datos.");
                return;
        }
    //Obtener datos importantes de insersion y clausula
        String nombre = txtNombre.getText();
        String apellidoPaterno = txtApellidoPaterno.getText();
        String apellidoMaterno = txtApellidoMaterno.getText();
        int idRepartidor = datosSesionRepartidor.getIdRepartidor();
    //Sentencia sql
        String sql = """
                    UPDATE repartidor
                    SET nombre = ?, apellido_paterno = ?, apellido_materno = ?
                    WHERE id_repartidor = ?
                """;
        Connection conexion = null;
        try{
            conexion = controladorConector.conectar();
            conexion.setAutoCommit(false);
           try(PreparedStatement actualizar = conexion.prepareStatement(sql);){
                actualizar.setString(1, nombre);
                actualizar.setString(2, apellidoPaterno);
                actualizar.setString(3, apellidoMaterno);
                actualizar.setInt(4, idRepartidor);
                actualizar.executeUpdate();
            }//Cierra TWR
                conexion.commit(); 
        }//Ciera try Connection y Prepared
        catch(SQLException e){
            if(conexion!=null){
                try{
                    conexion.rollback();
                }
                catch(SQLException v){
                    e.addSuppressed(v);
                }
            }
        }
        finally{
            if(conexion!=null){
                conexion.close();
            }
        }



    }
//Metodo para cancelar 
    @FXML 
    private void regresar()throws IOException{
    //Cargar archivo fxml
        try{
        FXMLLoader loaderPrincipal = new FXMLLoader(
            getClass().getResource("/com/example/interfazrepartidor/interfazPrincipal.fxml")
        );
    //Cargar scene y obtener scene actual
        Pane principal = loaderPrincipal.load();
        Scene esceneNueva = btnRegresar.getScene();
    //Obtener contenedor y setear contenido
        ScrollPane contenedor = (ScrollPane) esceneNueva.lookup("#contenedorprincipal");
        contenedor.setContent(principal);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo para habilitar txt
    @FXML 
    private void editar(){
    //Botones
        btnGuardar.setDisable(false);
        btnEditar.setDisable(true);
    //Text
        txtNombre.setDisable(false);
        txtApellidoPaterno.setDisable(false);
        txtApellidoMaterno.setDisable(false);
    }


//Metodo de inicializacion
    @FXML 
    private void initialize() throws SQLException{

        btnGuardar.setDisable(true);
        txtNombre.setDisable(true);
        txtApellidoPaterno.setDisable(true);
        txtApellidoMaterno.setDisable(true);

        try{
            consultarDatos();
        }
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo realizar la consulta, intente de nuevo.");
        }

    }

}
