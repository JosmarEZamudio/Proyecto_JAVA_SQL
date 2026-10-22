package com.example.controladoresRestaurante;
//Librerias para java fxml
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Clases,librerias adicionales
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.controladorConectorPool;
import com.example.dialogosMensajes;

public class ControladorConsultarHistorial {    
//Elementos para la primera tabla
    @FXML 
    private TableView<Historial> tbvHistorial;
    @FXML 
    private TableColumn<Historial, Integer> tbcIdPedido;
    @FXML 
    private TableColumn<Historial, String> tbcClientePedido;
    @FXML 
    private TableColumn<Historial, LocalDate> tbcFechaPedido;
    @FXML 
    private TableColumn<Historial, String> tbcEstadoPedido;
    @FXML 
    private TableColumn<Historial, Integer> tbcTotalPedido;
//Elementos para la segunda talba 
    @FXML 
    private TableView<DetalleCompleto> tbvDetalle;
    @FXML 
    private TableColumn <DetalleCompleto, Integer> tbcIdDetalle;
    @FXML 
    private TableColumn <DetalleCompleto, String> tbcClienteDetalle;
    @FXML 
    private TableColumn <DetalleCompleto, String> tbcProductoDetalle;
    @FXML 
    private TableColumn <DetalleCompleto, Integer> tbcCantidadDetalle;
    @FXML 
    private TableColumn <DetalleCompleto, Integer> tbcPrecioDetalle;
    
//Lista para el historial
    ObservableList <Historial> listaHistorial = FXCollections.observableArrayList();
//Lista para el detalle
    ObservableList <DetalleCompleto> listaDetalle = FXCollections.observableArrayList();
//Variable para terminar
    private Integer idPublica = null;

//Metodo para consultar historial
    @FXML
    private void consultarHistorial() throws SQLException{
    //Sentencia sql
        String sql = """
            SELECT pedido.id_pedido,cliente.nombre,pedido.fecha,pedido.estado,
            SUM(detallepedido.cantidad*detallepedido.precio_unitario) AS subtotal
            FROM cliente JOIN pedido
            ON pedido.id_cliente = cliente.id_cliente
            JOIN detallepedido
            ON detallepedido.id_pedido = pedido.id_pedido
            JOIN producto
            ON detallepedido.id_producto = producto.id_producto
            WHERE pedido.estado >= ? 
            GROUP BY pedido.id_pedido,pedido.fecha,pedido.estado,cliente.nombre
        """;
    //Inicia try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
            //Clausulas
                consultar.setInt(1, 5);
            //Inicia try ResultSet
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        listaHistorial.add(
                            new Historial(
                                resultado.getInt(1),
                                resultado.getString(2),
                                resultado.getDate(3).toLocalDate(),
                                resultado.getString(4),
                                resultado.getInt(5)
                            )
                        );
                    }//Cierra while
                }//Cierra segundo try
        }//Cierra primer try
    }

//Metodo para consultar historial
    @FXML
    private void consultarDetalle(int id) throws SQLException{
    //Sentencia sql
        String sql = """
            SELECT pedido.id_pedido,cliente.nombre,producto.nombre,
            detallepedido.cantidad,producto.precio
            FROM cliente JOIN pedido
            ON pedido.id_cliente = cliente.id_cliente
            JOIN detallepedido
            ON detallepedido.id_pedido = pedido.id_pedido
            JOIN producto
            ON detallepedido.id_producto = producto.id_producto
            WHERE pedido.estado >= ? AND pedido.id_pedido = ?
        """;
    //Inicia try Connection y Prepared
        try(Connection conexion = controladorConectorPool.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
            //Clausulas
                consultar.setInt(1, 5);
                consultar.setInt(2, id);
            //Inicia try ResultSet
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        listaDetalle.add(
                            new DetalleCompleto(
                                resultado.getInt(1),
                                resultado.getString(2),
                                resultado.getString(3),
                                resultado.getInt(4),
                                resultado.getInt(5)
                            )
                        );
                    }//Cierra while
                }//Cierra segundo try
        }//Cierra primer try
    }


//Metodo para consultar detalle por seleccion de la tabla pedido
    @FXML 
    private void asignarColumnaDetalle()throws SQLException{
        tbvHistorial.getSelectionModel().selectedItemProperty().addListener(
            (observable,anterior,seleccionado)->{
                tbvDetalle.getItems().clear();
            //Validar si un item se selecciono
                if(seleccionado!=null){
                    idPublica = seleccionado.getId();
                    System.out.println(idPublica);
                }
                //Llamar columnas
                    asignarDetalle();
                //Hacer consulta
                try{
                    consultarDetalle(idPublica);
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


//Metodos para asignar getters a las columnas de la tabla historial
    @FXML 
    private void asignarHistorial(){
        tbcIdPedido.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );

        tbcClientePedido.setCellValueFactory(
            new PropertyValueFactory<>("nombre")
        );

        tbcFechaPedido.setCellValueFactory(
            new PropertyValueFactory<>("fecha")
        );

        tbcEstadoPedido.setCellValueFactory(
            new PropertyValueFactory<>("estado")
        );

        tbcTotalPedido.setCellValueFactory(
            new PropertyValueFactory<>("total")
        );
    }

//Metodos para asignar getters a las columnas de la tabla historial
    @FXML 
    private void asignarDetalle(){
        tbcIdDetalle.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );

        tbcClienteDetalle.setCellValueFactory(
            new PropertyValueFactory<>("nombre")
        );

        tbcProductoDetalle.setCellValueFactory(
            new PropertyValueFactory<>("producto")
        );

        tbcCantidadDetalle.setCellValueFactory(
            new PropertyValueFactory<>("cantidad")
        );

        tbcPrecioDetalle.setCellValueFactory(
            new PropertyValueFactory<>("precio")
        );
    }

//Metodo initialize
    @FXML 
    private void initialize() throws SQLException{
    //Mostrar mensaje en la talba
        tbvDetalle.setPlaceholder(
            new Label("Seleccione un pedido para ver el detalle.")
        );
    //Mostrar pedidos completados en la tabla
        try{
        //Generar columnas historial
            consultarHistorial();
            asignarHistorial();
            tbvHistorial.setItems(listaHistorial);
        //Generar columnas detalle
            asignarColumnaDetalle();
        }
        catch(SQLException e){
            dialogosMensajes.mensajeError("Mensaje error", 
            "No se pudo cargar el historial.");
            return;
        }
    }

}
