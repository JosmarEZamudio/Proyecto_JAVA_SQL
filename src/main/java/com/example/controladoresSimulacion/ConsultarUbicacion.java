/*package com.example.controladoresSimulacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.controladorConector;

public class ConsultarUbicacion {  

//Metodo para consultar X y Y
    private void consultarUbicacion() throws SQLException{
        int coordenadaX;
        int coordenadaY;

        String sql = "SELECT coordenada_x,coordenada_y FROM cliente ";

        Connection conexion = controladorConector.conectar();
            conexion.setAutoCommit(false);
            PreparedStatement consultar = conexion.prepareStatement(sql);

                



    }
    
}
*/