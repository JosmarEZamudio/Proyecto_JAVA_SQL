package com.example.controladoresCliente;
//Librerias para trabajar con objetos de fxml
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
import com.example.controladorConector;
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
    private TableColumn<Historial,Integer> tbcEstado;
//Objetos de java para la tabla detalle pedido

//Objetos de javafx adicionales
    @FXML 
    private Button btnRegresar;

    ObservableList<Historial> lista = FXCollections.observableArrayList();

//Metodo para consultar los pedidos que ha hecho el cliente, no importa el estado 
    private void consultar() throws SQLException{
    //Obtener id del cliente
        int id = datosSesionCliente.getIdCliente();
    //Declara variables donde se guardara el resultado de la consulta momentaneamente
        int idPedido;
        LocalDate fecha;
        int estado;
    //Sentencia sql
        String sql = "SELECT id_pedido,fecha,estado FROM pedido WHERE id_cliente = ?";
    //Abre try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        idPedido = resultado.getInt("id_pedido");
                        fecha = resultado.getDate("fecha").toLocalDate();
                        estado = resultado.getInt("estado");
                        lista.add(
                            new Historial(idPedido, fecha, estado)
                        );
                    }
                }//Ciera segundo try ResultSet
        }//Ciera primer try
    }

//Metodo para definir las columnas con su getter correspondiente
    @FXML 
    private void definirColumnas(){
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
        consultar();
        definirColumnas();
        tbvHistorialPedido.setItems(lista);
    }
    
}
