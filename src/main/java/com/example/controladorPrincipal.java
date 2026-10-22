package com.example;
//Librerias para trabajar con objetos de fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
//Librerias para trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
//Librerias,clases adicionales
import java.io.IOException;
import java.sql.SQLException;

import com.example.controladorSesion.ControladorInicioSesion;
import com.example.controladorSesion.datosSesionCliente;
import com.example.controladorSesion.datosSesionRestaurante;
import com.example.controladoresRestaurante.ControladorMenuRestaurante;
import com.example.controladoresSimulacion.ControladorSeguimiento;
import com.example.controladoresSimulacion.ControladorSimulacion;

public class controladorPrincipal {
//Objeto dialogos
    dialogosMensajes dialogos = new dialogosMensajes();
//Objetos de clase
    private final ControladorSimulacion simulacion = new ControladorSimulacion();
    private ControladorSeguimiento seguimientoActual;
    private controladorPrincipal principal = this;
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

//Metodo para mostrar interfaz de registrar cliente
    @FXML
    private void registroUsuario() throws Exception{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderRegistroCliente = new FXMLLoader(
                getClass().getResource("/com/example/interfazsesion/interfazRegistroCliente.fxml")
            );
        //Cargar raiz pane 
            Pane registro = loaderRegistroCliente.load(); 
        //Setear contenedor
            contenedorPrincipal.setContent(registro); 
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para mostrar interfaz registro negocio  
    @FXML
    private void registroNegocio() throws Exception{
    //Cagar archivo fxml
        try{
            FXMLLoader loaderRegistroNegocio = new FXMLLoader(
                getClass().getResource("/com/example/interfazsesion/interfazRegistroRestaurante.fxml")
            );
        //Obtener raiz pane
            Pane registro = loaderRegistroNegocio.load();
        //Setear contenedor
            contenedorPrincipal.setContent(registro);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error",
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para mostrar interfaz de inicio sesion
    @FXML 
    private void iniciarSesion() throws Exception{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderSesion = new FXMLLoader(
                getClass().getResource("/com/example/interfazsesion/iniciarSesion.fxml")
            );
        //Cargar raiz pane
            Pane sesion = loaderSesion.load();
        //Obtener controlador del fxml
            ControladorInicioSesion controlador = loaderSesion.getController();
            controlador.setControladorPrincipal(principal);
        //Setear contenedor
            contenedorPrincipal.setContent(sesion); 
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error",
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para mostrar interfaz del menu restaurante
    @FXML 
    private void opcionesRestaurante() throws Exception{
    //Cagar archivo fxml
        try{
            FXMLLoader loaderRestaurante = new FXMLLoader(
                getClass().getResource("/com/example/interfazrestaurante/menuPrincipal.fxml")
            );
        //Obtener raiz pane
            Pane menu = loaderRestaurante.load();
        //Pasar controlador 
            ControladorMenuRestaurante menuRestaurante = loaderRestaurante.getController();
            menuRestaurante.setControladorPrincipal(principal);
        //Setear contenedor
            contenedorPrincipal.setContent(menu);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error",
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para ir a la interfaz simulacion
    @FXML 
    private void simulacion() throws IOException{
    //Si se sale de la interfaz, crear un nuevo objeto visual
        ControladorSeguimiento anterior = seguimientoActual;
        if (anterior != null) {
            anterior.detenerActualizacion();
        }
    //Cargar archivo fxml
        try{
            FXMLLoader loaderSimulacion = new FXMLLoader(
                getClass().getResource("/com/example/interfazsimulacion/interfaz.fxml")
            );
    //Obtener raiz y setear contenedor
            Pane interfaz = loaderSimulacion.load();
        //Cargar controlador de la interfaz y setear simulacion
            ControladorSeguimiento controlador = loaderSimulacion.getController();
            controlador.setSimulacion(simulacion);
        //Setear contenedor
            contenedorPrincipal.setContent(interfaz);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para ir a la interfaz de cargar mapa
    @FXML 
    private void cargarMapa() throws IOException{
    //Si se sale de la interfaz, crear un nuevo objeto visual
        ControladorSeguimiento anterior = seguimientoActual;
        if (anterior != null) {
            anterior.detenerActualizacion();
        }
    //Cargar archivo fxml
        try{
            FXMLLoader loaderSimulacion = new FXMLLoader(
                getClass().getResource("/com/example/pruebaUbicacion.fxml")
            );
        //Obtener raiz y setear contenedor
            Scene escenaNueva = new Scene(loaderSimulacion.load());
            Stage nuevaVentana = new Stage();
        //Dar propieades a la stage
            nuevaVentana.setTitle("Ubicacion");
            nuevaVentana.setScene(escenaNueva);
            nuevaVentana.initModality(Modality.APPLICATION_MODAL);
            nuevaVentana.showAndWait();

        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para salir
    @FXML 
    private void salir(){
    //Obtener ventana actual y cerrar
        Stage ventanaActual = (Stage) btnSalir.getScene().getWindow();
    //Guardar valor de retorno
        Boolean resultado = dialogosMensajes.mensajeConfirmacion( "Mensaje confirmacion", 
        "Guarde cambios para salir");
    //Validar valor de retorno
        if(resultado == true){
            //Obtener ventana actual y cerrar
                ventanaActual.close();
        }

    }

//Metodo para ver la interfaz del repartidor
    @FXML 
    private void interfazRepartidor() throws IOException{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderRepartidor = new FXMLLoader(
                getClass().getResource("/com/example/interfazrepartidor/interfazPrincipal.fxml")
            );
        //Obtener raiz pane
            Pane repartidor = loaderRepartidor.load();
        //Setear contenedor 
            contenedorPrincipal.setContent(repartidor);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Mensaje error", 
            "No se pudo cargar la interfaz, intente de nuevos.");
            return;
        }
    }

//*****Metodo iniciales********* 
//Metodo para mostrar la interfaz de saludo
    @FXML 
    private void saludoInicial() throws Exception{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderSaludo = new FXMLLoader(
                getClass().getResource("saludoInicial.fxml")
            );
    //Cargar raiz pane
            Pane saludo = loaderSaludo.load();
    //Setear contenedor
            contenedorPrincipal.setContent(saludo);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Mensaje error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para acceder a la simulacion 
    public void iniciarSimulacion() throws SQLException{
        try{
            simulacion.simulacionMovimiento();
        }
        catch(SQLException e){
            dialogosMensajes.mensajeError("Mensaje error", 
            "No se pudo cambiar de estado.");
        }
    }

//Metodo para asginar controlador 
    public void setControladorPrincipal(controladorPrincipal principal) {
         System.out.println("Setter del menú recibió: " + principal);
        this.principal = principal;
    }

//Metodos de validacion para no acceder a opciones no permitidas para cliente
    @FXML 
    public void mostrarOpcionesCliente(){
    //Si el cliente esta logeado
        if(datosSesionCliente.getIdCliente()!=null){
            btnCliente.setDisable(false);
            btnRegistroCliente.setDisable(true);
            btnRegistroNegocio.setDisable(true);
            btnSesion.setDisable(true);
        }
    }

//Metodo de validacion para no acceder a opciones no permitidas para negocio
    @FXML 
    public void mostrarOpcionesRestaurante(){
        if(datosSesionRestaurante.getIdRestaurante()!=null){
            btnRestaurante.setDisable(false);
            btnRegistroCliente.setDisable(true);
            btnRegistroNegocio.setDisable(true);
            btnSesion.setDisable(true);
        }
    }

//Metodo para ocular opciones disponibles del negocio o cliente despues de cerrar cesion
    @FXML 
    public void ocultarOpciones(){
    //Si la sesion esta activa con un cliente
        if(datosSesionCliente.getIdCliente()!=null){
            btnCliente.setDisable(true);
        }
    //Su la sesion esta activa con un negocio
        if(datosSesionRestaurante.getIdRestaurante()!=null){
            btnRestaurante.setDisable(true);
        }
        btnSesion.setDisable(false);
        btnRegistroCliente.setDisable(false);
        btnRegistroNegocio.setDisable(false);
    }

//Metodo de inicializacion
    @FXML 
    public void initialize() throws Exception{ 
        saludoInicial();
        btnCliente.setDisable(true);
        btnRestaurante.setDisable(true);
        btnRepartidor.setDisable(true);
        btnSimulacion.setDisable(true);        
    }
    
}