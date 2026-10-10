package com.example.controladoresRestaurante;
//Librerias para crear listas
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//Objetos de java fxml
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
//Librerias para sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Clases adicionales
import com.example.controladorConector;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRestaurante;

public class ControladorConsultarProducto {
//Objetos button de fxml
    @FXML 
    private Button btnEditar;
    @FXML 
    private Button btnGuardar;
    @FXML 
    private Button btnBuscar;
//Objetos text de fxml
    @FXML 
    private TextField txtEditarNombre;
    @FXML 
    private TextField txtEditarPrecio;
    @FXML 
    private TextField txtBuscar;
//Objetos tableview y table column de fxml
    @FXML 
    private TableView<Producto> tbvProductos;
    @FXML 
    private TableColumn<Producto, Integer> tbcId;
    @FXML
    private TableColumn<Producto, String> tbcNombre;
    @FXML 
    private TableColumn<Producto, Integer> tbcPrecio;
    @FXML 
    private TableColumn<Producto, Integer> tbcDisponibilidad;

//Metodo donde se consulta los productos del restaurante a traves de su id obtenida anteriormente 
    @FXML
    private void consultarProductos() throws SQLException{
    //Crear una lista especial donde contendra cada objeto nuevo
        ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    //Buscar getter correspondiente para cada tbc
        tbcId.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );
        tbcNombre.setCellValueFactory(
            new PropertyValueFactory<>("nombre")
        );
        tbcPrecio.setCellValueFactory(
            new PropertyValueFactory<>("precio")
        );
        tbcDisponibilidad.setCellValueFactory(
            new PropertyValueFactory<>("disponibilidad")
        );
        //Obtener el id del restaurante
        int id = datosSesionRestaurante.getIdRestaurante();
        //Crear sentencia sql
        String campos = "id_producto,nombre,precio,disponibilidad";
        String sql = "SELECT "+campos+" FROM producto WHERE id_restaurante = ?";
        //Declarar variables de entorno para la consulta
        int id_producto;
        int precio;
        int disponibilidad;
        String nombre;
        //Inciar conexion y consulta
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
                consultar.setInt(1, id);
                try(ResultSet resultado = consultar.executeQuery()){
                    while(resultado.next()){
                        id_producto = resultado.getInt("id_producto");
                        nombre = resultado.getString("nombre");
                        precio = resultado.getInt("precio");
                        disponibilidad = resultado.getInt("disponibilidad");
                        listaProductos.add(
                            new Producto(id_producto, nombre, precio, disponibilidad)
                        );
                       // System.out.println(id_producto+","+nombre+","+precio+","+disponibilidad);
                    }
                }//Segundo try cierra ResultSet
        }//Primer try cierra Connection y Prepared
        tbvProductos.setItems(listaProductos);
    }
//Metodo al seleccionar una fila de la tabla traer informacion a un txt
    @FXML 
    private void seleccionarTabla(){
        tbvProductos.getSelectionModel().selectedItemProperty().addListener(
            (observable, anterior, seleccionado) -> {
                if(seleccionado != null){
                   ProductoEditado.almacenarTemporal(seleccionado.getId());
                   txtEditarNombre.setText(seleccionado.getNombre());
                   txtEditarPrecio.setText(String.valueOf(seleccionado.getPrecio()));
                }
            }
        );
    }
//Metodo con el que incializa la interfaz
    @FXML
    public void initialize() throws SQLException{
        try{
        consultarProductos();   
        seleccionarTabla();
        txtEditarNombre.setDisable(true);
        txtEditarPrecio.setDisable(true); 
        btnGuardar.setDisable(true);
        } catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
                "No se pudo conectar con la base, si el error persite contacte a soporte."
            );
        }
    }
//Metodo para editar un registro
    @FXML 
    private void editar(){

        if(txtEditarNombre.getText().isBlank() ||
            txtEditarPrecio.getText().isBlank()){
                dialogosMensajes.mensajeInformacion("Informacion editar", 
                "Seleccione un producto para editar.");
                return;
        }

        txtEditarNombre.setDisable(false);
        txtEditarPrecio.setDisable(false);
        btnGuardar.setDisable(false);
        btnEditar.setDisable(true);
        tbvProductos.setDisable(true);
    }
//Metodo para guardar el registro
    @FXML 
    private void guardar() throws SQLException{
    //Obtiene el id del producto    
        int idProducto = ProductoEditado.getIdProducto();
    //Evalua texts en blanco
        if(txtEditarNombre.getText().isBlank() ||
        txtEditarPrecio.getText().isBlank()){
            dialogosMensajes.mensajeInformacion("Informacion editar", 
            "Falta un dato, revisar y reintentar nuevamente.");
            return;
        }
    //Obtiene las cadenas de los texts
        String nombre = txtEditarNombre.getText();
        String precio = txtEditarPrecio.getText();
    //Sentencia sql 
        String sql = "UPDATE producto SET nombre = ?, precio = ? WHERE id_producto = ?";
    //Empieza conexion y preparacion (editar)
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement editar = conexion.prepareStatement(sql)){
                editar.setString(1, nombre);
                editar.setInt(2, Integer.valueOf(precio));
                editar.setInt(3, idProducto);
                editar.executeUpdate();
        }catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
                "No se pudo editar la informacion, intente de nuevo"
            );
            return;
        }
    //Dialoogo de exito
        dialogosMensajes.mensajeInformacion("Informacion editar", 
            "La informacion fue editada correctamente."
        );
    //Limpiar texts y refrescar tableview
        txtEditarNombre.clear();
        txtEditarPrecio.clear();
        tbvProductos.setDisable(false);
        consultarProductos();
    }

//Metodo para buscar un producto
    @FXML 
    private void buscar() throws SQLException{
    //Inicializar variables
        int id;
        String nombre;
        int precio;
        int disponibilidad;
    //Validar txt vacios
        if(txtBuscar.getText().isBlank()){
            dialogosMensajes.mensajeInformacion("Informacion busqueda", 
            "La busqueda necesita al menos un caracter.");
            return;
        }
    //Obtener texto de txt
        String busqueda = txtBuscar.getText();
    //Crear lista
        ObservableList<Producto> listaProducto = FXCollections.observableArrayList();
    //Prerarar columnas
        tbcId.setCellValueFactory(
            new PropertyValueFactory<>("id")
        );
        tbcNombre.setCellValueFactory(
            new PropertyValueFactory<>("nombre")
        );
        tbcPrecio.setCellValueFactory(
            new PropertyValueFactory<>("precio")
        );
        tbcDisponibilidad.setCellValueFactory(
            new PropertyValueFactory<>("disponibilidad")
        );
    //Sentencia sql
        String campos ="id_producto,nombre,precio,disponibilidad";
        String sql = "SELECT "+campos+" FROM producto WHERE nombre LIKE ?";
    //Conecion y prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement buscar = conexion.prepareStatement(sql)){
                buscar.setString(1, busqueda + "%");
            //ResultSet    
                try(ResultSet resultado = buscar.executeQuery();){
                    while(resultado.next()){
                        id = resultado.getInt("id_producto");
                        nombre = resultado.getString("nombre");
                        precio = resultado.getInt("precio");
                        disponibilidad = resultado.getInt("disponibilidad");
                        System.out.println(id+","+nombre+","+precio+","+disponibilidad);
                        listaProducto.add(
                            new Producto(id, nombre, precio, disponibilidad)
                        );
                    }
                }//Cierra segundo try ResultSet}
            tbvProductos.setItems(listaProducto);
        }//Cierra primer try Connection y prepared
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudo consultar con la base, si el error persiste conatacte a soporte."
            );
            return;
        }
    }
}
