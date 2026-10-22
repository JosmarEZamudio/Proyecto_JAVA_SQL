package com.example.controladoresRestaurante;
//Importar librerias de fxml
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
//Importar librerrias de sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//Importar librerias para ventanas
import javafx.stage.FileChooser;
//Importar clases
import com.example.controladorConectorPool;
import com.example.dialogosMensajes;
import com.example.controladorSesion.datosSesionRestaurante;
//Import librerias para archivos
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ControladorAgregarProducto {
//Para toda la clase
    public static File archivo;
//Elementos de fxml
    @FXML 
    private Button btnBuscar;
    @FXML 
    private Button btnAgregar; 
    @FXML 
    private TextField txtNombre;
    @FXML 
    private TextField txtPrecio;
    @FXML 
    private TextField txtImagen;
//Metodo para obtener el ultimo producto registrado y asi, agregar la imagen correspondiente
//a su carpeta de iamgenes, despues de que se agregaron los registros
    @FXML 
    private void agregarProducto() throws Exception{
    //Obtener id del restaurante
        int idRestaurante = datosSesionRestaurante.getIdRestaurante();
    //Metodos
        agregarDatosSql();
    //Obtener llave generada despues de ejecutar la actualizacion
        int idProducto = ProductoEditado.getIdProducto();
    //Copiar imagen seleccionada en la propia carpeta del restaurante
        String nombreImagen = archivo.getName();
        int punto = nombreImagen.lastIndexOf('.');
        String extension = nombreImagen.substring(punto);
        
        Path carpeta = Paths.get("datos","imagenes", String.valueOf(idRestaurante));
        Files.createDirectories(carpeta);
            
        Path destino = carpeta.resolve(String.valueOf(idProducto)+extension);
        Files.copy(archivo.toPath(),destino,StandardCopyOption.REPLACE_EXISTING);

        dialogosMensajes.mensajeInformacion("Informacion registro", 
        "Su producto fue agregado exitosamente");
        txtImagen.clear();
        txtNombre.clear();
        txtPrecio.clear();
    }

//Metodo para agregar datos del producto a sql
    @FXML 
    private void agregarDatosSql()throws SQLException{
    //Validacion txt vacios
        if(txtNombre.getText().isBlank() ||
            txtPrecio.getText().isBlank()){
                dialogosMensajes.mensajeInformacion("Informacion Registro", 
                "Hay datos en blanco, agregar los datos faltantes.");
                return;
            }
    //Atributos que se agregaran a la base
        int idRestaurante = datosSesionRestaurante.getIdRestaurante();
        String nombre = txtNombre.getText();
        int precio =  Integer.valueOf(txtPrecio.getText());
        int disponibilidad = 0;
    //Sentencia sql
        String sql = "INSERT INTO producto(nombre,precio,id_restaurante,disponibilidad)" 
                    +" VALUES(?,?,?,?)";
    //Realizar un TWR, en caso de falla
        try{
            Connection conexion = controladorConectorPool.conectar();
                conexion.setAutoCommit(false);
            PreparedStatement agregar = conexion.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);
                agregar.setString(1, nombre);
                agregar.setInt(2, precio);
                agregar.setInt(3, idRestaurante);
                agregar.setInt(4, disponibilidad);
                agregar.executeUpdate();

                try(ResultSet resultado = agregar.getGeneratedKeys();){
                    if(resultado.next()){
                        int id = resultado.getInt(1);
                        ProductoEditado.almacenarTemporal(id);
                    }
                    conexion.commit();
                }catch(SQLException e){
                    if(conexion!=null){
                        conexion.rollback();
                        throw e; }//Termina if
            }//Termina segundo try
        }catch(SQLException e){
            dialogosMensajes.mensajeInformacion("Informacion error", 
            "No se pudo guardar el registro.");
        }//Termina primer try
    }
    

//Metodo para escoger imagen: Se abre un dialogo de archivo donde se seleccionara la imagen, 
//y su direccion se copiara a un text, todo esto antes de crear la copia de la imagen a la 
//carpeta del negocio
    @FXML 
    private void agregarImagen(){
    //Crear una ventana para escoger archivos
        FileChooser selector = new FileChooser();
        selector.setTitle("Escoger imagen");
        selector.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Imagenes", "*.jpg","*.jpeg","*.png")
        );
    //Archivo guarda la imagen, como es static puede ser usado en cualquier parte de la clase
        archivo = selector.showOpenDialog(null);
    //Imprimir direccion del archivo en el txt
        if (archivo != null){
            txtImagen.setText(archivo.getAbsolutePath());
        }
    }

//Metodo incial para agregar producto: no se usara este txt, se desactiva
    @FXML 
    private void initialize(){
        txtImagen.setDisable(true);
    }
   
    
}
