package com.example.controladoresCliente;
//Librerias para trabajar con objetos de fxml
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
//Librerias para trabajar con la ventana
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
//Librerias para trabajar con sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias para conector y datos flotantes
import com.example.controladorConectorPool;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionCliente;
//Librerias adicionales
import java.io.IOException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ControladorHistorialCliente {
//Objetos de java fxml para la tabla pedido
    @FXML 
    private TableView<Historial> tbvHistorialPedido;
    @FXML 
    private TableColumn<Historial,Integer> tbcIdPedido;
    @FXML 
    private TableColumn<Historial,LocalDate> tbcFecha;
    @FXML 
    private TableColumn<Historial,String> tbcEstado;
//Objetos de java para la tabla detalle pedido
    @FXML 
    private TableView<DetalleHistorial> tbvDetalle;
    @FXML 
    private TableColumn<DetalleHistorial,Integer> tbcDetallePedido; 
    @FXML 
    private TableColumn<DetalleHistorial,String> tbcLugar;
    @FXML 
    private TableColumn<DetalleHistorial,String> tbcProducto;
    @FXML 
    private TableColumn<DetalleHistorial,Integer> tbcCantidad;
    @FXML 
    private TableColumn<DetalleHistorial,Integer> tbcPrecio;
    @FXML 
    private TableColumn<DetalleHistorial, Integer> tbcSubtotal;

//Objetos de javafx adicionales
    @FXML 
    private Button btnRegresar;
//Declarar listas y variable como atributo de la clase
    ObservableList<Historial> lista = FXCollections.observableArrayList();
    ObservableList <DetalleHistorial> listaDetalle = FXCollections.observableArrayList();
    private Integer idPublica = null;
//Metodo para consultar los pedidos que ha hecho el cliente, no importa el estado 
    private void consultar() throws SQLException{
    //Obtener id del cliente
        int id = datosSesionCliente.getIdCliente();
    //Declara variables donde se guardara el resultado de la consulta momentaneamente
        int idPedido;
        LocalDate fecha;
        //int estado;
    //Sentencia sql
        String sql = "SELECT id_pedido,fecha,estado FROM pedido WHERE id_cliente = ? AND estado = ?";
    //Abre try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                consultar.setInt(2,0);
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        idPedido = resultado.getInt("id_pedido");
                        fecha = resultado.getDate("fecha").toLocalDate();
                        lista.add(
                            new Historial(idPedido, fecha, "Entregado al cliente.")
                        );
                    }
                }//Ciera segundo try ResultSet
        }//Ciera primer try
    }

//Metodo para consultar el detalle del pedido seleccionado 
    @FXML 
    private void detallePedido(int id) throws SQLException{   
    //Sentencia sql
        String sql = """
            SELECT detallepedido.id_detallepedido, producto.nombre, restaurante.nombre,
            detallepedido.cantidad, detallepedido.precio_unitario,
            detallepedido.cantidad*detallepedido.precio_unitario AS subtotal
            FROM pedido JOIN detallepedido
            ON pedido.id_pedido = detallepedido.id_pedido
            JOIN producto 
            ON detallepedido.id_producto = producto.id_producto
            JOIN restaurante 
            ON producto.id_restaurante = restaurante.id_restaurante
            WHERE pedido.id_pedido = ?
        """;
    //Inicia try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
            //Inicia try ResultSet
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        listaDetalle.add(
                            new DetalleHistorial(
                                resultado.getInt(1),
                                resultado.getInt(4),
                                resultado.getInt(6),
                                resultado.getInt(5),
                                resultado.getString(3),
                                resultado.getString(2)
                            )
                        );
                    }//Termina while
                }//Cierra segundo try
        }//Cierra primer try    
    }

//Consultar detalle por seleccion de la tabla pedido
    @FXML 
    private void consultarDetalle()throws SQLException{
        tbvHistorialPedido.getSelectionModel().selectedItemProperty().addListener(
            (observable,anterior,seleccionado)->{
                tbvDetalle.getItems().clear();
            //Validar si un item se selecciono
                if(seleccionado!=null){
                    idPublica = seleccionado.getId();
                    System.out.println(idPublica);
                }
                //Llamar columnas
                    definirColumnasDetalle();
                //Hacer consulta
                try{
                    detallePedido(idPublica);
                    tbvDetalle.setItems(listaDetalle);
                }
                catch(SQLException e){
                    dialogosMensajes.mensajeError("Mensaje informacion", 
                    "No se pudo consultar el detalle.");
                    return;
                }       
            }//Cierra metodo lamda
        );//Cierra argumento
    }

//Metodo para definir las columnas con su getter correspondiente
    @FXML 
    private void definirColumnasPedido(){
        tbcIdPedido.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );
        tbcFecha.setCellValueFactory(
            new PropertyValueFactory<>("fecha")
        );
        tbcEstado.setCellValueFactory(
            new PropertyValueFactory<>("estado")
        );
    }

//Metodo para definir las columnas de la tabla detalle con su getter correspondiente
    @FXML 
    private void definirColumnasDetalle(){
        tbcDetallePedido.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );
        tbcLugar.setCellValueFactory(
            new PropertyValueFactory<>("restaurante")
        );
        tbcProducto.setCellValueFactory(
            new PropertyValueFactory<>("producto")
        );
        tbcCantidad.setCellValueFactory(
            new PropertyValueFactory<>("cantidad")
        );
        tbcPrecio.setCellValueFactory(
            new PropertyValueFactory<>("precio")
        );
        tbcSubtotal.setCellValueFactory(
            new PropertyValueFactory<>("subtotal")
        );
    }

//Metodo para regresar de interaz
    @FXML 
    private void regresar() throws IOException{
    //Cargar archivo fxml
        FXMLLoader loaderPrincipal = new FXMLLoader(
            getClass().getResource("/com/example/interfazcliente/interfazPrincipal.fxml")
        );
    //Obtener la raiz Pane del archivo fxml
        Pane principal = loaderPrincipal.load();
        Scene escenaActual = btnRegresar.getScene();
    //Encontrar contenedor actual y setear
        ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
        contenedor.setContent(principal);
    }

//Metodo de iniciacion
    @FXML 
    private void initialize() throws SQLException{
    //Metodo para consultar y mostrar en tabla pedido
        consultar();
        definirColumnasPedido();
    //Metodo para consultar y mostrar en tabla detalle (incluye las dos)
        consultarDetalle();
    //Setear elementos en tabla pedido
        tbvHistorialPedido.setItems(lista);
    //Mensaje para la tabla detalle
        tbvDetalle.setPlaceholder(
            new Label("Seleccione un pedido para ver el detalle.")
        );
        
    }
    
}
