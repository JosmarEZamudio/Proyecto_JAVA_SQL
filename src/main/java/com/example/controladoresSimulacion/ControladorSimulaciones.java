package com.example.controladoresSimulacion;
//Librerias sql
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ControladorSimulaciones {

    // Cada pedido tiene su propia simulacion
    private final Map<Integer, SimulacionPedido> simulaciones =
            new HashMap<>();

    // Registrar e iniciar una simulacion
    public void iniciarSimulacion(SimulacionPedido nueva)
            throws SQLException {

        int idPedido = nueva.getIdPedido();

        // Evitar duplicar una simulacion activa
        SimulacionPedido existente = simulaciones.get(idPedido);

        if (existente != null) {
            if (!existente.getEstado().equals("Entrega completada")) {
                return;
            }
        }

        simulaciones.put(idPedido, nueva);
        nueva.iniciar();
    }

    // Obtener una simulacion especifica
    public SimulacionPedido obtenerSimulacion(int idPedido) {
        return simulaciones.get(idPedido);
    }

    // Consultar si un pedido ya tiene simulacion registrada
    public boolean existeSimulacion(int idPedido) {
        return simulaciones.containsKey(idPedido);
    }

    // Detener una simulacion especifica
    public void detenerSimulacion(int idPedido) {
        SimulacionPedido simulacion = simulaciones.get(idPedido);

        if (simulacion != null) {
            simulacion.detener();
        }
    }

    // Obtener todas las simulaciones registradas
    public Map<Integer, SimulacionPedido> getSimulaciones() {
        return simulaciones;
    }
}
