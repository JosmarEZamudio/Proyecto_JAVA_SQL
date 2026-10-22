package com.example.controladoresRepartidor;
//Importar librerias para trabajar con objetos fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
//Importar librerias para trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
//Librerias,clases adicionales
import java.io.IOException;
import com.example.controladorConectorPool;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRepartidor;

public class ControladorPrincipal {
//Objetos txt 
    @FXML 
    private TextField txtCoordenadaX;
    @FXML 
    private TextField txtCoordenadaY;
//Objetos de menu fxml
    @FXML 
    private MenuButton mnbOpciones;
    @FXML 
    private MenuItem mniInformacion;
    @FXML 
    private MenuItem mniHistorial;
//Objetos boton fxml
    @FXML
    private Button btnRegistrar;
    @FXML 
    private Button btnIniciar;
    @FXML 
    private Button btnActivar;
    @FXML 
    private Button btnDesactivar;

//Metodo de menu desplegable para ir a la interfaz informacion personal
    @FXML 
    private void interfazPersonal() throws IOException {
    //Cargar archivo fxml
        try{
            FXMLLoader loaderInformacion = new FXMLLoader(
                getClass().getResource("/com/example/interfazrepartidor/interfazInformacion.fxml")
            );
        //Cargar raiz
            Pane informacion = loaderInformacion.load();
            Scene escenaNueva = mnbOpciones.getScene();
        //Obtener contenedor y setear el contenido
            ScrollPane contenedor = (ScrollPane) escenaNueva.lookup("#contenedorprincipal");
            contenedor.setContent(informacion);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo de menu desplegable para ir a la interfaz historial pedidos
    @FXML 
    private void interfazHistorial () throws IOException{
    //Cargar fxml
        try{
            FXMLLoader loaderHistorial = new FXMLLoader(
                getClass().getResource("")
            );
        //Cargar raiz
            Pane historial = loaderHistorial.load();
            Scene escenaNueva = mnbOpciones.getScene();
        //Encontrar contenedor y setear contenido
            ScrollPane contenedor = (ScrollPane) escenaNueva.lookup("#contenedorprincipal");
            contenedor.setContent(historial);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo para mostrar la interfaz de registro
    @FXML
    private void registrar() throws IOException{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderRegistrar = new FXMLLoader(
                getClass().getResource("/com/example/interfazrepartidor/interfazRegistro.fxml")
            );
        //Cargar raiz y obtener escena actual
            Pane registrar = loaderRegistrar.load();
            Scene escenaActual = btnRegistrar.getScene();
        //Encontrar el contenedor y setear
            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(registrar);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo para mostrar la interfaz de inicio de sesion
    @FXML 
    private void iniciar() throws IOException{
    //Cargar archivo fxml
        try{
            FXMLLoader loaderSesion = new FXMLLoader(
                getClass().getResource("/com/example/interfazrepartidor/interfazSesion.fxml")
            );
        //Cargar escena
            Scene escenaNueva = new Scene(loaderSesion.load());
        //Crear nueva ventana y setear escena
            Stage ventanaNueva = new Stage();
            ventanaNueva.setTitle("Iniciar sesion repartidor");
            ventanaNueva.setScene(escenaNueva);
            ventanaNueva.initModality(Modality.APPLICATION_MODAL);
            ventanaNueva.setOnHidden(event ->{
                initialize();
            });
            ventanaNueva.showAndWait();
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
        }
    }

//Metodo para guardar ubicacion del repartidor
        public static Double[] generarUbicacionAleatoria(
            Double longitudCentro, Double latitudCentro){
            Double radioMaximo = 8.0; // kilómetros
            Double distancia = ThreadLocalRandom.current()
                    .nextDouble(0, radioMaximo);
            Double angulo = ThreadLocalRandom.current()
                    .nextDouble(0, 2 * Math.PI);
            Double nuevaLatitud = latitudCentro
                    + (distancia * Math.cos(angulo)) / 111.32;
            Double nuevaLongitud = longitudCentro
                    + (distancia * Math.sin(angulo))
                    / (111.32 * Math.cos(
                            Math.toRadians(latitudCentro)));
            return new Double[]{nuevaLongitud, nuevaLatitud};
    }

//Metodo para imprimir las variables generadas
    @FXML 
    private void generarUbicacion() throws SQLException{
    //Ubicaciion aleatoria
        Double[] ubicacion = generarUbicacionAleatoria(-100.3673021,25.8397959);
    //Mostrar coordenadas
        txtCoordenadaX.setText(String.format("Longitud: %.7f", ubicacion[0]));
        txtCoordenadaY.setText(String.format("Latitud: %.7f", ubicacion[1]));
    //Obtener id del repartidor
        Integer id = datosSesionRepartidor.getIdRepartidor();
    //Sentencia sql
        String sql = """
            UPDATE repartidor 
            SET x_actual = ?, y_actual = ?
            WHERE id_repartidor = ?
        """;
    //Inicia try
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement actualizar = conexion.prepareStatement(sql);){
                actualizar.setDouble(1, ubicacion[0]);
                actualizar.setDouble(2, ubicacion[1]);
                actualizar.setInt(3, id);
            //Ejecutar actualizacion
                actualizar.executeUpdate();
        }//Termina try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
                "No pudo guardar la ubicacion"
            );
        }

        dialogosMensajes.mensajeInformacion("Mensaje informacion", 
        "Ubicacion activada correctamente.");
    }

//Metodo para desactivar repartidor
    @FXML
    private void desactivar() throws SQLException{
    //Obtener id de repartidor
        int idRepartidor = datosSesionRepartidor.getIdRepartidor();
    //Sentencia sql
        String sql = """
            UPDATE repartidor
            SET activo = ?, disponibilidad = ?
            WHERE id_repartidor = ?
                """;
    //Inicia try 
        Connection conexion = null;
        try{
            conexion = controladorConectorPool.conectar();
            conexion.setAutoCommit(false);
            try(PreparedStatement editar = conexion.prepareStatement(sql);){
                editar.setInt(1, 0);
                editar.setInt(2,0);
                editar.setInt(3,idRepartidor);

                editar.executeUpdate();
            }
        //Commit a la base da datos
            conexion.commit();
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Cesion cerrada, hasta luego.");
            txtCoordenadaX.clear();
            txtCoordenadaY.clear();
        //Resetear valores de inicio de sesion
            datosSesionRepartidor.setRepartidor(0, "");
            datosSesionRepartidor.setUbicacion(null, null);
            validacionId();
        }
        catch(SQLException e){
            if(conexion!=null){
                try{
                    conexion.rollback();
                }
                catch(SQLException v){
                    e.addSuppressed(v);
                }
            }
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cerrar la sesion.");
        }finally{
            if(conexion!=null){
                conexion.close();
            }
        }
    }

//Metodo validacionId para mostrar ciertas opciones disponibles o no despues del inicio de sesion
    @FXML 
    private void validacionId(){
        if(datosSesionRepartidor.getIdRepartidor() == null){
            btnIniciar.setVisible(true);
            btnRegistrar.setVisible(true);
            btnActivar.setDisable(true);
            btnDesactivar.setDisable(true);
            mniInformacion.setDisable(true);
        }
        else{
            btnIniciar.setVisible(false);
            btnRegistrar.setVisible(false);
            btnActivar.setDisable(false);
            btnDesactivar.setDisable(false);
            mniInformacion.setDisable(false);
        }
    }

//Metodo para validar ubicacion
    @FXML 
    private void validacionUbicacion(){
        if(datosSesionRepartidor.getCoordenadaX()!=null && datosSesionRepartidor.getCoordenadaY()!=null){
            txtCoordenadaX.setText(String.valueOf(datosSesionRepartidor.getCoordenadaX()));
            txtCoordenadaY.setText(String.valueOf(datosSesionRepartidor.getCoordenadaY()));
            btnActivar.setDisable(true);
        }
    }

//Metodo de inicializacion
    @FXML 
    private void initialize(){
        txtCoordenadaX.setDisable(true);
        txtCoordenadaY.setDisable(true);
        validacionId();
        validacionUbicacion();
    }
    
}
