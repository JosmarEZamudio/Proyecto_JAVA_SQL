package com.example;
//Librerias para trabajar con objetos de fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
//Librerias para trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
//Librerias adicionales
import java.io.IOException;

public class controladorPrincipal {
//Objeto dialogos
    dialogosMensajes dialogos = new dialogosMensajes();
//Botones
    @FXML 
    private Button btnSesion;
    @FXML 
    private Button btnRestaurante;
    @FXML 
    private Button btnCliente;
    @FXML 
    private Button btnRepartidor;
    @FXML 
    private Button btnSimulacion;
    @FXML 
    private Button btnSalir;
    @FXML 
    private Button btnRegistroCliente;
    @FXML 
    private Button btnRegistroNegocio;
//Contenedores
    @FXML 
    private  Pane contenedorSaludo;
    @FXML 
    private ScrollPane contenedorPrincipal;

//Metodo restaurante
    @FXML
    private void registroUsuario() throws Exception{
        FXMLLoader loaderRegistroCliente = new FXMLLoader(
            getClass().getResource("/com/example/interfazsesion/interfazRegistroCliente.fxml")
        );
        //El.fxml tiene de raiz un contenedor Pane, por lo tanto: 
        Pane registro = loaderRegistroCliente.load(); //Se puede obtener directamente su Pane
        contenedorPrincipal.setContent(registro); //Y despues ajustar el contenido a ScrollPane
    }
//Metodo para registrar negocio  
    @FXML
    private void registroNegocio() throws Exception{
        FXMLLoader loaderRegistroNegocio = new FXMLLoader(
            getClass().getResource("/com/example/interfazsesion/interfazRegistroRestaurante.fxml")
        );
        Pane registro = loaderRegistroNegocio.load();
        contenedorPrincipal.setContent(registro);
    }
//Metodo para inciar sesion
    @FXML 
    private void iniciarSesion() throws Exception{
        FXMLLoader loaderSesion = new FXMLLoader(
            getClass().getResource("/com/example/interfazsesion/iniciarSesion.fxml")
        );
    Pane sesion = loaderSesion.load();
    contenedorPrincipal.setContent(sesion); 
    }

//Metodo para ver las opciones de restaurante 
    @FXML 
    private void opcionesRestaurante() throws Exception{
        FXMLLoader loaderRestaurante = new FXMLLoader(
            getClass().getResource("/com/example/interfazrestaurante/menuPrincipal.fxml")
        );
    
    Pane menu = loaderRestaurante.load();
    contenedorPrincipal.setContent(menu);
    }

//Metodo para ver la simulacion
    @FXML 
    private void simulacion() throws IOException{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderSimulacion = new FXMLLoader(
                getClass().getResource("/com/example/interfazsimulacion/interfaz.fxml")
            );
    //Obtener raiz y setear contenedor
            Pane simulacion = loaderSimulacion.load();
            contenedorPrincipal.setContent(simulacion);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz.");
        }
    }


//Metodo para salir
    @FXML 
    private void salir(){
        Stage ventanaActual = (Stage) btnSalir.getScene().getWindow();
        ventanaActual.close();
    }
    
    public Pane contenedorPane(){
        return contenedorSaludo;

    }

//Metodo para ver la interfaz del repartidor
    @FXML 
    private void interfazRepartidor() throws IOException{
        FXMLLoader loaderRepartidor = new FXMLLoader(
            getClass().getResource("/com/example/interfazrepartidor/interfazPrincipal.fxml")
        );

        Pane repartidor = loaderRepartidor.load();
        contenedorPrincipal.setContent(repartidor);
    }

//*****Metodo iniciales********* 
    @FXML 
    private void saludoInicial() throws Exception{
        FXMLLoader loaderSaludo = new FXMLLoader(
            getClass().getResource("saludoInicial.fxml")
        );
    Pane saludo = loaderSaludo.load();
    contenedorPrincipal.setContent(saludo);
    }
//Metodo para incicializar cosas empezando la aplicacion
    @FXML 
    public void initialize() throws Exception{ 
        saludoInicial();
        btnCliente.setDisable(false);
        btnRestaurante.setDisable(false);
        btnRepartidor.setDisable(false);
        btnSimulacion.setDisable(false);        
    }
    
}