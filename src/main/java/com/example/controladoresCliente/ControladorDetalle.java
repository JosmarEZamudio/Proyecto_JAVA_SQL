package com.example.controladoresCliente;
//Librerias para trabajar con listas en tablev(c)
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
//Objetos de fxml
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;

public class ControladorDetalle{
//Objetos
    @FXML 
    private TableView<Producto> tbvDetalle;
    @FXML 
    private TableColumn<Producto,String> tbcNombre;
    @FXML
    private TableColumn<Producto,Integer> tbcCantidad;
    @FXML 
    private TableColumn<Producto,Integer> tbcPrecio;

    //Crear lista static
    static ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

//Metodo para agregar elementos a la tabla
    @FXML 
    private void mostrar(){
    //Buscar metodo de retorno para cada tbc
    tbcNombre.setCellValueFactory(
        new PropertyValueFactory<>("nombre")
    );
    tbcCantidad.setCellValueFactory(
        new PropertyValueFactory<>("cantidad")
    );
    tbcPrecio.setCellValueFactory(
        new PropertyValueFactory<>("precio")
    );
    }

//Metodo de agregar a observable list
    public int agregar(int idP, String nombre, int cantidad, int precio){
        int valor;
        //Si existe un producto con el mismo Id, aumentar en uno la cantidad
        for (Producto p : listaProductos){
            if(p.getId()==idP){
                p.setCantidad(p.getCantidad()+1);
                valor = p.getCantidad();
                return valor;
            }
        }
        //Si no existe un producto con el mismo Id en a lista se crea
         listaProductos.add(
            new Producto(idP, nombre, cantidad, precio)
            );
        //Retornara un valor de uno cuando el producto recien se agrege a la lista de detalles
        return 1;
    }
//Metodo de eleminar un producto a la observable list
    public int quitar(int idP, String nombre, int cantidad, int precio){
        int valor;
        //Decrementar la cantidad en uno
        for (Producto p : listaProductos){
            if(p.getId()==idP){
                p.setCantidad(p.getCantidad()-1);
                valor = p.getCantidad();
                return valor;
            }
        }
        //Si falla la busqueda retorna 0
        return 0;
    }
//Metodo de inicializacion 
    //Muestra la tabla con datos
    @FXML 
    private void initialize(){
        mostrar();
        tbvDetalle.setItems(listaProductos);
    }

}
