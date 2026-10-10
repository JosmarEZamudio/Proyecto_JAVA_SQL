package com.example.controladoresRestaurante;
//Librerias fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
//Librerias para trabajar con ventana
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//Librerias adicionales
import java.io.IOException;
import com.example.dialogosMensajes;

public class ControladorListaVacia {

    @FXML 
    private Button btnAgregar;

    @FXML 
    private void interfazAgregar() throws IOException{
        try{
            FXMLLoader loaderAgregar = new FXMLLoader(
                getClass().getResource("/com/example/interfazrestaurante/agregarProducto.fxml")
            );

            Pane agregar = loaderAgregar.load();
            Scene escenaActual = btnAgregar.getScene();

            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(agregar);
        }catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }
    
}


