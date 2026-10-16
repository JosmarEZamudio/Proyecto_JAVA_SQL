package com.example.controladorSesion;
//Componentes de java fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
//Componetes para la interfaz
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//Componentes para sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Funciones adicionales
import com.example.controladorConector;
import com.example.dialogosMensajes;
import java.io.IOException;

public class ControladorInicioSesion {
//Objetos botones
    @FXML 
    private Button btnIngresarCliente;
    @FXML 
    private Button btnIngresarNegocio;
    @FXML 
    private Button btnCancelar; 
//Objetos text
    @FXML 
    private TextField txtUsuario;
    @FXML 
    private TextField txtPassword;

//Metodo para iniciar sesion como cliente
    @FXML 
    private void inicioSesionCliente() throws SQLException{
        //Obtener text de TextField
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();
        Scene escenaActual = btnIngresarCliente.getScene();

        if (txtUsuario.getText().isBlank() ||
            txtPassword.getText().isBlank()){
                dialogosMensajes.mensajeInformacion("Informacion", "¡¡Ingresar ambos datos!!");
                return;
        }

        String sql = "SELECT id_cliente,nombre FROM cliente WHERE correo = ? AND contrasena = ?";

        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setString(1, usuario);
                consultar.setString(2, password);
            try(ResultSet resultado = consultar.executeQuery();){
                if(resultado.next()){
                    int id = resultado.getInt("id_cliente");
                    String nombre = resultado.getString("nombre");
                    datosSesionCliente.iniciar(id, nombre); //Pasar datos de sesion 
                    menuCliente(escenaActual);
                    dialogosMensajes.mensajeInformacion("Informacion Sesion","Usuario encontrado, bienvenido");
                    return;
                }else{
                    dialogosMensajes.mensajeError("Informacion Sesion",
                    "Usuario no encontrado, intentete de nuevo.");
                }
            } //Cierra (ResultSet) segundo try
        } //Cierra (Connection y Prepared) primer try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo conectar con la base, si persiste con el error contacte a soporte.");
            return;
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se cargar la interfaz, contacte a soporte en caso de persistir con el error.");
            e.printStackTrace();
            return;
        }
    }
//Metodo para iniciar sesion como negocio
    @FXML 
    private void inicioSesionRestaurante() throws SQLException{
        Scene escenaActual = btnIngresarNegocio.getScene();
    //Obtiene valores de los text
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();
    //Valida text en blanco
        if (txtUsuario.getText().isBlank() ||
            txtPassword.getText().isBlank()){
                dialogosMensajes.mensajeInformacion("Informacion", "¡¡Ingresar ambos datos!!");
                return;
        }
    //Cadena de consulta
        String sql = """
            SELECT id_restaurante,nombre,coordenada_x,coordenada_y
            FROM restaurante 
            WHERE correo = ? AND contrasena = ?
                """;
    //Inicia conexion y consulta
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
            consultar.setString(1, usuario);
            consultar.setString(2, password);
            try(ResultSet resultado = consultar.executeQuery();){
            //Obtener resultados de la consulta
                if(resultado.next()){
                    int id = resultado.getInt("id_restaurante");
                    String nombre = resultado.getString("nombre");
                    int coordenadaX = resultado.getInt("coordenada_x");
                    int coordenadaY = resultado.getInt("coordenada_y");
                    datosSesionRestaurante.iniciar(id,nombre); //*** Pasar datos de sesion
                    datosSesionRestaurante.guardarUbicacion(coordenadaX, coordenadaY); //Pasar datos de la ubicacion
                    dialogosMensajes.mensajeInformacion("Informacion sesion", 
                        "Negocio encontrado, bienvenido."
                    );
                    menuRestaurante(escenaActual);
                    return;
                }else{
                    dialogosMensajes.mensajeError("Informacion sesion", 
                    "Negocio no encontrado, intente de nuevo.");
                }
            } //Cierra segundo try (ResultSet)
        }//Ciera primer try (Connection y Prepared)
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se puede conectar con la base, contacte a soprte en caso de persistir con el error.");
            return;
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se cargar la interfaz, contacte a soporte en caso de persistir con el error.");
            return;
        }
    }

//Metodo para cancelar y regresar al saludo
    @FXML 
    private void cancelar() throws IOException{
        try{
            FXMLLoader loaderSaludo = new FXMLLoader(
                getClass().getResource("/com/example/saludoInicial.fxml")
            );

            Pane saludo = loaderSaludo.load();
            Scene escenaActual = btnCancelar.getScene();

            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(saludo);
        }catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se cargar la interfaz, contacte a soporte en caso de persistir con el error.");
            return;
        }
    }

//Metodo para mostrar la ventana despues del inicio de sesion correcto para restaurante
    @FXML 
    private void menuRestaurante(Scene escenaActual) throws IOException{
        FXMLLoader loaderMenu = new FXMLLoader(
                getClass().getResource("/com/example/interfazrestaurante/menuPrincipal.fxml")
            );
        //Obtener pane del archivo fxml
        Pane menu = loaderMenu.load();
        //Pasar escena actual como parametro, no es necesario volver a buscarla
        ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
        contenedor.setContent(menu);
    }

//Metodo para mostrar la ventana despues del inicio de sesion correcto para cliente
    @FXML 
    private void menuCliente(Scene escenaActual) throws IOException{
    //Cargar archivo de fxml
        FXMLLoader loaderMenu = new FXMLLoader(
            getClass().getResource("/com/example/interfazcliente/interfazPrincipal.fxml")
        );
    //Cargar interfaz (sub ventana)
        Pane menu = loaderMenu.load();
    //Setear 
        ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
        contenedor.setContent(menu);
    }

}

