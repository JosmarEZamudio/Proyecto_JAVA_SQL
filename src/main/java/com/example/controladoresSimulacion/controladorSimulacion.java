package com.example.controladoresSimulacion;
//Librerias para la simulacion de movimiento
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
//Librerias, clases adicionales
import java.util.Random;
import com.example.controladorConectorPool;
import com.example.dialogosMensajes;

//Librerias sql 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ControladorSimulacion {

    //Coordenadas del repartidor
        private Double coordenadaXRepartidor;
        private Double coordenadaYRepartidor;
    //Coordenadas del restaurante
        private Double coordenadaXNegocio;
        private Double coordenadaYNegocio;
    //Coordenadas del cliente
        private Double coordenadaXCliente;
        private Double coordenadaYCliente;
    //Coordenadas a las que debe ir
        private Double coordenadaXDestino;
        private Double coordenadaYDestino;
    //Distancia recorrida y velocidad
        private Double distanciaRecorrida;
        private Double velocidad = (double) 600;
    //Tiempos y repeticiones
        private String estado = "Inactivo";
        private Timeline simulacion;
        private long tiempoInicio;
    //Variables de simulacion de espera
        private boolean esperando = false;
        private long tiempoFinal;
        private final Random random = new Random();

//Metodo para simular movimiento del repartidor
    public void simulacionMovimiento() throws SQLException{
    //Evita duplicar simulaciones
        if(simulacion != null){
            simulacion.stop();
        }
    //Iniciar tiempo
        this.tiempoInicio = System.nanoTime();
    //Ubicacion del repartidor
        this.coordenadaXRepartidor = ControladorUbicaciones.getCoordenadaYRepartidor();
        this.coordenadaYRepartidor = ControladorUbicaciones.getCoordenadaXRepartidor();
    //Ubicacion del negocio
        this.coordenadaXNegocio = ControladorUbicaciones.getCoordenadaXNegocio();
        this.coordenadaYNegocio = ControladorUbicaciones.getCoordenadaYNegocio();
    //Ubicacion del clietne
        this.coordenadaXCliente = ControladorUbicaciones.getCoordenadaXCliente();
        this.coordenadaYCliente = ControladorUbicaciones.getCoordenadaYCliente();
    //Ubicacion del destino al cual debe dirigirse, puede ser aun destino (negocio o cliente)
        this.coordenadaXDestino = coordenadaXNegocio;
        this.coordenadaYDestino = coordenadaYNegocio;
    //Asignar estado del pedido y valor a la distancia recorrida
        this.distanciaRecorrida = (double) 0;
        this.estado = "En camino al negocio";
    //Cambiar estado
            cambiarEstado(2);
    //Inicia simulacion
        simulacion = new Timeline(
            new KeyFrame(Duration.millis(100), e->{
                try{
                    movimiento();         
                }
                catch(SQLException v){
                    dialogosMensajes.mensajeError("Mensaje informacion", 
                    "No se pudo realizar los cambios de estado.");
                    simulacion.stop();
                    return;
                }
            })
        );

        simulacion.setCycleCount(Animation.INDEFINITE);
        simulacion.play();

    }

 // Metodo para calcular el movimiento del repartidor
 // Coordenadas X = longitud, Y = latitud
    private void movimiento() throws SQLException{

        if (esperando) {
            if (System.nanoTime() < tiempoFinal) {
                return;
            }
            esperando = false;
        }

        double latitudActual = coordenadaXRepartidor;
        double longitudActual = coordenadaYRepartidor;

        double latitudDestino = coordenadaXDestino;
        double longitudDestino = coordenadaYDestino;

        // Distancia real hasta el destino, en kilometros
        double distancia = ControladorAsignarRepartidor.distanciaHaversine(
                latitudActual, longitudActual,
                latitudDestino, longitudDestino
        );

        // Avance en kilometros por cada 100 milisegundos
        double avance = velocidad * (0.1 / 3600.0);

        if (distancia <= avance || distancia == 0) {

            distanciaRecorrida += distancia;

            coordenadaXRepartidor = coordenadaXDestino;
            coordenadaYRepartidor = coordenadaYDestino;

            if (estado.equals("En camino al negocio")) {

                estado = "Esperando en el negocio";
                try{
                    cambiarEstado(3);
                }
                catch(SQLException e){
                    throw e;
                }
                iniciarEspera();
        
            } else if (estado.equals("En camino al cliente")) {

                estado = "Entregando al cliente";
                iniciarEspera();

            } else if (estado.equals("Esperando en el negocio")) {
                velocidad = 30.0;
                coordenadaXDestino = coordenadaXCliente;
                coordenadaYDestino = coordenadaYCliente;

                estado = "En camino al cliente";
                try{
                    cambiarEstado(4);
                }
                catch(SQLException e){
                    throw e;
                }

            } else if (estado.equals("Entregando al cliente")) {
                try{
                    cambiarEstado(5);
                    cambiarDisponibilidad();
                }
                catch(SQLException e){
                    throw e;
                }
                estado = "Entrega completada";
                simulacion.stop();
                
            }

        } else {
            // Calcular direccion hacia el destino
            double lat1 = Math.toRadians(latitudActual);
            double lat2 = Math.toRadians(latitudDestino);
            double lon1 = Math.toRadians(longitudActual);
            double lon2 = Math.toRadians(longitudDestino);

            double diferenciaLon = lon2 - lon1;

            double y = Math.sin(diferenciaLon) * Math.cos(lat2);

            double x = Math.cos(lat1) * Math.sin(lat2)
                    - Math.sin(lat1) * Math.cos(lat2)
                    * Math.cos(diferenciaLon);

            double rumbo = Math.atan2(y, x);

            // Convertir el avance a distancia angular
            double distanciaAngular = avance / 6371.0;

            // Calcular nueva latitud
            double nuevaLatitud = Math.asin(
                    Math.sin(lat1) * Math.cos(distanciaAngular)
                    + Math.cos(lat1) * Math.sin(distanciaAngular)
                    * Math.cos(rumbo)
            );

            // Calcular nueva longitud
            double nuevaLongitud = lon1 + Math.atan2(
                    Math.sin(rumbo) * Math.sin(distanciaAngular)
                    * Math.cos(lat1),
                    Math.cos(distanciaAngular)
                    - Math.sin(lat1) * Math.sin(nuevaLatitud)
            );

            coordenadaXRepartidor = Math.toDegrees(nuevaLatitud);
            coordenadaYRepartidor = Math.toDegrees(nuevaLongitud);

            distanciaRecorrida += avance;
        }
    }

//Metodo de tiempo de espera aleatorio
    private void iniciarEspera() {
        esperando = true;
    // Entre 30 y 45 segundos, ambos incluidos
        int segundos = random.nextInt(16) + 30;
        tiempoFinal = System.nanoTime()
                + segundos * 1_000_000_000L;
    }

//Metodos sql para cada cada estado de entrega
    private void cambiarEstado(int i) throws SQLException{
    //Setencia sql
        String sql = """
            UPDATE pedido 
            SET estado = ?
            WHERE id_pedido = ?
        """;
    //Try prepared y Connection
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement actualizar = conexion.prepareStatement(sql);){
                actualizar.setInt(1, i);
                actualizar.setInt(2, ControladorUbicaciones.getIdPedido());

                actualizar.executeUpdate();
        }
        catch(SQLException e){
            //dialogosMensajes.mensajeError("Mensaje error", 
            //"No se pudieron realizar los cambios de estado.");
            throw e;
        }
    }

//Cambiar disponibilidad 
    private void cambiarDisponibilidad() throws SQLException{
        String sql= """
            UPDATE repartidor 
            SET disponibilidad = ?
            WHERE id_repartidor = ?
        """;
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement actualizar = conexion.prepareStatement(sql);){
                actualizar.setInt(1, 1);
                actualizar.setInt(2, ControladorAsignarRepartidor.idRepartidor);

                actualizar.executeUpdate();
        }
    }

//Metodos de retorno
    public double getXRepartidor(){
        return coordenadaXRepartidor;
    }

    public double getYRepartidor(){
        return coordenadaYRepartidor;
    }
      
    public double getXDestino(){
        return coordenadaXDestino;
    }

    public double getYDestino(){
        return coordenadaYDestino;
    }

    public double getDistanciaRecorrida(){
        return distanciaRecorrida;
    }
   
    public double getTiempoSimulacion(){
        return (System.nanoTime()-tiempoInicio)/1_000_000_000.00;
    }

    public String getEstado(){
        return estado;
    }
}

