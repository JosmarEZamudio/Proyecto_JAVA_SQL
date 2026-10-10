package com.example.controladoresSimulacion;
//Librerias para objetos fxml
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//Librerias, clases adicionañes
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionCliente;
import com.example.controladorConector;

public class ControladorUbicacion {
//Objetos de java fxml
    @FXML 
    private Button btnGuardar;
    @FXML 
    private TextField txtCoordenadaX;
    @FXML
    private TextField txtCoordenadaY;

//Metodo para guardar ubicacion
    @FXML 
    private void guardarUbicacion() throws SQLException{ 
    //Validar txt vacios
        if(txtCoordenadaX.getText().isBlank() ||
            txtCoordenadaY.getText().isBlank()){
                dialogosMensajes.mensajeInformacion("Informacion ubicacion", 
                "Ubicacion invalida.");
                return;
            }
    //Obtener textos de txt
        int coordenadaX = Integer.valueOf(txtCoordenadaX.getText());
        int coordenadaY = Integer.valueOf(txtCoordenadaY.getText());
    //Pasar parametros de ubicacion a datosSesionCliente
        datosSesionCliente.ubicacion(coordenadaX, coordenadaY);
    //Obtener id del cliente
            int idCliente = datosSesionCliente.getIdCliente();
    //Preparar sentencia sql
            String sql = "UPDATE cliente SET coordenada_x = ?, coordenada_y = ? WHERE id_cliente = ?";
    //Abre primer try Connection
        try(Connection conexion = controladorConector.conectar();){
            conexion.setAutoCommit(false);
            //Segundo try prepared
            try{PreparedStatement agregar = conexion.prepareStatement(sql);

                agregar.setInt(1, coordenadaX);
                agregar.setInt(2, coordenadaY);
                agregar.setInt(3, idCliente);
                agregar.executeUpdate();
                conexion.commit();

                dialogosMensajes.mensajeInformacion("Informacion ubicacion", 
                "Ubicacion exitosa");

            }catch(SQLException e){
                if(conexion != null){
                    conexion.rollback();
                    throw e;
                }
            }
        }//Ciera primer try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo guardar la ubicacion, intente de nuevo.");
            return;
        }
    }


}
