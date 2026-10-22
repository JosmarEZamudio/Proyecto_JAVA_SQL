package com.example;
//Librerias sql y archivos
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class controladorConector {

    public static Connection conectar() throws SQLException {
        String url = "jdbc:mysql://15.235.108.34:3306/proyecto";
        String usuario = "app_restaurante";

        File configuracion = new File(
            System.getProperty("user.home"),
            "restaurante-db.properties"
        );

        if (!configuracion.isFile()) {
            throw new SQLException(
                "No se encontró el archivo de configuración: "
                + configuracion.getAbsolutePath()
            );
        }

        Properties datos = new Properties();

        try (FileInputStream entrada =
                new FileInputStream(configuracion)) {
            datos.load(entrada);
        } catch (IOException e) {
            throw new SQLException(
                "No se pudo leer el archivo de configuración.", e
            );
        }

        String password = datos.getProperty("db.password");
        String passwordCertificado = datos.getProperty("truststore.password");

        

        if (password == null || password.isBlank()) {
            throw new SQLException("DB_PASSWORD está vacía o no llega a Java.");
        }


        File certificado = new File(
            System.getProperty("user.home"),
            "Downloads/mysql-truststore.p12"
        );

        if (!certificado.isFile()) {
            throw new SQLException(
                "No se encontró el almacén de certificados: "
                + certificado.getAbsolutePath()
            );
        }

        try {
            Properties propiedades = new Properties();
            propiedades.setProperty("user", usuario);
            propiedades.setProperty("password", password);
            propiedades.setProperty("sslMode", "VERIFY_CA");
            propiedades.setProperty(
                "trustCertificateKeyStoreUrl",
                certificado.toURI().toURL().toString()
            );
            propiedades.setProperty(
                "trustCertificateKeyStoreType", "PKCS12"
            );
            propiedades.setProperty(
                "trustCertificateKeyStorePassword",
                passwordCertificado
            );

            return DriverManager.getConnection(url, propiedades);

        } catch (java.net.MalformedURLException e) {
            throw new SQLException(
                "La ruta del certificado no es válida.", e
            );
        }
    }
}
