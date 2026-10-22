package com.example.controladoresSimulacion;
//Librerias para simulaciones
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
//Librerias para sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
//Librerias, clases adicionales
import java.util.Random;
import com.example.controladorConector;
import com.example.dialogosMensajes;

public class SimulacionPedido {

// Identificador propio de la entrega
    private final int idPedido;

// Coordenadas del repartidor
    private double coordenadaXRepartidor;
    private double coordenadaYRepartidor;

// Coordenadas del negocio
    private final double coordenadaXNegocio;
    private final double coordenadaYNegocio;

// Coordenadas del cliente
    private final double coordenadaXCliente;
    private final double coordenadaYCliente;

// Coordenadas del destino
    private double coordenadaXDestino;
    private double coordenadaYDestino;

// Distancia y velocidad
    private double distanciaRecorrida = 0;
    private Double velocidad =
            ThreadLocalRandom.current().nextDouble(40, 81);

// Estado y tiempo
    private String estado = "Inactivo";
    private Timeline simulacion;
    private long tiempoInicio;
    private long tiempoFin;

// Esperas aleatorias
    private boolean esperando = false;
    private long tiempoFinal;
    private final Random random = new Random();

//Metodo constructor que recibe los datos especificos de cada pedido
    public SimulacionPedido(
            int idPedido,
            double longitudRepartidor,
            double latitudRepartidor,
            double longitudNegocio,
            double latitudNegocio,
            double longitudCliente,
            double latitudCliente) {

        this.idPedido = idPedido;

        this.coordenadaXRepartidor = longitudRepartidor;
        this.coordenadaYRepartidor = latitudRepartidor;

        this.coordenadaXNegocio = longitudNegocio;
        this.coordenadaYNegocio = latitudNegocio;

        this.coordenadaXCliente = longitudCliente;
        this.coordenadaYCliente = latitudCliente;

        this.coordenadaXDestino = longitudNegocio;
        this.coordenadaYDestino = latitudNegocio;
    }

     // Iniciar simulacion
    public void iniciar() throws SQLException {

        if (simulacion != null) {
            simulacion.stop();
        }

        tiempoInicio = System.nanoTime();
        tiempoFin = 0;

        distanciaRecorrida = 0;
        esperando = false;
        estado = "En camino al negocio";

        cambiarEstado(2);

        simulacion = new Timeline(
            new KeyFrame(Duration.millis(100), e -> {
                try {
                    movimiento();
                } catch (SQLException ex) {
                    detener();
                    dialogosMensajes.mensajeError(
                        "Error de simulacion",
                        "No se pudieron actualizar los datos del pedido "
                        + idPedido + "."
                    );
                }
            })
        );

        simulacion.setCycleCount(Animation.INDEFINITE);
        simulacion.play();
    }

    // Movimiento del repartidor
    private void movimiento() throws SQLException {

        // Controlar tiempo de espera
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

        double distancia =
            ControladorAsignarRepartidor.distanciaHaversine(
                latitudActual, longitudActual,
                latitudDestino, longitudDestino
            );

        // Avance en kilometros cada 100 ms
        double avance = velocidad * (0.1 / 3600.0);

        if (distancia <= avance || distancia == 0) {

            distanciaRecorrida += distancia;

            coordenadaXRepartidor = coordenadaXDestino;
            coordenadaYRepartidor = coordenadaYDestino;

            if (estado.equals("En camino al negocio")) {

                estado = "Esperando en el negocio";
                cambiarEstado(3);
                iniciarEspera();

            } else if (estado.equals("En camino al cliente")) {

                estado = "Entregando al cliente";
                iniciarEspera();

            } else if (estado.equals("Esperando en el negocio")) {

                coordenadaXDestino = coordenadaXCliente;
                coordenadaYDestino = coordenadaYCliente;

                estado = "En camino al cliente";
                cambiarEstado(4);

            } else if (estado.equals("Entregando al cliente")) {

                cambiarEstado(5);
                estado = "Entrega completada";
                detener();
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

            double distanciaAngular = avance / 6371.0;

            double nuevaLatitud = Math.asin(
                Math.sin(lat1) * Math.cos(distanciaAngular)
                + Math.cos(lat1) * Math.sin(distanciaAngular)
                * Math.cos(rumbo)
            );

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

    // Espera aleatoria entre 30 y 45 segundos
    private void iniciarEspera() {
        esperando = true;

        int segundos = random.nextInt(16) + 30;

        tiempoFinal = System.nanoTime()
                + segundos * 1_000_000_000L;
    }

    // Actualizar estado del pedido en MySQL
    private void cambiarEstado(int nuevoEstado)
            throws SQLException {

        String sql = """
            UPDATE pedido
            SET estado = ?
            WHERE id_pedido = ?
        """;

        try (Connection conexion = controladorConector.conectar();
             PreparedStatement actualizar =
                     conexion.prepareStatement(sql)) {

            actualizar.setInt(1, nuevoEstado);
            actualizar.setInt(2, idPedido);

            actualizar.executeUpdate();
        }
    }

    // Detener exclusivamente esta simulacion
    public void detener() {
        if (simulacion != null) {
            simulacion.stop();
        }

        if (tiempoInicio != 0 && tiempoFin == 0) {
            tiempoFin = System.nanoTime();
        }
    }

    // Getters
    public int getIdPedido() {
        return idPedido;
    }

    public Double getVelocidad() {
        return velocidad;
    }

    public double getXRepartidor() {
        return coordenadaXRepartidor;
    }

    public double getYRepartidor() {
        return coordenadaYRepartidor;
    }

    public double getXDestino() {
        return coordenadaXDestino;
    }

    public double getYDestino() {
        return coordenadaYDestino;
    }

    public double getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public double getTiempoSimulacion() {
        if (tiempoInicio == 0) {
            return 0;
        }

        long fin = tiempoFin == 0
                ? System.nanoTime()
                : tiempoFin;

        return (fin - tiempoInicio) / 1_000_000_000.0;
    }

    public String getEstado() {
        return estado;
    }

    public boolean isEsperando() {
        return esperando;
    }
}


