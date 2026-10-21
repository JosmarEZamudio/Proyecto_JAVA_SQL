package com.example.controladoresSimulacion;
//Librerias para objetos fxml
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
//Librerias para simular movimiento
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.animation.Timeline;

public class ControladorSeguimiento {
//Objetos de fxml
    @FXML 
    private TextField txtPosicion;
    @FXML 
    private TextField txtMovimiento;
    @FXML 
    private TextField txtDestino;
    @FXML 
    private TextField txtEstado;
    @FXML 
    private TextField txtDistancia;
    @FXML 
    private TextField txtTiempo;
    @FXML 
    private TextField txtDireccionCliente;
    @FXML
    private TextField txtDireccionNegocio;

    private ControladorSimulacion simulacion;
    private Timeline actualizador;

//Metodo para obtener la simulacion actual
    public void setSimulacion(ControladorSimulacion simulacion) {
        this.simulacion = simulacion;
        iniciarActualizacion();
    }

//Metodo para iniciar la actualizacion 
    private void iniciarActualizacion(){
        actualizador = new Timeline(
            new KeyFrame(Duration.millis(100), e -> actualizarText())
        );

        actualizador.setCycleCount(Animation.INDEFINITE);
        actualizador.play();

        actualizarText();
        actualizador.play();
    }

//Metodo para actualizar txt
    @FXML
    private void actualizarText(){
    //Actualizar txt de origen
        txtPosicion.setText(String.format("%.5f , %.5f",
            simulacion.getXRepartidor(),simulacion.getYRepartidor()));
    //Actualizar txt de destino
        txtDestino.setText(String.format("%.5f , %.5f", 
            simulacion.getXDestino(),simulacion.getYDestino()));
    //Actualizar estado 
        txtEstado.setText(simulacion.getEstado());
    //Actualizar tiempo
        txtTiempo.setText(String.format("%.2f", 
            simulacion.getTiempoSimulacion()));
    //Actualizar distancia recorrida
        txtDistancia.setText(String.format("%.2f", 
            simulacion.getDistanciaRecorrida()));
            if(simulacion.getEstado() == "Entrega completada"){
                actualizador.stop();
            }
    }

//Detener actualizacion 
    public void detenerActualizacion() {
        if (actualizador != null) {
            actualizador.stop();
        }
    } //Termina clase

//Metodo de inicializacion 
    @FXML
    private void initialize(){
    //Deshabilitar text, solo se ocupan para muestra
        txtDestino.setDisable(true);
        txtDireccionCliente.setDisable(true);
        txtDireccionCliente.setText(ControladorUbicaciones.getDireccionCliente());
        txtDireccionNegocio.setDisable(true);
        txtDireccionNegocio.setText(ControladorUbicaciones.getDireccionNegocio());
        txtDistancia.setDisable(true);
        txtEstado.setDisable(true);
        txtMovimiento.setDisable(true);
        txtPosicion.setDisable(true);
        txtTiempo.setDisable(true);
    }


}
