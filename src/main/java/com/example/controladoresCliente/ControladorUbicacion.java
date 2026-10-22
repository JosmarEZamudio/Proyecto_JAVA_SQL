package com.example.controladoresCliente;
//Librerias fxml
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
//Librerias java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//Librerias, clases adicionales
import com.example.controladorConectorPool;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionCliente;

public class ControladorUbicacion {
    @FXML 
    private TextField txtCoordenadaX;
    @FXML 
    private TextField txtCoordenadaY;
    @FXML 
    private Button btnGuardar;

//Metodo para guardar la ubicacion
    @FXML
    private void guardarUbicacion() throws SQLException{
    //Validar txt en blanco
        if(txtCoordenadaX.getText().isBlank() || txtCoordenadaY.getText().isBlank()){
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Ubicacion invalida!! Campos de texto en blanco.");
            return;
        }
    //Obtener id del cliente
        int idCliente = datosSesionCliente.getIdCliente();
    //Obtener coordenadas de los txt
        int coordenadaX = Integer.valueOf(txtCoordenadaX.getText());
        int coordenadaY = Integer.valueOf(txtCoordenadaY.getText());

        String sql = """
            UPDATE cliente
            SET coordenada_x = ?, coordenada_y = ?
            WHERE id_cliente = ?
                """;
    //Declarar conexion como nula
        Connection conexion = null;
    //Abre try
        try{
            conexion = controladorConectorPool.conectar();
            conexion.setAutoCommit(false);
            try(PreparedStatement actualizar = conexion.prepareStatement(sql);){
                actualizar.setInt(1, coordenadaX);
                actualizar.setInt(2,coordenadaY);
                actualizar.setInt(3,idCliente);
            
                actualizar.executeUpdate();
            }//Cierra TWR
            conexion.commit();
            //datosSesionCliente.guardarUbicacion((double)coordenadaX, (double)coordenadaY);
        //Mostrar mensaje y bloquear ciertos controladores
            dialogosMensajes.mensajeInformacion("Informacion", 
            "Ubicacion guardada con exito.");
            txtCoordenadaX.setDisable(true);
            txtCoordenadaY.setDisable(true);
            btnGuardar.setDisable(true);
        }//Cierra try
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
            "No se pudo guardar la ubicacion, intente nuevamente.");
        }//Termina catch
        finally{
            conexion.close();
        }//Termina finally

    }

//Metodo de inicializacion
    @FXML 
    private void initialize(){
    //Poner ubicacion su ya existe una
        if(datosSesionCliente.getCoordenadaX()!= null && datosSesionCliente.getCoordenadaY()!=null){
            txtCoordenadaX.setText(String.valueOf(datosSesionCliente.getCoordenadaX()));
            txtCoordenadaY.setText(String.valueOf(datosSesionCliente.getCoordenadaY()));
        }
    }


}
