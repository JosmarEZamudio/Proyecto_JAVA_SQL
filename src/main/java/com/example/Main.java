package com.example;
//Importar libreria application
import javafx.application.Application;
//Importar librerias para trabajar conventanas
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
//Importar libreria io y dialogos
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

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

        }
        catch(IOException e){
            try {
                Files.writeString(
                    Path.of(System.getProperty("user.home"), "error-app.txt"),
                    java.io.StringWriter.class.getName() + "\n"
                        + obtenerError(e),
                    StandardCharsets.UTF_8
                );
            } catch (IOException errorArchivo) {
                errorArchivo.printStackTrace();
            }

            e.printStackTrace();
            return;
        }

        }

//Cambiar metodo de cerrar el programa
    @Override
    public void stop() {
        controladorConectorPool.cerrarPool();
    }

    private String obtenerError(Exception e) {
        java.io.StringWriter texto = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(texto));
        return texto.toString();
    }

  
    public static void main(String[] args){
        launch();
    }

    
}
