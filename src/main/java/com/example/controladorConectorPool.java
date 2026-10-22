package com.example;
//Librerias sql y para conexion
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
//Librerias para archivos
import java.io.File;
import java.io.FileInputStream;

public class controladorConectorPool {
       
    private static final HikariDataSource pool;
    
    static {
        HikariConfig config = new HikariConfig();

        File configuracion = new File(
            System.getProperty("user.home"),
            "restaurante-db.properties"
        );

        Properties datos = new Properties();

        try {
            if (!configuracion.isFile()) {
                throw new IOException(
                    "No se encontró el archivo de configuración: "
                    + configuracion.getAbsolutePath()
                );
            }

            try (FileInputStream entrada =
                    new FileInputStream(configuracion)) {
                datos.load(entrada);
            }

        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        config.setJdbcUrl("jdbc:mysql://15.235.108.34:3306/proyecto?sslMode=VERIFY_CA");
        config.setUsername(datos.getProperty("db.user"));
        config.setPassword(datos.getProperty("db.password"));


        config.addDataSourceProperty(
            "trustCertificateKeyStoreUrl",
            new File(
                System.getProperty("user.home"),
                "Downloads/mysql-truststore.p12"
            ).toURI().toString()
        );
        config.addDataSourceProperty(
            "trustCertificateKeyStoreType",
            "PKCS12"
        );
        config.addDataSourceProperty(
            "trustCertificateKeyStorePassword",
            datos.getProperty("truststore.password")
        );

        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);

        pool = new HikariDataSource(config);
    }

//Retorno
    public static Connection conectar() throws SQLException {
        return pool.getConnection();
    }

//Metodo de prueba
    public static void probarConexion() throws SQLException {
        try (Connection con = conectar();
            var ps = con.prepareStatement("SELECT 1");
            var rs = ps.executeQuery()) {

            if (rs.next()) {
                System.out.println(
                    "Conexión correcta. Resultado: " + rs.getInt(1)
                );
            }
        }
    }

//Cierra el pool
    public static void cerrarPool() {
        pool.close();
    }


}
