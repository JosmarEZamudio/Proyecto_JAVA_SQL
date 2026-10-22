package com.example.controladoresRestaurante;
//Librerias para trabajar con objetos de fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
//Librerias para trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
//Librerias para trabajar con sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias adicionales
import com.example.controladorConectorPool;
import com.example.controladorPrincipal;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRestaurante;
import java.io.IOException;


public class ControladorMenuRestaurante { //Inicio de la clase
    private controladorPrincipal principal;
//Objetos de fxml, solo botones
    @FXML 
    private Button btnProducto;
    @FXML 
    private Button btnConsultar;
    @FXML 
    private Button btnAgregar;
    @FXML 
    private Button btnPendiente;
    @FXML 
    private Button btnHistorial;
    @FXML 
    private Button btnActivar;
    @FXML 
    private Label lblUbicacion;
//Metodo para ir a la interfaz de producto desplegados
    @FXML 
    private void interfazProducto() throws IOException{
        
        try{
            String direccion = "";
            if(consultaProductos()==0){
                direccion = "/com/example/interfazrestaurante/listaVacia.fxml";
            }else {
                direccion = "/com/example/interfazrestaurante/desplegarProducto.fxml";
            }

            FXMLLoader loaderLista = new FXMLLoader(
                getClass().getResource(direccion)
            );

            Pane lista = loaderLista.load();
            Scene escenaActual = btnProducto.getScene();

            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(lista); 
        }
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se puede conectar con la base, si el error persiste contactar a soporte.");
            return;
        }
        catch(IOException e){
              dialogosMensajes.mensajeError("Informacion error", 
            "No se puede cargar la interfaz, si el error persiste contactar a soporte.");
            e.printStackTrace();
            return;
        }
    
    }
//Metodo para ir a la interfaz de consultar producto
    @FXML 
    private void interfazConsultar() throws IOException{
        try{FXMLLoader loaderConsultar = new FXMLLoader(
            getClass().getResource("/com/example/interfazrestaurante/consultarProducto.fxml")
        );
        //Obtener pane(raiz) del fxml cargado
            Pane consultar = loaderConsultar.load();
            Scene escenaActual = btnConsultar.getScene();
        //Obtener scrollpane actual e insertar pane
            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(consultar); 
        }
         catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se puede cargar la interfaz, si el error persiste contactar a soporte.");
            
            return;
        }       
    }
//Metodo para ir a la interfaz de agregar producto
    @FXML 
    private void interfazAgregar() throws IOException{
        try{FXMLLoader loaderAgregar = new FXMLLoader(
            getClass().getResource("/com/example/interfazrestaurante/agregarProducto.fxml")
            );
            Pane agregar = loaderAgregar.load();
            Scene escenaActual = btnAgregar.getScene();
            
            ScrollPane contenedor = (ScrollPane)escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(agregar);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se puede cargar la interfaz, si el error persiste contactar a soporte.");
            return;
        }   
    }
//Metodo para ir a la interfaz de pedidos pendientes
    @FXML 
    private void interfazPendiente() throws IOException{
        try{FXMLLoader loaderPendiente = new FXMLLoader(
            getClass().getResource("/com/example/interfazrestaurante/consultarPedidos.fxml")
            );
        //Obtener raiz de la interfaz nueva y obtener escena actual
            Pane pendiente = loaderPendiente.load();
            Scene escenaActual = btnPendiente.getScene();
        //Pasar referencia del controlador principal
            ControladorPedidosPendientes controlador = loaderPendiente.getController();
            controlador.setControladorPrincipal(principal);
            //Obtener scroll de la escena actual y darle la raiz anterior
            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(pendiente);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se puede cargar la interfaz, si el error persiste contactar a soporte.");
            return;
        }   
    }
//Metodo para ir a la interfaz de historial de pedidos 
    @FXML 
    private void interfazHistorial() throws IOException{
        try{FXMLLoader loaderHistorial = new FXMLLoader(
            getClass().getResource("/com/example/interfazrestaurante/consultarHistorial.fxml")
            );
        //Obtener pane del archivo fxml (pane es raiz tambien en ese archivo)
            Pane historial = loaderHistorial.load();
            Scene escenaActual = btnHistorial.getScene();
        //Obtener Scroll pane actual y psasarle el pane
            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(historial);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se puede cargar la interfaz, si el error persiste contactar a soporte.");
            return;
        }   
    }

//Metodo para cerrar la sesion
    @FXML 
    private void cerrarSesion() throws IOException{
    //Cargar archivo fxml
        FXMLLoader loaderCerrar = new FXMLLoader(
            getClass().getResource("/com/example/saludoInicial.fxml")
        );
    //Obtener pane del archivo fxml (pane es raiz tambien en ese archivo)
        Pane cerrar = loaderCerrar.load();
        Scene escenaActual = btnHistorial.getScene();
    //Obtener Scroll pane actual y psasarle el pane
        ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
        contenedor.setContent(cerrar);

    //Si se inicio sesion con restaurante inicializar valores en nulo
        if(datosSesionRestaurante.getIdRestaurante()!=null){
        //Id y nombre restaurante
            datosSesionRestaurante.iniciar(null, null);
        //Coordenadas y direccion
            datosSesionRestaurante.guardarUbicacion(null, null, null);
            return;
        }

    }

//Metodo para ir a la interfaz de ubicacion
    @FXML
    private void ubicacion() throws IOException{
        FXMLLoader loaderUbicacion = new FXMLLoader(
            getClass().getResource("/com/example/pruebaUbicacion.fxml")
        );

        Scene ubicacion = new Scene(loaderUbicacion.load());
        Stage ventanaNueva = new Stage();

        ventanaNueva.setTitle("Guardar ubicacion");
        ventanaNueva.initModality(Modality.APPLICATION_MODAL);
        ventanaNueva.setScene(ubicacion);
        ventanaNueva.setOnHidden(
            event ->{
                try{
                    mostrarUbicacion();
                }
                catch(SQLException e){

                }
            }
        );
        ventanaNueva.showAndWait();

    }

//Metodo para hacer una consulta sobre si hay productos existente
    private int consultaProductos() throws SQLException{
    //Para consultar sql
        int id = datosSesionRestaurante.getIdRestaurante();
            System.out.println(id);
        String sql = "SELECT COUNT(*) AS cantidad FROM producto WHERE id_restaurante = ?";
        int valor=0; //inicializar variable para poder utilizar despues
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);
        ){ //TWR para cerrar conexion y Prepared
                consultar.setInt(1,id);
            try(ResultSet resultado = consultar.executeQuery();){ //Try para cerrar ResultSet   
                if (resultado.next()){
                    valor = resultado.getInt("cantidad"); //Obtener valor y retornar
                }    
            }//Cierrar segundo try
        }//Cierrar primer try
        return valor;
    }

//Metodo que consulta la cantidad de pedidos, si es cero inhabilitar btnHistorial y btnPendiente
    private int cantidadPedidos () throws SQLException{
    //Obtener id_restaurante e inicializar variable de retorno
        Integer id = datosSesionRestaurante.getIdRestaurante();
        Integer valor = null;
    //Setencia sql     
        String sql = "SELECT COUNT(detallepedido.id_pedido) AS cantidad " 
        +"FROM detallepedido JOIN producto "
        +"ON detallepedido.id_producto = producto.id_producto "
        +"JOIN restaurante "
        +"ON producto.id_restaurante = restaurante.id_restaurante "
        +"WHERE restaurante.id_restaurante = ?";
    //Primer try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                try(ResultSet resultado = consultar.executeQuery();){
                    if(resultado.next()){
                        valor = resultado.getInt("cantidad");
                        return valor;
                    }
                }//Cierra segundo try ResultSet
        }//Cierra primer try
        return 0;
    }

//Metodo para desplegar ubicacion
    public void mostrarUbicacion() throws SQLException{
    //Variables donde se guardaran los datos de direccion
        String direccion = null;
        Double longitud = null;
        Double latitud = null;
        Integer idUbicacion = null;
    //Obtener datos para realizar consultas
        Integer idRestaurante = datosSesionRestaurante.getIdRestaurante();
    //Segunda setencia sql
        String sqlUbicacion = """
            SELECT direccion,coordenada_y,coordenada_x,id_ubicacion
            FROM ubicacion
            WHERE id_restaurante = ? AND activo = ?
            """;
        //Segunda consulta
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultarUbicacion = conexion.prepareStatement(sqlUbicacion)){
           
            consultarUbicacion.setInt(1, idRestaurante);
            consultarUbicacion.setInt(2,1);
            try(ResultSet resultado = consultarUbicacion.executeQuery();){
                if(resultado.next()){
                    direccion = resultado.getString(1);
                    longitud = resultado.getDouble(2);
                    latitud = resultado.getDouble(3);
                    idUbicacion = resultado.getInt(4);
                }
            }//Cierra try Prepared
        }//Cierra try Result
        //Termina segunda consulta
    //Validar si las variables obtuvieron un valor
        if(direccion!=null && latitud != null && longitud != null){
            datosSesionRestaurante.guardarUbicacion(latitud, longitud, direccion);
            datosSesionRestaurante.idUbicacion(idUbicacion);
            lblUbicacion.setText(direccion);
        }
    }

//Metodo para setear controlador pasado
    public void setControladorPrincipal(controladorPrincipal principal) {
        System.out.println("Setter del menú recibió: " + principal);
        this.principal = principal;
    }

//Metodo de inicializacion 
    @FXML 
    private void initialize() throws SQLException{
    //Mensaje en label
        lblUbicacion.setText("Seleccione una ubicacion.");
    //Si el metodo cantidadPedidos retorna 0, entonces:  
        try{
            mostrarUbicacion();
            if(cantidadPedidos()==0){
                
                btnPendiente.setDisable(true);
                btnHistorial.setDisable(true);
            }
            }catch(SQLException e){
                dialogosMensajes.mensajeError("Mensaje error", 
                "No se pudo realizar la consulta.");
                e.printStackTrace();
                return;
            }
    }

}//Fin de la clase
