package com.example.controladoresRestaurante;
//Librerias de java fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
//Librerias de mysql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias, clases adicionales

import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRestaurante;

public class ControladorUbicacion {
//Objetos de java fxml
    @FXML 
    private TextField txtCoordenadaX;
    @FXML 
    private TextField txtCoordenadaY;
    @FXML 
    private Button btnEditar;
    @FXML 
    private Button btnGuardar;

//Metodo para guardar la ubicacion
    @FXML 
    private void guardar() throws SQLException{
    //validar txt en blanco
        if(txtCoordenadaX.getText().isBlank() || txtCoordenadaY.getText().isBlank()){
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Ubicacion no valida, hay algun campo en blanco, intente de nuevo.");
            return;
        }
    //Obtener datos para realizar la actualizacion
        int coordenadaX = Integer.valueOf(txtCoordenadaX.getText());
        int coordenadaY = Integer.valueOf(txtCoordenadaY.getText());
        int id = datosSesionRestaurante.getIdRestaurante();
    //Sentencia sql
        String sql = """
            UPDATE restaurante
            SET coordenada_x = ?, coordenada_y = ?
            WHERE id_restaurante = ?    
                """;
    //Iniciar conexion en null
        Connection conexion = null;
    //Iniciar try
        try{
            conexion = controladorConector.conectar();
            conexion.setAutoCommit(false);
            //TWR Prepared
            try(PreparedStatement guardar = conexion.prepareStatement(sql);){
                guardar.setInt(1, coordenadaX);
                guardar.setInt(2, coordenadaY);
                guardar.setInt(3, id);

                guardar.executeUpdate();
            }  //Termina TWR
        //Ejecutar commit si todo sale bien
            conexion.commit();

            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Ubicacion guardada correctamente.");
            txtCoordenadaX.setDisable(true);
            txtCoordenadaY.setDisable(true);
            btnGuardar.setDisable(true);
            btnEditar.setDisable(false);
        }
        catch(SQLException e){
            if(conexion != null){
                try{ 
                    conexion.rollback();
                }
                catch(SQLException v){
                    e.addSuppressed(v);
                }
            } //Termina if
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo realizar la actualizacion, intente de nuevo o contacte a soporte.");
        }//Cierra catch
        finally{
            if(conexion != null){
                conexion.close(); //Cerrar conexion 
            }
        }//Cierra finally
    }

//Metodo para consultar
    @FXML
    private void consultar() throws SQLException{
    //Variables donde se guardara el resultado
        Integer coordenadaX=null;
        Integer coordenadaY=null;
    //Cargar id del restaurante
        int id = datosSesionRestaurante.getIdRestaurante();
    //Setencia sql
        String sql = "SELECT coordenada_x,coordenada_y FROM restaurante WHERE id_restaurante = ?";
    //Empieza primer try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar  = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                try(ResultSet resultado = consultar.executeQuery();){
                    resultado.next();
                    coordenadaX = resultado.getInt("coordenada_x");
                    coordenadaY = resultado.getInt("coordenada_y");
                }//Cierra segundo try
        }//Cierra primer try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la ubicacion, cierre y abra ventana.");
        }

        if (coordenadaX==null && coordenadaY ==null){
            txtCoordenadaX.setText("Sin ubicacion");
            txtCoordenadaY.setText("Sin ubicacion");
        }else{
            txtCoordenadaX.setText(String.valueOf(coordenadaX));
            txtCoordenadaY.setText(String.valueOf(coordenadaY));
        }
    }

//Metodo para editar 
    @FXML
    private void editar(){
        txtCoordenadaX.setDisable(false);
        txtCoordenadaY.setDisable(false);
        btnGuardar.setDisable(false);
        btnEditar.setDisable(true);
    }
    
//Metodo de inicializacion 
    @FXML 
    private void initialize() throws SQLException{

        consultar();

        txtCoordenadaX.setDisable(true);
        txtCoordenadaY.setDisable(true);
        btnGuardar.setDisable(true);
    }
}
