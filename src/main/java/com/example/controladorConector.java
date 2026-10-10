package com.example;

//Importar librerias de mysql
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class controladorConector {

    public static Connection conectar() throws SQLException{
            String url = "jdbc:mysql://localhost:3306/proyectoCopia";
            String usuario = "root";
            String password = "7004";
            return DriverManager.getConnection(url, usuario, password);
    }
    
}
