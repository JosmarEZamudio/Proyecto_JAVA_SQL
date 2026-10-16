package com.example.controladoresSimulacion;
//Librerias para fxml
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
//Librerias para la simulacion de movimiento
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
//Librerias, clases adicionales
import com.example.controladorSesion.datosSesionRepartidor;

public class controladorSimulacion {
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
//Variables para todos los metodos
    //Coordenadas del repartidor
        private double coordenadaXRepartidor;
        private double coordenadaYRepartidor;
    //Coordenadas del restaurante
        double coordenadaXDestino = 23;
        double coordenadaYDestino = 21;
    //Distancia recorrida y velocidad
        double distanciaRecorrida = 0;
        double velocidad = 7;
    //Tiempos y repeticiones
        Timeline simulacion;
        private long tiempoInicio;
        double tiempoTranscurrido;

//Metodo para simular movimiento del repartidor
    @FXML 
    private void simulacionMovimiento(){

        tiempoInicio = System.nanoTime();

        coordenadaXRepartidor = datosSesionRepartidor.getCoordenadaX();
        coordenadaYRepartidor = datosSesionRepartidor.getCoordenadaY();

        txtPosicion.setText(String.valueOf(coordenadaXRepartidor+" , "+coordenadaYRepartidor));

        simulacion = new Timeline(
            new KeyFrame(Duration.millis(100), e->{
                movimiento();
                imprimir();
            })
        );

        simulacion.setCycleCount(Animation.INDEFINITE);
        simulacion.play();
    }

//Metodo para realizar el movimiento 
    @FXML 
    private void movimiento(){
        double dx = coordenadaXDestino - coordenadaXRepartidor ;
        double dy = coordenadaYDestino - coordenadaYRepartidor;

        double distancia = Math.sqrt(dx*dx+dy*dy);
        double avance = velocidad * 0.1;

        if (distancia <= avance || distancia == 0){
            distanciaRecorrida += distancia;

            coordenadaXRepartidor = coordenadaXDestino;
            coordenadaYRepartidor = coordenadaYDestino;

            datosSesionRepartidor.setUbicacion((int)coordenadaXRepartidor,(int)coordenadaYRepartidor);
            
            simulacion.stop();

        }else{
            double movimientoX = (dx/distancia)*avance;
            double movimientoY = (dy/distancia)*avance;

            coordenadaXRepartidor += movimientoX;
            coordenadaYRepartidor += movimientoY;

            distanciaRecorrida += avance;
        }
    }

//Metodo para imprimir el movimiento 
    @FXML
    private void imprimir(){
        txtMovimiento.setText(String.format("%.2f , %.2f",coordenadaXRepartidor,coordenadaYRepartidor));
        txtDestino.setText(String.format("%.2f , %.2f",coordenadaXDestino,coordenadaYDestino));
        txtDistancia.setText(String.format("%.2f",distanciaRecorrida));

        tiempoTranscurrido = (System.nanoTime()-tiempoInicio) /1_000_000_000.0 ;

        txtTiempo.setText(String.format("%.2f segundos", tiempoTranscurrido));
    }
   
//Metodo para inhabilitar txt
    @FXML 
    private void inhabilitar(){
        txtPosicion.setDisable(true);
        txtDestino.setDisable(true);
        txtEstado.setDisable(true);
        txtDistancia.setDisable(true);
        txtTiempo.setDisable(true);
        txtMovimiento.setDisable(true);
    }

//Metodo de inicializacion
    @FXML 
    private void initialize(){
        inhabilitar();
        simulacionMovimiento();
    }

}
