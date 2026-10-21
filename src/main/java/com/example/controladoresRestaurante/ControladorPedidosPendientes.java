package com.example.controladoresRestaurante;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//Librerias objetos fxml
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Librerias, clases, etc adicionales
import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorPrincipal;
import com.example.controladorSesion.datosSesionRestaurante;
import com.example.controladoresSimulacion.ControladorAsignarRepartidor;
import com.example.controladoresSimulacion.ControladorUbicaciones;


public class ControladorPedidosPendientes {
//Cargar interfaz
    private controladorPrincipal principal;
//Objeros fxml button 
    @FXML 
    private Button btnBuscar;
    @FXML 
    private TextField txtBuscar;
    @FXML 
    private Button btnListo;
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
    @FXML 
    private TableView<Detalle> tbvDetalle;
    @FXML 
    private TableColumn<Detalle, Integer> tbcDetalleId;
    @FXML 
    private TableColumn<Detalle, String> tbcDetalleNombre;
    @FXML 
    private TableColumn<Detalle, String> tbcDetalleProducto;
    @FXML 
    private TableColumn<Detalle, Integer> tbcDetalleCantidad;

    ObservableList<Detalle> listaDetalle = FXCollections.observableArrayList();

    private int idPublica=0;

//Metodo para consultar pedidos pendientes
    private void consultarPendiente() throws SQLException{
    //Variable para la consulta sql
        int idRestaurante = datosSesionRestaurante.getIdRestaurante();
    //Variables donde se guardaran temporalmente los resultados de as consultas
        int idPedido;
        int idCliente;
        String nombre;
        int estado;
    //Sentencia sql
        String sql = """
                SELECT pedido.id_pedido, pedido.id_cliente,cliente.nombre, pedido.estado FROM cliente JOIN pedido
                ON pedido.id_cliente = cliente.id_cliente
                JOIN detallePedido 
                ON pedido.id_pedido = detallePedido.id_pedido
                JOIN producto 
                ON detallePedido.id_producto = producto.id_producto
                JOIN restaurante 
                ON producto.id_restaurante = restaurante.id_restaurante
                WHERE pedido.estado = ? AND restaurante.id_restaurante = ?
                GROUP BY id_pedido
                """;
    //Empieza try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, 0);
                consultar.setInt(2, idRestaurante);
            //Empieza segundo try
                try(ResultSet resultado = consultar.executeQuery();){

                while(resultado.next()){
                //Guardar resultados temporalemente y pasarlos
                    idPedido = resultado.getInt("id_pedido");
                    idCliente = resultado.getInt("id_cliente");
                    nombre = resultado.getString("nombre");
                    estado = resultado.getInt("estado");
                //Crear objeto nuevo 
                    listaPendientes.add(  
                        new Pendiente(idPedido,idCliente,nombre,estado)
                    ); }
                }//Cierra segundo try ResultSet
        }//Ciera primer try 
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo realizar la consulta, intente de nuevo.");
            return;
        }
    }
          

//Metodo para ir poner pedido como listo 
    @FXML 
    private void pedidoListo() throws SQLException{
        String sql = "UPDATE pedido SET estado = ? WHERE id_pedido = ?";

        try(Connection conexion = controladorConector.conectar();
            PreparedStatement editar = conexion.prepareStatement(sql);){
                editar.setInt(1, 1);
                editar.setInt(2, idPublica);

                editar.executeUpdate();

                dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                "Estado actualizado");
                
                tbvPedido.getItems().clear();
                asignarPendiente();
                consultarPendiente();
                tbvPedido.setItems(listaPendientes);
        }
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo actualizar el estado, intente de nuevo");
        }
    //Obtener ubicaciones del cliente y del negocio para enviarlos a la simulacion
        consultarUbicacionCliente();
        ControladorUbicaciones.setUbicacionNegocio(datosSesionRestaurante.getCoordenadaX(),
                datosSesionRestaurante.getCoordenadaY(), datosSesionRestaurante.getDireccion());
    //Empieza simulacion 
        ControladorAsignarRepartidor asignar = new ControladorAsignarRepartidor();
        asignar.repartidorCercano();
    
        principal.iniciarSimulacion();
       
    //Imprime coordenadas para validar
    
        System.out.println(ControladorUbicaciones.getCoordenadaXCliente()+" , "
        +ControladorUbicaciones.getCoordenadaYCliente());

        System.out.println(ControladorUbicaciones.getCoordenadaXNegocio()+" , "
        +ControladorUbicaciones.getCoordenadaYNegocio());

        System.out.println(ControladorUbicaciones.getCoordenadaYRepartidor()+" , "
        +ControladorUbicaciones.getCoordenadaXRepartidor());
    }

//Metodo para obtener la direcion del cliente 
    private void consultarUbicacionCliente() throws SQLException{
    //Sentencia sql
        String sql = """
            SELECT ubicacion.coordenada_x, ubicacion.coordenada_y, ubicacion.direccion, cliente.id_cliente
            FROM pedido JOIN cliente
            ON pedido.id_cliente = cliente.id_cliente
            JOIN ubicacion
            ON cliente.id_cliente = ubicacion.id_cliente 
            WHERE pedido.estado = ? AND pedido.id_pedido = ? AND ubicacion.activo = ?
        """;
    //Incia try Connection y prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, 1);
                consultar.setInt(2, idPublica);
                consultar.setInt(3, 1);
            //Inicia try ResultSet
                try(ResultSet resultado = consultar.executeQuery();){
                    if(resultado.next()){
                        Double x = resultado.getDouble(1);
                        Double y = resultado.getDouble(2);
                        String direccion = resultado.getString(3);
                    //Enviar ubicacion del cliente a la simulacion
                        ControladorUbicaciones.setUbicacionCliente(x, y,direccion);
                    }
                }//Termina segundo try
        }//Termina primer try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo obtener la direccion del cliente.");
        }
    }      

//Metodo para consultar el detalle de pedidos pendientes mediante seleccion en table
    @FXML 
    private void detallePendienteSeleccionar() throws SQLException{
    //Metodo lamda
        tbvPedido.getSelectionModel().selectedItemProperty().addListener(
            (observable,anterior,seleccionado) -> {
            //Limpiar antes de cada busqueda
                tbvDetalle.getItems().clear();
            //Obtener valores idCliente
                int idPedido=0;
                int idRestaurante = datosSesionRestaurante.getIdRestaurante();
                if(seleccionado != null){
                    idPedido = seleccionado.getId();
                    idPublica = seleccionado.getId();
                    System.out.println(idPublica);
                }else{
                    System.out.println("no "+idPublica);
                    return;
                }
            //Llamar columnas
                asignarDetalle();
            //Abre try
                try{
                    consultarDetallePendiente(idRestaurante, idPedido);
                    ControladorUbicaciones.setIdPedido(idPublica);
                    tbvDetalle.setItems(listaDetalle);
                }
                catch(SQLException e){
                    dialogosMensajes.mensajeInformacion("Informacion error", 
                    "No se pudo realizar la consulta, intente de nuevo.");
                    return;
                }//Ciera try
            }//Ciera metodo lamda
        );//Ciera argumento de tbv
    }

//Metodo para consultar el detalle de pedidos pendientes mediante el boton buscar
    @FXML 
    private void detallePendienteBuscar() throws SQLException{
    //Limpiar antes de cada busqueda
        tbvDetalle.getItems().clear();
    //Validar txt vacio
        if (txtBuscar.getText().isBlank()){
            dialogosMensajes.mensajeInformacion("Informacion", 
            "Ingrese un id.");
            return;
        }
    //Obtener valores de id restaurante y pedido para realizar la consulta
        int idRestaurante = datosSesionRestaurante.getIdRestaurante();
        int idPedido = Integer.valueOf(txtBuscar.getText());
    //LLamar columnas, consultas y setear tbv
        asignarDetalle();
        try{
            consultarDetallePendiente(idRestaurante, idPedido);
        }catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo realizar la consulta, intente de nuevo.");
            return;
        }
        tbvDetalle.setItems(listaDetalle);
    }

//Metodo para consultar el detalle del pedido del cliente seleccionado o buscado
    private void consultarDetallePendiente(int idRestaurante, int idPedido) throws SQLException{
    //Guardar temporalmente los resultados en estas variables
        int idPedidoConsulta;
        String nombre;
        int cantidad;
        String nombreProducto;

    //Sentencia sql
        String sql = """
                SELECT p.id_pedido, c.nombre, d.cantidad,pr.nombre AS producto FROM cliente AS c JOIN pedido AS p
                ON p.id_cliente = c.id_cliente
                JOIN detallePedido AS d
                ON p.id_pedido = d.id_pedido
                JOIN producto AS pr
                ON pr.id_producto = d.id_producto
                WHERE p.id_pedido= ? AND pr.id_restaurante = ?
                ORDER BY p.id_pedido ASC
                """;
    //Primera try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
        PreparedStatement consultar = conexion.prepareStatement(sql);){
        //
            consultar.setInt(1, idPedido);
            consultar.setInt(2, idRestaurante);
        //Segundo try
            try(ResultSet resultado = consultar.executeQuery();){
            //Mientras resultado tenga filas hacer:
                while(resultado.next()){
                //Obtener valores temporales
                    idPedidoConsulta = resultado.getInt("id_pedido");
                    nombre = resultado.getString("nombre");
                    cantidad = resultado.getInt("cantidad");
                    nombreProducto = resultado.getString("producto");
                //Crear objeto nuevo
                    listaDetalle.add(
                        new Detalle(idPedidoConsulta, nombre, nombreProducto, cantidad)
                    );

                }//Cierra while
            }//Ciera segundo try ResultSet   
        }//Cierra primer try
    }

//Metodo para asignar getters lista pendiente
    @FXML 
    private void asignarPendiente(){
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

//Metodo para asignar getter lista detalle
    @FXML 
    private void asignarDetalle(){
        tbcDetalleId.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );
        tbcDetalleNombre.setCellValueFactory(
            new PropertyValueFactory<>("nombre")
        );
        tbcDetalleProducto.setCellValueFactory(
            new PropertyValueFactory<>("producto")
        );
        tbcDetalleCantidad.setCellValueFactory(
            new PropertyValueFactory<>("cantidad")
        );
    }

//Asingar controlador principal
    public void setControladorPrincipal(controladorPrincipal principal) {
        System.out.println("Setter del menú recibió: " + principal);
        this.principal = principal;
    }

//Metodo de inicializacion
    @FXML 
    private void initialize() throws SQLException{
        asignarPendiente();
        detallePendienteSeleccionar();
        consultarPendiente();
        tbvPedido.setItems(listaPendientes);
        tbvDetalle.setPlaceholder(new Label("Seleccion un pedido para mostrar el detalle."));
    }

}