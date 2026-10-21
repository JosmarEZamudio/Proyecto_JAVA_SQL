package com.example.controladoresCliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//Librerias fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
//Librerias de geoemetria
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
//Librerias para trabajar con ventanas
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
//Librerias de sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//Librerias adicionales, clases, etcerta
import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionCliente;
import com.example.controladoresSimulacion.ControladorUbicaciones;
//Librerias para fechas, archivos
import java.time.LocalDate;
import java.io.File;
import java.io.IOException;

public class ControladorCompraProducto {
    ControladorDetalle lista = new ControladorDetalle();
//Objetos de fxml
    @FXML 
    private Label lblPrueba1;
    @FXML 
    private Label lblPrueba2;
    @FXML 
    private Label lblAgregado;
    @FXML 
    private Label lblTotal;
    @FXML 
    private GridPane gdpProducto;
    @FXML 
    private ScrollPane scpContenedor;
    @FXML 
    private Button btnRegresar;
    @FXML 
    private Button btnOrdenar; 
    @FXML 
    private Button btnDetalle; 
    @FXML 
    private Pane pnBarra;

//Metodo para consultar productos
    ObservableList <Producto> listaCompra = FXCollections.observableArrayList();
    private void consultarProductos() throws SQLException{
    //Declarar variables donde se guardara el resultado de la consulta
        int idProducto;
        String nombreProducto; 
        int productoPrecio;
        int columna = 0; //Valor de columna donde se posicionara el producto
    //Obtener id de restaurante para desplegar los productos del restaurante seleccionado
        int id = datosSesionCompra.getIdRestaurante();
        int disponibilidad = 1;
    //Sentencia sql
        String sql = "SELECT id_producto,nombre,precio FROM producto WHERE id_restaurante = ? AND disponibilidad = ?";
    //Abre primer try Connection y Prepared  
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                consultar.setInt(2, disponibilidad);
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        idProducto = resultado.getInt("id_producto");
                        nombreProducto = resultado.getString("nombre");
                        productoPrecio = resultado.getInt("precio");
                        crearTarjetas(idProducto, nombreProducto, productoPrecio,columna);
                        columna++;
                    }
                }//Cierra segundo try ResultSet
        }//Cierra primer try
        catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error",
            "Error en cargar las imagenes, intente de nuevo.");
        }
    }

//Metodo para crear tarjetas de los productos
    @FXML 
    private void crearTarjetas(int idP, String nombreProducto, 
        int productoPrecio, int columna) throws IOException{
        //Cagar valor de id_cliente e inicializar variable valor
            int valor = 0;
            int id = datosSesionCompra.getIdRestaurante();
        //Declarar ruta de donde se seleccionara la imagen
            String ruta = "datos/imagenes/"+id+"/"+idP+".jpg";
        //Declarar objetos nuevos
            VBox vbTarjeta = new VBox();
            Label lblNombre = new Label();
            Label lblPrecio = new Label();
            Label lblCantidad = new Label();
            Button btnAgregar = new Button();
            Button btnQuitar = new Button();
        //Cargar imagen 
            Image imagen = new Image(
                new File(ruta).toURI().toString()
            );
            ImageView vista = new ImageView(imagen);
        //****Definicion de objetos, implementacion de botones tamaño y setear****
            //Encontrar el valor de la cantidad para cada idp
            for(Producto p: ControladorDetalle.listaProductos){
                if(p.getId()==idP){
                    valor = p.getCantidad();
                    break;
                }
            }    
        //Si es valor es 0 desactivar botones
            if (valor == 0){
                btnQuitar.setDisable(true);
            }
        //Imprimir valores y setear texto
            lblNombre.setText(nombreProducto);
            lblPrecio.setText("Precio: $"+productoPrecio);
            lblCantidad.setText("Cantidad: "+valor);
            btnAgregar.setText("Agregar");
            btnQuitar.setText("Quitar");
        //Metodo para agregar un producto a la canasta    
            btnAgregar.setOnAction(e->{
            //Se inicializa variable donde guardara el valor de retorno
                int cantidadNueva = 0;
            //Llamar funciones: se crea un elemento en la lista y se agregan datos a la barra 
                cantidadNueva = lista.agregar(idP, nombreProducto, 1, productoPrecio);
                datosSesionCompra.sumaCompra(1, productoPrecio);
            //Imprimir resultados en label
                lblAgregado.setText("Productos agregados: "+datosSesionCompra.getCantidad());
                lblTotal.setText("Total: $"+datosSesionCompra.getTotal());
                lblCantidad.setText("Cantidad: "+cantidadNueva);
            //Desactivar todos los botones quitar  
                btnQuitar.setDisable(false);
                
            });
        //Metodo para quitar un producto de la canasta
            btnQuitar.setOnAction(e->{
            //Se inicializa variable donde guardara el valor de retorno
                int cantidadNueva=0;
            //LLamar funciones, quitar elemento en la lista y quitar datos a la barra
                cantidadNueva = lista.quitar(idP, nombreProducto, id, productoPrecio);
                datosSesionCompra.quitarCompra(1, productoPrecio);
            //Imprimir resultados
                    lblAgregado.setText("Productos agregados: "+datosSesionCompra.getCantidad());
                    lblTotal.setText("Total: $"+datosSesionCompra.getTotal());
                    lblCantidad.setText("Cantidad: "+cantidadNueva);
                    
                    if(cantidadNueva==0){
                        btnQuitar.setDisable(true);
                    }
    
            });
            //Tamaños imageView, vbox setear grid
            vista.setFitHeight(100);
            vista.setFitWidth(100);
            vista.setPreserveRatio(false);
        //Acomodo geometrico de cada VBox 
            vbTarjeta.setSpacing(5);
            vbTarjeta.setAlignment(Pos.CENTER);
            vbTarjeta.getChildren().addAll(lblNombre,vista,lblPrecio,lblCantidad,btnAgregar,btnQuitar);
        //Agregar tarjeta e indicar posicion
            gdpProducto.add(vbTarjeta, columna, 0);
    }

//Metodo para regresar 
    @FXML 
    private void regresar() throws IOException{
        //Cagar archivo fxml
        try{  
            FXMLLoader loaderPrincipal = new FXMLLoader(
                getClass().getResource("/com/example/interfazcliente/interfazPrincipal.fxml")
            );
        //Obtener raiz de la interfaz que es de tipo Pane y obtener escena actual
            Pane principal = loaderPrincipal.load();
            Scene escenaActual = btnRegresar.getScene();
        //Buscar contenedor principal y setear
            ScrollPane contenedor = (ScrollPane) escenaActual.lookup("#contenedorprincipal");
            contenedor.setContent(principal);
        }catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar la interfaz, intente de nuevo.");
            return;
        }
    }

//Metodo para ver detalle 
    @FXML 
    private void detalle() throws IOException{
        try{ //Cargar archivo fxml
            FXMLLoader loaderDetalle = new FXMLLoader(
                getClass().getResource("/com/example/interfazcliente/interfazDetalle.fxml")
            );
        //Cargar fxml a scene y crear scene, stage nuevas
            Scene escenaNueva = new Scene(loaderDetalle.load());
            Stage ventanaNueva = new Stage();
            ventanaNueva.setTitle("Detalle pedido");
            ventanaNueva.setScene(escenaNueva);
            ventanaNueva.setResizable(false);
            ventanaNueva.show();
        }catch(IOException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo cargar el detalle del pedido, intente de neuvo.");
            return;
        }
    }

//Metodo para ordenar 
    @FXML 
    private void agregar() throws SQLException{
    //Cargar datos que utilizaran para la insersion
        int idCliente = datosSesionCliente.getIdCliente();
        int estado = 0;
        LocalDate fecha = LocalDate.now();
        int idPedido;
    //Sentencia sql
        String sql = """
            INSERT INTO pedido(id_cliente,estado,fecha,id_ubicacionCliente) 
            VALUES(?,?,?,?)
        """;
        Connection conexion = null;
    //Abrir primer try Connection y Prepared
        try{
            conexion = controladorConector.conectar();
            conexion.setAutoCommit(false);
            PreparedStatement agregar = conexion.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
                agregar.setInt(1, idCliente);
                agregar.setInt(2, estado);
                agregar.setDate(3,java.sql.Date.valueOf(fecha)); 
                agregar.setInt(4, datosSesionCliente.getIdubicacion());
                agregar.executeUpdate();
            //Empieza segundo try
                try(ResultSet resultado = agregar.getGeneratedKeys();){
                    if(resultado.next()){
                        idPedido = resultado.getInt(1);
                        datosSesionCompra.idpedido(idPedido); //Guardar id pedido en el metodo estatico
                        ControladorUbicaciones.setIdPedido(idPedido); //Guardar el id del pedido
                    }
                }//Cierra segundo try
                agregarDetalle(conexion);
                conexion.commit();
                //Mensaje de confirmacion
                String titulo = "Mensaje confirmacion";
                String mensaje = "Confirmar orden: No se puede editar despues de ordenar.";
                if(dialogosMensajes.mensajeConfirmacion(titulo, mensaje) == true){
                    dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                    "Gracias por su compra, puede ver el estado de su pedido en la pagina principal.");
                    //pnBarra.setVisible(false);
                    datosSesionCompra.inicializarCompra(); 
                      /////*** IMPORTANTE PONER FUNCION REGRESAR      
                }
        }//Cierra try primer try
        catch(SQLException e){
            if(conexion!=null){
                conexion.rollback();
            }
            dialogosMensajes.mensajeError("Informacion error",
            "No se pudo ordenar, intentar de nuevo, en caso de persistir con el error contacte a soporte.");
            return;
        }//Cierra catch
        finally{
            if(conexion != null){
                conexion.close();
            }
        }//Ciera finally
    }

//Metodo insertar detallePedido
    private void agregarDetalle(Connection conexion) throws SQLException{
    //Obtener valor id_pedido de la clase estatica datosSesionCompra
        int idPedido = datosSesionCompra.getIdPedido();
    //Setencia sql
        String campos = "cantidad,precio_unitario,id_pedido,id_producto";
        String sql= "INSERT INTO detallePedido("+campos+") VALUES (?,?,?,?)";
    //Inciar try Prepared
        try(PreparedStatement agregar = conexion.prepareStatement(sql);){
            for(Producto p : ControladorDetalle.listaProductos){
                if(p.getCantidad()!=0){
                    agregar.setInt(1, p.getCantidad());
                    agregar.setInt(2, p.getPrecio());
                    agregar.setInt(3, idPedido);
                    agregar.setInt(4,p.getId());

                    agregar.addBatch();
                }//Ciera if
                agregar.executeBatch();     
            }//Cierra for
        }//Ciera try
    }

//Metodo de inicializacion 
    @FXML 
    private void initialize() throws SQLException{
    //Dar valores de geomtria para el gridpane del contenedor
        gdpProducto.setHgap(30);
        gdpProducto.setPadding(new Insets(115, 25, 0, 25));
    //Atrapar error de SQl  
        try{
            consultarProductos();;
        }
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo realizar la consulta, intente de nuevo.");
        }
    //Imprimir valores guardados en las clases Producto y datosSesionCompra  
        lblPrueba1.setText("Id restaurante: "+datosSesionCompra.getIdRestaurante());
        lblPrueba2.setText("Nombre restuarante: "+datosSesionCompra.getNombreRestaurante());
        lblAgregado.setText("Productos agregados: "+datosSesionCompra.getCantidad());
        lblTotal.setText("Total: $"+datosSesionCompra.getTotal());
    }
    
}
