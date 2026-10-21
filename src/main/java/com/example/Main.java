package com.example;
//Importar libreria application
import javafx.application.Application;
//Importar librerias para trabajar conventanas
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
//Importar libreria io y dialogos
import java.io.IOException;

public class Main extends Application{
    @Override 
        public void start(Stage stage) throws IOException{
        //RutasImagenes.inicializar();
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("interfazPrincipal.fxml")
                );

            Scene escenaNueva = new Scene(loader.load());

            stage.setTitle("Menu principal");
            stage.setScene(escenaNueva);
            stage.setResizable(false);
            stage.show();

        }catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente ejecutar de nuevo la aplicacion.");
            return;
        }

        }

    public static void main(String[] args){
        launch();
    }

    
}
