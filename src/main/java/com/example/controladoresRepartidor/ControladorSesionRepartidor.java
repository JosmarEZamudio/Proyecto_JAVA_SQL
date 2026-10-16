package com.example.controladoresRepartidor;
//Librerias para trabajar con objetos fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias para trabajar conventanas
import javafx.stage.Stage;
//Librerias,clases adicionales
import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRepartidor;


public class ControladorSesionRepartidor {
    @FXML 
    private Button btnIniciar;
    @FXML 
    private Button btnCancelar;
    @FXML 
    private TextField txtCorreo;
    @FXML 
    private TextField txtPassword;

//Metodo para iniciar sesion
    @FXML 
    private void iniciarSesion() throws SQLException{
        if(txtCorreo.getText().isBlank() || txtPassword.getText().isBlank()){
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Todos los datos son requeridos!!");
            return;
        }

        Stage ventanaActual = (Stage) btnIniciar.getScene().getWindow();

        String correo = txtCorreo.getText();
        String password = txtPassword.getText();
        int id = 0;
        String nombre;

        String sql = "SELECT id_repartidor,nombre FROM repartidor WHERE correo = ? AND contrasena = ?";
    //Inicia try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql)){
                consultar.setString(1, correo);
                consultar.setString(2, password);
            //Inicia try Result set
                try(ResultSet resultado = consultar.executeQuery()){
                    if(resultado.next()){
                    //Obtener datos de la consulta
                        id = resultado.getInt("id_repartidor");
                        nombre = resultado.getString("nombre");
                    //Guardar informacion relevante en metodos estaticos y mostrar mensaje
                        dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                        "Usuario encontrado, bienvenido.");
                        datosSesionRepartidor.setRepartidor(id,nombre);
                        ventanaActual.close();
                        return;
                    }else{
                        dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                        "Usuario no encontrado, intente de nuevo.");
                        return;
                    }
                }//Cierra segundo try
        }//Ciera primer try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se puede iniciar sesion, intente de nuevo.");
        }

    }

//Metodo para regresar a la ventana principal
    @FXML 
    private void cancelar(){
        Stage ventanaActual = (Stage) btnCancelar.getScene().getWindow();
        ventanaActual.close();
    }

}
