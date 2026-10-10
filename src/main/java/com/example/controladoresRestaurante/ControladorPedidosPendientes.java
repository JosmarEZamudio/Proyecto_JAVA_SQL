package com.example.controladoresRestaurante;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//Librerias objetos fxml
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias, clases, etc adicionales
import com.example.controladorConector;
import com.example.dialogosMensajes;


public class ControladorPedidosPendientes {
//Objeros fxml button 
    @FXML 
    private Button btnBuscar;
    @FXML 
    private TextField txtBuscar;
//Objetos TableColumn y Table view de la primera tabla
    @FXML 
    private TableView<Pendiente> tbvPedido;
    @FXML 
    private TableColumn<Pendiente, Integer> tbcPedidoId;
    @FXML 
    private TableColumn<Pendiente, String> tbcPedidoNombre;
    @FXML 
    private TableColumn<Pendiente, Integer> tbcPedidoEstado;

    ObservableList <Pendiente> listaPendientes = FXCollections.observableArrayList();

//Objetos Tablecolumn y TableViewde la segunda tabla
    


//Metodo para consultar 
    private void consultar() throws SQLException{
    //Variable para la consulta sql
        int valor = 0;
    //Variables donde se guardaran temporalmente los resultados de as consultas
        int id;
        String nombre;
        int estado;
    //Sentencia sql
        String sql = """
                SELECT pedido.id_pedido, cliente.nombre, pedido.estado FROM pedido JOIN cliente
                ON pedido.id_cliente = cliente.id_cliente
                WHERE pedido.estado = ?
                """;
    //Empieza try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, valor);
            //Empieza segundo try
                try(ResultSet resultado = consultar.executeQuery();){

                while(resultado.next()){
                //Guardar resultados temporalemente y pasarlos
                    id = resultado.getInt("id_pedido");
                    nombre = resultado.getString("nombre");
                    estado = resultado.getInt("estado");
                //Crear objeto nuevo 
                    listaPendientes.add(  
                        new Pendiente(id,nombre,estado)
                    ); }
                }//Cierra segundo try ResultSet
        }//Ciera primer try 
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo realizar la consulta, intente de nuevo.");
            return;
        }
    }

//Metodo para asignar getters 
    @FXML 
    private void asignar(){
        tbcPedidoId.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );
        tbcPedidoNombre.setCellValueFactory(
            new PropertyValueFactory<>("nombre")
        );
        tbcPedidoEstado.setCellValueFactory(
            new PropertyValueFactory<>("estado")
        );
    }

//Metodo de inicializacion
    @FXML 
    private void initialize() throws SQLException{
        asignar();
        consultar();
        tbvPedido.setItems(listaPendientes);
    }

}