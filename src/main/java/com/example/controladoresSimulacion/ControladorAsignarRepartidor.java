package com.example.controladoresSimulacion;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias, clases adicionales
import com.example.controladorConector;

public class ControladorAsignarRepartidor {
    public int buscarRepartidorCercano(
        double xRestaurante,
        double yRestaurante) throws SQLException {

    String sql = """
        SELECT id_repartidor, coordenada_x, coordenada_y
        FROM repartidor
        WHERE activo = 1 AND disponibilidad = 1
        """;

    int idSeleccionado = -1;
    double distanciaMinima = Double.MAX_VALUE;

    try (Connection conexion = controladorConector.conectar();
         PreparedStatement consulta = conexion.prepareStatement(sql);
         ResultSet resultado = consulta.executeQuery()) {

        while (resultado.next()) {

            int id = resultado.getInt("id_repartidor");

            double x = resultado.getDouble("coordenada_x");
            double y = resultado.getDouble("coordenada_y");

            double dx = xRestaurante - x;
            double dy = yRestaurante - y;

            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                idSeleccionado = id;
            }
        }//Cierra try resultset
    }//Cierra segundo try

    return idSeleccionado;
}

//Metodo para ocupar repartidor
    public boolean ocuparRepartidor(int idRepartidor)
            throws SQLException {

        String sql = """
            UPDATE repartidor
            SET disponibilidad = 0
            WHERE id_repartidor = ?
            AND activo = 1
            AND disponibilidad = 1
            """;

        try (Connection conexion = controladorConector.conectar();
            PreparedStatement actualizar =
                    conexion.prepareStatement(sql)) {

            actualizar.setInt(1, idRepartidor);

            return actualizar.executeUpdate() == 1;
        }
    }

    
}
