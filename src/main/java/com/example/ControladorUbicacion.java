package com.example;
//Librerias para objetos fxml
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.web.WebView;
//Librerias para trabajar con ventanas
import javafx.stage.Stage;
//Clases adicionales
import com.example.controladorSesion.datosSesionCliente;
import com.example.controladorSesion.datosSesionRestaurante;
//Liberias para trabajar con URL
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//Conexiones para el servicio de mapa
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import netscape.javascript.JSObject;
//Librerias sql
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;



public class ControladorUbicacion {
//Botones fxml
    @FXML 
    private Button btnSeleccionar;
    @FXML 
    private Button btnCancelar;
    @FXML 
    private Button btnBuscar;
//Label fxml
    @FXML 
    private Label lblLatitud;
    @FXML 
    private Label lblLongitud;
//Text fxml
    @FXML 
    private TextField txtBuscar;
    @FXML 
    private TextField txtNumero;
//Webview
    @FXML 
    private WebView wvMapa;
//ComboBox
    @FXML 
    private ComboBox<Ubicacion> cmbUbicaciones;
//ListView fxml
    @FXML 
    private ListView<UbicacionesResultado> lvMapa;

//Metodo de busqueda
    @FXML
    private void buscarUbicaciones() throws SQLException{

        String busqueda = txtBuscar.getText().trim();
    
        if (busqueda.isEmpty()) {
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                "Ingrese una ubicacion para buscar!!");
            return;
        }

        try {
            String consulta = URLEncoder.encode(
                busqueda + ", Nuevo León, México",
                StandardCharsets.UTF_8
            );

            String url = "https://nominatim.openstreetmap.org/search"
                + "?format=jsonv2"
                + "&addressdetails=1"
                + "&limit=8"
                + "&countrycodes=mx"
                + "&viewbox=-100.43,25.87,-100.30,25.76"
                + "&bounded=0"
                + "&q=" + consulta;

            HttpClient cliente = HttpClient.newHttpClient();

            HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(
                    "User-Agent",
                    "SistemaGestionPedidosEntregas/1.0"
                )
                .header("Accept", "application/json")
                .GET()
                .build();

            CompletableFuture.supplyAsync(() -> {
                try {
                    return cliente.send(
                        solicitud,
                        HttpResponse.BodyHandlers.ofString()
                    );
                } catch (IOException | InterruptedException e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    throw new RuntimeException(e);
                }
            }).thenAccept(respuesta -> {

                if (respuesta.statusCode() != 200) {
                    throw new RuntimeException(
                        "Error HTTP: " + respuesta.statusCode()
                    );
                }

                JsonArray resultados = JsonParser
                    .parseString(respuesta.body())
                    .getAsJsonArray();

                Platform.runLater(() -> {
                    lvMapa.getItems().clear();
                    lvMapa.setPlaceholder(
                        new Label("No se encontraron ubicaciones.")
                    );

                    for (int i = 0; i < resultados.size(); i++) {
                        JsonObject lugar = resultados.get(i)
                            .getAsJsonObject();

                        String nombre = lugar.get("display_name")
                            .getAsString();

                        double latitud = Double.parseDouble(
                            lugar.get("lat").getAsString()
                        );

                        double longitud = Double.parseDouble(
                            lugar.get("lon").getAsString()
                        );

                        lvMapa.getItems().add(
                            new UbicacionesResultado(
                                nombre, latitud, longitud
                            )
                        );
                    }
                });

            }).exceptionally(error -> {
                Platform.runLater(() -> {
                    lvMapa.getItems().clear();

                    lvMapa.setPlaceholder(
                        new Label(
                            "Error de conexión. Intenta nuevamente."
                        )
                    );
                });

                error.printStackTrace();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   

//Metodo de seleccion para guardar variables de la direccion 
    @FXML 
    private void seleccionDireccion(){
        UbicacionesResultado ubicacion = lvMapa.getSelectionModel().getSelectedItem();
    //Validar seleccion en blanco o nulla
        if(ubicacion == null){
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Selecciona una direcion.");
            return;
        }
    //Validar txt numero en blanco
        if (txtNumero.getText().isEmpty()) {
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
                "Ingrese un numero");
            return;
        }

    //Obtener numero
        String numero = txtNumero.getText().trim();
    //Guardar variables
        String nombre = ubicacion.getNombre();
        double latitud = ubicacion.getLatitud();
        double longitud = ubicacion.getLongitud();

    //Guardar direccion completa
        String direccionCompleta = nombre.replaceFirst(
            "^([^,]+)",
            "$1 " + java.util.regex.Matcher.quoteReplacement(numero)
        );
    //Inicia try
        try{
            guardarUbicacion(latitud, longitud, direccionCompleta);
        }
        catch(SQLException e){
            return;
        }finally{
        //Si se inicia sesion como restaurante 
            if(datosSesionRestaurante.getIdRestaurante()!=null){
                datosSesionRestaurante.guardarUbicacion(latitud, longitud,nombre);
            }
        //Si se inicia sesion como cliente
            if(datosSesionCliente.getIdCliente()!=null){
                datosSesionCliente.ubicacion(latitud, longitud, nombre);
            }
        } //Termina T-C-F
    //Mensaje de informacion
        dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Ubicacion guardada con exito.");
    //Cerar ventana actual
        Stage ventanaActual = (Stage) btnSeleccionar.getScene().getWindow();
        ventanaActual.close();
    }

//Metodo para escoger direccion a traves del combbox
    @FXML 
    private void direccionSeleccion() throws SQLException{
    //Obtener el tipo de usuario
        Integer idCliente = datosSesionCliente.getIdCliente();
        Integer idRestaurante = datosSesionRestaurante.getIdRestaurante();
    //Obtener cadena de combox
        Ubicacion seleccionado = cmbUbicaciones.getValue();
        String direccion = null;

        if(seleccionado!=null &&  idRestaurante!=null){
            direccion = seleccionado.getDireccion();
            Integer id = seleccionado.getId();
            System.out.println(id);
            datosSesionRestaurante.idUbicacion(id);
            cambiarActivo(1);
        }
        if(seleccionado!=null && idCliente!=null){
            direccion = seleccionado.getDireccion();
            Integer id = seleccionado.getId();
            System.out.println(id);
            datosSesionCliente.idUbicacion(id);
        }
        Double longitud = null;
        Double latitud = null;
    //Sentencia sql
        String sqlUbicacion = """
            SELECT coordenada_y,coordenada_x
            FROM ubicacion
            WHERE direccion = ? 
            AND (id_cliente = ? OR id_restaurante = ?)
        """;
    //Abrir try Connection y prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sqlUbicacion);){
                consultar.setString(1, direccion);
            //Si existe el id del cliente
                if(idCliente!=null){
                    consultar.setInt(2, idCliente);
                }
                else{
                    consultar.setNull(2, java.sql.Types.INTEGER);
                }
            //Si existe el id del restaurante
                if(idRestaurante!=null){
                    consultar.setInt(3, idRestaurante);
                }
                else{
                    consultar.setNull(3, java.sql.Types.INTEGER);
                }

            //Abrir segundo try 
                try(ResultSet resultado = consultar.executeQuery();){
                    if(resultado.next()){
                    //Almacenar resultados
                        longitud = resultado.getDouble(1);
                        latitud = resultado.getDouble(2);
                }
            }//Cerrar segundo try
        }//Cerrar primer try
        catch(SQLException e){
            dialogosMensajes.mensajeError("Informacion error", 
            "No se pudieron obtener las coordenadas.");
            return;
        }
    //Validar si inicio se sesion un cliente
        if(idCliente!=null){
        //Mostrar mensajes
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Ubicacion seleccionada!!");
        //Pasar parametros
            datosSesionCliente.ubicacion(latitud, longitud, direccion);
        }
    //Validar si inicio sesion un negocio
        if(idRestaurante!=null){
        //Mostrar mensajes
            dialogosMensajes.mensajeInformacion("Mensaje informacion", 
            "Ubicacion seleccionada!!");
        //Pasar parametros
            datosSesionRestaurante.guardarUbicacion(latitud, longitud, direccion);
        }

    //Cerrar ventana
        Stage ventanaActual = (Stage) cmbUbicaciones.getScene().getWindow();
        ventanaActual.close();
    }

//Metodo para mostrar latitud y longitud a traves de seccion 
    //Evento tipo mouse
    @FXML 
    private void mostrarCoordenadas(){
    //
        UbicacionesResultado ubicacion = lvMapa.getSelectionModel().getSelectedItem();
    //Almacenar variables de la direccion seleccionada
        Double longitud = ubicacion.getLongitud();
        Double latitud = ubicacion.getLatitud();
    //Si ubicacion es diferente de null
        if(ubicacion != null){

            lblLatitud.setText(String.format("Latitud: %.7f",latitud));
            lblLongitud.setText(String.format("Longitud: %.7f",longitud));
            
            wvMapa.getEngine().executeScript(
                "mapa.setView([" + latitud + ", " + longitud + "], 17);" +
                "if (marcador) {" +
                "  marcador.setLatLng([" + latitud + ", " + longitud + "]);" +
                "} else {" +
                "  marcador = L.marker([" + latitud + ", " + longitud + "]).addTo(mapa);" +
                "}"
            );
        }//Termina if
    }

//Metodo para guardar ubicacion como cliente o negocio
    private void guardarUbicacion(double x, double y, String nombre) throws SQLException{
    //Setenecia para obtener los id
        Integer idCliente = datosSesionCliente.getIdCliente();
        Integer idRestaurante = datosSesionRestaurante.getIdRestaurante();

    //Setencia sql
        String sql = """
            INSERT INTO ubicacion (direccion,coordenada_x,coordenada_y,id_cliente,id_restaurante,activo)
            VALUES (?,?,?,?,?,?)
        """;
    //Abre try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement insertar = conexion.prepareStatement(sql);){
                insertar.setString(1, nombre);
                insertar.setDouble(2, x);
                insertar.setDouble(3, y);
            //Validar si existe un cliente
                if(idCliente != null){
                    insertar.setInt(4, idCliente);
                }
                else{
                    insertar.setNull(4, java.sql.Types.INTEGER);
                }
            //Validar si existe un restaurante
                if(idRestaurante!=null){
                    insertar.setInt(5, idRestaurante);
                }
                else{
                    insertar.setNull(5, java.sql.Types.INTEGER);
                }
                insertar.setInt(6, 1);
                insertar.executeUpdate();
            }//Cierra try
    }

//Metodo para cambiar valor: 0 de ubicacion activa para negocios
    @FXML 
    private void cambiarDesactivo(int activo) throws SQLException{
        String sql = """
            UPDATE ubicacion
            SET activo = ?
            WHERE id_restaurante = ?
        """;
        //Abre try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement insertar = conexion.prepareStatement(sql);){
                insertar.setInt(1, activo);
                insertar.setInt(2, datosSesionRestaurante.getIdRestaurante());
                insertar.executeUpdate();
        }
    }

//Metodo para cambiar valor: 1 de ubicacion activa para negocios
    @FXML 
    private void cambiarActivo(int activo) throws SQLException{
        String sql = """
            UPDATE ubicacion
            SET activo = ?
            WHERE id_ubicacion = ?
        """;
        //Abre try Connection y Prepared
        try(Connection conexion = controladorConector.conectar();
            PreparedStatement insertar = conexion.prepareStatement(sql);){
                insertar.setInt(1, activo);
                insertar.setInt(2, datosSesionRestaurante.getIdUbicacion());
                insertar.executeUpdate();
        }
    }

//Metodo para salir 
    @FXML 
    private void cancelar() throws SQLException{
    //Obtener ventana nueva
        Stage ventanaNueva = (Stage) btnCancelar.getScene().getWindow();
    //Declarar variable booleana
        boolean resultado;
    //Mostrar mensaje y obtener retorno
        resultado = dialogosMensajes.mensajeConfirmacion("Confirmar", 
        "No ingreso una ubicacion, ¿Desea regresar?");

    //Cancelar
        if(resultado==true){
            ventanaNueva.close();
        }
    }

//Metodo para mostrar ubicaciones en el combo box
    @FXML 
    private void ubicacionesGuardadas() throws SQLException{
    //Variable donde se guarda la direccion 
        String nombre = "";
        Integer idUbicacion = null;
        Double latitud = null;
        Double longitud = null;
    //Obtener valores de clausula de sql
        Integer idCliente = datosSesionCliente.getIdCliente();
        Integer idRestaurante = datosSesionRestaurante.getIdRestaurante();
    //Sentencia sql 
        String sql = """
            SELECT direccion,id_ubicacion 
            FROM ubicacion
            WHERE id_cliente = ? OR id_restaurante = ?
        """;

        try(Connection conexion = controladorConector.conectar();
            PreparedStatement consultar = conexion.prepareStatement(sql);){
            //Validar si idCliente contiene un valor
                if(idCliente != null){
                    consultar.setInt(1, idCliente);
                }
                else{
                    consultar.setNull(1, Types.INTEGER);
                }

            //Validar si idRestaurante contiene un valor 
                if(idRestaurante!=null){
                    consultar.setInt(2, idRestaurante);
                }
                else{
                    consultar.setNull(2, Types.INTEGER);
                }
            //Obtener resultados 
                try(ResultSet resultado = consultar.executeQuery();){
                    while(resultado.next()){
                        nombre = resultado.getString(1);
                        idUbicacion = resultado.getInt(2);

                        cmbUbicaciones.getItems().add(
                            new Ubicacion(idUbicacion, nombre)
                        );

                    }
                }//Cierra try ResultSet
            }//Ciera try Connection y Prepared
        //Guardar ubicacion de la sesion
            if(nombre!=null && latitud !=null & longitud!=null){
                datosSesionRestaurante.guardarUbicacion(latitud, longitud, nombre);
            }
    }

//Metodo para buscar por medio del enter presionado
    @FXML 
    private void presionarEnter(KeyEvent event) throws SQLException{
    //Evaluar evento recibido
        if(event.getCode()== KeyCode.ENTER){
            //Empieza try
            try{
                buscarUbicaciones();
            }
            catch(SQLException e){
                return;
            }//Termina try catch
        }//Termina if
    }

//Almacenara coordendas recibidas
    public void recibirCoordenadas(Double latitud, Double longitud) {
        lblLatitud.setText(String.format("Latitud: %.7f", latitud));
        lblLongitud.setText(String.format("Longitud: %.7f",longitud));
    }


//Metodo de inicializacion
    @FXML 
    private void initialize() throws SQLException{
    //Mostrar webView
        wvMapa.getEngine().load(
            "https://josmarezamudio.github.io/Selector_Ubicacion/"
        );
    //Esperar a que cargue la pagina web
        // Esperar a que cargue la página web
        wvMapa.getEngine().getLoadWorker().stateProperty().addListener(
            (observable, anterior, nuevo) -> {
                if (nuevo == javafx.concurrent.Worker.State.SUCCEEDED) {

                    JSObject window = (JSObject) wvMapa.getEngine()
                        .executeScript("window");

                    window.setMember("javaBridgeReceptor", this);

                    System.out.println("Puente Java-JavaScript conectado");
                }
            }
        );
    //Mostrar mensaje en ListView
        lvMapa.setPlaceholder(
            new Label("Escribe una ubicacion y presiona buscar o presione enter.")
        );
        //Empieza try
        try{
            ubicacionesGuardadas();
        }
        catch(SQLException e){
            dialogosMensajes.mensajeError("Mensaje error", 
            "No se pudo cargar las ubicaciones guardadas.");
        } 
        //Termina try
    }

}
