package com.example;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class dialogosMensajes {
//Metodo para mensaje de informacion    
    @FXML 
    static public void mensajeInformacion(String mensaje1, String mensaje2){
        Alert informacion = new Alert(AlertType.INFORMATION);
            informacion.setTitle(mensaje1);
            informacion.setHeaderText(null);
            informacion.setContentText(mensaje2);
            informacion.show();
    }

//Metodo para mensaje de error
    @FXML 
    static public void mensajeError(String titulo, String mensaje){
        Alert informacion = new Alert(AlertType.ERROR);
            informacion.setTitle(titulo);
            informacion.setHeaderText(null);
            informacion.setContentText(mensaje);
            informacion.showAndWait();
    }

//Metodo mensaje de confirmacion
    @FXML 
    static public boolean mensajeConfirmacion(String titulo, String mensaje){
        Alert informacion = new Alert(AlertType.CONFIRMATION);
        informacion.setTitle(titulo);
        informacion.setHeaderText(null);
        informacion.setContentText(mensaje);
        Optional<ButtonType> resultado = informacion.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
