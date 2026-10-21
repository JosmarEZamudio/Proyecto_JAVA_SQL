package com.example.controladoresCliente;
//Objetos de javafxml
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
//Librerias para posiciones
import javafx.geometry.Pos;
import javafx.geometry.Insets;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
//Librerias adicionales
import java.io.IOException;
import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionCliente;

public class ControladorPrincipalCliente {
//Objetos FXML
    @FXML
    private Pane pnBarra;
    @FXML 
    private MenuButton mnbOpciones;
    @FXML 
    private MenuItem mniHistorial;
    @FXML 
    private GridPane gdpRestaurante;
    @FXML 
    private TextField txtBuscar;
//Objetos label fxml
    @FXML 
    private Label lblAgregado; 
    @FXML 
    private Label lblTotal;
    @FXML 
    private Label lblMensaje;
    @FXML 
    private Label lblUbicacion;
    @FXML 
    private Label lblUbicacionNombre;
//Botones fxml
    @FXML 
    private Button btnDetalle;
    @FXML 
    private Button btnOrdenar;
    @FXML 
    private Button btnBuscar;

//Metodo para cargar interfaz de informacion    
    @FXML 
    private void interfazInformacion() throws IOException{
        //Cargar FXML 
        try{FXMLLoader loaderInformacion = new FXMLLoader(
                getClass().getResource("/com/example/interfazcliente/interfazInformacion.fxml")
            );
        //Cargar interfaz (sub ventana)
            Pane informacion = loaderInformacion.load();
            Scene escenaActual = mnbOpciones.getScene();
        //Obtener contenedor y setear informacion
            ScrollPane contenedor = (ScrollPane)escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(informacion);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error.", 
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }
//Metodo para cargar interfaz de historial
    @FXML
    private void interfazHistorial() throws IOException{
        //Cargar archivo fxml
        try{FXMLLoader loaderHistorial = new FXMLLoader(
                getClass().getResource("/com/example/interfazcliente/interfazHistorial.fxml")
            );
        //Obtener interfaz (sub ventana)
            Pane historial = loaderHistorial.load();
            Scene escenaActual = mnbOpciones.getScene();
        //Obtener contenedor y setear contenido
            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(historial);
        }
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error.", 
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para cerrar sesion 
    @FXML 
    private void cerrarSesion() throws IOException{
    //Cargar archivo fxml
        FXMLLoader loaderCerrar = new FXMLLoader(
            getClass().getResource("/com/example/saludoInicial.fxml")
        );
    //Obtener raiz y escena
        Pane cerrar = loaderCerrar.load();
        Scene escenaActual = mnbOpciones.getScene();
    //Obtener contenedor y seteae
        ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
        contenedor.setContent(cerrar);
    //Si se inicio sesion con cliente inicializar valores en nulods
        if(datosSesionCliente.getIdCliente()!=null){
        //Id y nombre cliente
            datosSesionCliente.iniciar(null, null);
        //Coordenadas y direccion
            datosSesionCliente.ubicacion(null, null, null);
            return;
        }
    
    }
//Consultar negocios existentes
    @FXML 
    private void consultarNegocios() throws SQLException{
        int columna = 0;  
    //Inicializacion de variables donde se guardar los resultados de la consulta
        int idRestaurante;
        String nombre;
        String telefono;
    //Sentencia sql
        String sql = """
            SELECT restaurante.id_restaurante,restaurante.nombre,restaurante.telefono,
                ST_Distance_Sphere(
                    POINT(ubicacion.coordenada_y, ubicacion.coordenada_x),
                    POINT(-100.3673021, 25.8397959)
                ) AS distancia_metros
            FROM restaurante JOIN ubicacion
            ON restaurante.id_restaurante = ubicacion.id_restaurante
            WHERE ST_Distance_Sphere(
                POINT(ubicacion.coordenada_y, ubicacion.coordenada_x),
                POINT(-100.3673021, 25.8397959)
            ) <= 7000 AND activo = 1;
        """; 
    //Primer try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql)){
            //Try resultSet
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        idRestaurante = resultado.getInt("id_restaurante");
                        nombre = resultado.getString("nombre");
                        telefono = resultado.getString("telefono");
                        crearTarjeta(idRestaurante,nombre,telefono,columna);
                        columna++;
                        //Preparacion para salto de fila
                    }
                }//Cierra segunfo try ResultSet
        }//Cierra priemr try
        catch(IOException e){
            dialogosMensajes.mensajeInformacion("Informacion imagenes", 
            "No se pudieron cargar las imagenes.");
            return; 
        }
    }

//Metodo donde se crearan las tarjetas
    @FXML 
    private void crearTarjeta(int idR, String nombreR,
         String telefonoR, int columna) throws IOException{  
    //Crear objetos
        VBox vbTajeta = new VBox();
        Label lblNombre = new Label();
        Label lblTelefono = new Label();
        Button btnVer = new Button();
    //Definicion
        lblNombre.setText(nombreR);
        lblTelefono.setText(telefonoR);
        btnVer.setText("Ver productos");
    //Implementacion
        btnVer.setOnAction(e->{
            try{
            Scene escenaActual = btnVer.getScene();
            datosSesionCompra.guardarRestaurante(idR, nombreR);
            cargarInterfaz(escenaActual);
            }catch(IOException c){
                dialogosMensajes.mensajeInformacion("Informacion error", 
                "No se pudo cargar la interfaz, intente de nuevo.");
                return;
            }
        });
    //Dar parametros, ajustar tamaños
        vbTajeta.getChildren().addAll(lblNombre,lblTelefono,btnVer);
        vbTajeta.setAlignment(Pos.CENTER);
        vbTajeta.setSpacing(5);
        gdpRestaurante.add(vbTajeta,columna,0);

    }

//Metodo para carga la interfaz de producto
    @FXML    
    private void cargarInterfaz(Scene esceneActual) throws IOException{
    //Cargar fxml
        FXMLLoader loaderProducto = new FXMLLoader(
            getClass().getResource("/com/example/interfazcliente/interfazProducto.fxml")
        );
    //Cargar interfaz en un pane (es una sub ventana y pane raiz)
        Pane producto = loaderProducto.load();
    //Obtener contenedor y setear 
        ScrollPane contenedor = (ScrollPane) esceneActual.lookup("#contenedorprincipal");
        contenedor.setContent(producto);
    }

//Metodo para para consultar la cantidad depedidos realizados y mostar interfaz al historial
    @FXML 
    private int consultaPedidos() throws SQLException{
    //Incializar variable de retorno y obtener id_cliente
        int valor = 0;
        int id = datosSesionCliente.getIdCliente();
    //Sentencia sql
        String sql = "SELECT COUNT(*) AS cantidad FROM pedido WHERE id_cliente = ?";
    //Abre primer try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                try(ResultSet resultado = consultar.executeQuery();){
                    if(resultado.next()){
                        valor = resultado.getInt("cantidad");
                        return valor;
                    }
                }//Cierra segundo try    
        }//Cierra primer try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion eror", 
            "No se pudo realizar la consultar pedidos, en caso de persistir contacte a soporte.");
        }
        return 0;
    }

//Metodo para cargar interfaz de Ubicacion
    @FXML 
    private void cargarInterfazUbicacion() throws IOException{
    //Validar si existe usuario 
        if(datosSesionCliente.getIdCliente()==null){
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "No existe una sesion!! Inicie sesion");
        }

    //Cargar archivo fxml
        try{
        FXMLLoader loaderUbicacion = new FXMLLoader(
            getClass().getResource("/com/example/pruebaUbicacion.fxml")
        );
    //Obtener escena
        Scene escenaNueva = new Scene(loaderUbicacion.load());
        Stage nuevaVentana = new Stage();
    //Crear stage nueva
        nuevaVentana.setTitle("Ingresar ubicacion");
        nuevaVentana.setScene(escenaNueva);
    //Instruccion para no poder interactuar con la ventanda secundaria hasta cerrarla
        nuevaVentana.initModality(Modality.APPLICATION_MODAL);
        nuevaVentana.setOnHidden(c->{
           verificar();
           try{
                if(datosSesionCliente.getCoordenadaX()==null &&
                    datosSesionCliente.getCoordenadaY()==null){
                        return;
                    }
                despligue();
           }catch(SQLException e){
                dialogosMensajes.mensajeError("Informacion error", 
                "No se pudo consultar los negocios disponibles, intente de nuevo.");
                return;
           }
        });
        nuevaVentana.showAndWait();
        }catch(IOException e){
            dialogosMensajes.mensajeInformacion("Informacion error", 
            "No se pudo cargar la interfaz de ubicacion, intente de nuevo.");
            e.printStackTrace();
        }
    }

//Metodo local para refrescar ventana principal si se agrego una ubicacion
    @FXML 
    private void verificar(){
        if(datosSesionCliente.getCoordenadaX() != null &
            datosSesionCliente.getCoordenadaY() != null){
                btnBuscar.setDisable(true);
                lblMensaje.setVisible(false);
                lblUbicacion.setVisible(true);
                lblUbicacionNombre.setVisible(true);
                lblUbicacionNombre.setText(datosSesionCliente.getNombreUbicacion());
                return;
            }
    }

//Metodo de despligue de producto completo
    @FXML 
    private void despligue() throws SQLException{
        //Dar propiedades size a gridpane
        gdpRestaurante.setHgap(30);
        gdpRestaurante.setPadding(new Insets(80, 25, 0, 25));
        try{
            consultarNegocios();
            pnBarra.setVisible(false);
        }catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo generar la consulta de restaurantes, intente de nuevo.");
            return;
        }
    //Validar si hay producto para que aparezva la barra de compras y se actualice
        if(datosSesionCompra.getCantidad()>0){
            pnBarra.setVisible(true);
            lblAgregado.setText("Productos agregados: "+datosSesionCompra.getCantidad());
            lblTotal.setText("Total: $"+datosSesionCompra.getTotal());
        }
    //Validar si hay pedido para poder acceder a la interfaz de historial
        if(consultaPedidos()==0){
            mniHistorial.setDisable(false);
        }{
            mniHistorial.setVisible(true);
        }
    }

//Metodo de inicializacion
    @FXML 
    private void initialize() throws SQLException{
    //Mostrar aviso si ubiacion esta activa, si no mostrar ubicacion
        if(datosSesionCliente.getCoordenadaX() == null & 
        datosSesionCliente.getCoordenadaY()==null){
            lblMensaje.setVisible(true);
            lblUbicacion.setVisible(false);
            lblUbicacionNombre.setVisible(false);
            btnDetalle.setDisable(true);
            btnOrdenar.setDisable(true);
            btnBuscar.setDisable(true);
            pnBarra.setVisible(false);
            return;
        }
        verificar();
        despligue();  
    }

}


