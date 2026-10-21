package com.example.controladoresSimulacion;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias, clases adicionales
import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRestaurante;
//Librerias adicionales
import java.util.ArrayList;
import java.util.List;

public class ControladorAsignarRepartidor {

    private List<Repartidor> repartidores = new ArrayList<>(); 
   
//Metodo para encontrar las ubicacion de los repartidores
    private void consultarUbicacion() throws SQLException{
    //Sentencia sql
        String sql = """
            SELECT y_actual, x_actual, id_repartidor 
            FROM repartidor
            WHERE activo = ? AND disponibilidad = ?;
        """;
    //Inicia primer try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, 1);
                consultar.setInt(2,1);
            //Inicia segundo try ResultSet
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        repartidores.add( //Crear objetos en la lista 
                            new Repartidor(
                                resultado.getDouble("x_actual"),
                                resultado.getDouble("y_actual"),
                                resultado.getInt("id_repartidor")
                            )
                        );
                    }
                }//Termina primer try
        }//Termina segundo try
    }

//Metodo para asignar el repartidor cercano
    public void repartidorCercano()  throws SQLException{
    //Llamar metodo que consulta ubicaciones de repartidores
    try{
        consultarUbicacion();
    }
    catch(SQLException e){
        return;
    }
    //Inicializar variables utilizadas en la busqueda
        Repartidor repartidorCercano = null;
        Double distanciaMinima = Double.MAX_VALUE;
    //Se obtienen las coordenadas del negocio
        Double coordenadaXNegocio = datosSesionRestaurante.getCoordenadaX();
        Double coordenadaYNegocio = datosSesionRestaurante.getCoordenadaY();
    //Empieza for
        for(Repartidor r: repartidores){
            
            Double distancia = distanciaHaversine(
                coordenadaXNegocio,
                coordenadaYNegocio,
                r.getCoordenadaX(),
                r.getCoordenadaY()
            );

            if(distancia<distanciaMinima){
                distanciaMinima = distancia;
                repartidorCercano = r;
            }
        }//Termina for
        int idRepartidor = 0;
    //Asignar repartidor cercaco
        if(repartidorCercano!=null){           
            ControladorUbicaciones.setUbicacionRepartidor(
                repartidorCercano.getCoordenadaX(),
                repartidorCercano.getCoordenadaY()
            );
            idRepartidor = repartidorCercano.getId();
            disponibilidad(idRepartidor);
        }
        else{
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "No hay repartidores cercanos disponibles, continua la busqueda.");
            return;
        }
    //Termina asignacion
    } 

// Metodo para calcular distancia mediante Haversine
    public static Double distanciaHaversine(
        Double latitud1, Double longitud1,
        Double latitud2, Double longitud2) {

        Double radioTierra = 6371.0; // Kilometros

        Double lat1 = Math.toRadians(latitud1);
        Double lat2 = Math.toRadians(latitud2);

        Double diferenciaLatitud =
                Math.toRadians(latitud2 - latitud1);

        Double diferenciaLongitud =
                Math.toRadians(longitud2 - longitud1);

        Double a = Math.pow(Math.sin(diferenciaLatitud / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(diferenciaLongitud / 2), 2);

        Double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return radioTierra * c;
    }

//Metodo para cambiar disponibilidad del repartidor 
    private void disponibilidad(int id) throws SQLException{
    //Sentencia sql
        String sql = """
            UPDATE repartidor
            SET disponibilidad = ?
            WHERE id_repartidor = ?
        """;
    //Primer try connection y prepared
    Connection conexion = null;
        try{conexion = controladorConector.conectar();
            conexion.setAutoCommit(false);
        //Segundo try 
            try(PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1,0);
                consultar.setInt(2,id);
            //Ejecutar update
                consultar.executeUpdate();
            }//Cierra segundo try ResultSet
            conexion.commit();
        }//Cierra primer try 
        catch(SQLException e){
            if(conexion!=null){
                try{
                    conexion.rollback();
                }
                catch(SQLException v){
                    e.addSuppressed(v);
                }
            }
            throw e;
        }//Cierra catch
        finally{
            if(conexion!=null){
                conexion.close();
            }
        }//Cierra finally
    
    }

}
