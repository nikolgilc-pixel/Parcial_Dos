package co.edu.uniquindio.poo.visual;

import co.edu.uniquindio.poo.controller.PrincipalController;
import co.edu.uniquindio.poo.model.Administrador;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Guia;
import co.edu.uniquindio.poo.model.Usuario;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/** Enrutador MVC: muestra el espacio correspondiente al rol autenticado. */
public class VistaPrincipal {
    private final PrincipalController controlador;
    private final Stage ventana;
    public VistaPrincipal(PrincipalController controlador,Stage ventana){this.controlador=controlador;this.ventana=ventana;}
    public void mostrarIngreso(){ventana.setTitle("RutaVerde Quindío · Ingreso");ventana.setScene(new Scene(new VistaLogin(controlador,this::mostrarPanel).crearVista(),1100,740));aplicarTema();ventana.show();}
    private void mostrarPanel(Usuario usuario){
        Parent contenido;
        if(usuario instanceof Cliente cliente)contenido=new VistaCliente(controlador,cliente).crearVista();
        else if(usuario instanceof Guia guia)contenido=new VistaGuia(controlador,guia).crearVista();
        else if(usuario instanceof Administrador admin)contenido=new VistaAdministrador(controlador,admin).crearVista();
        else throw new IllegalStateException("Rol no reconocido");
        Button cerrar=new Button("Cerrar sesión");cerrar.setOnAction(e->mostrarIngreso());Label marca=new Label("RUTAVERDE  ·  QUINDÍO");Label rol=new Label(usuario.obtenerRol());Region espacio=new Region();HBox.setHgrow(espacio,Priority.ALWAYS);
        HBox barra=new HBox(marca,espacio,rol,cerrar);barra.getStyleClass().add("app-topbar");
        BorderPane root=new BorderPane(contenido);root.getStyleClass().add("app-shell");root.setTop(barra);
        ventana.setTitle("RutaVerde Quindío · "+usuario.obtenerRol());ventana.setScene(new Scene(root,1180,790));aplicarTema();ventana.show();
    }
    private void aplicarTema(){var recurso=VistaPrincipal.class.getResource("/rv-theme.css");if(recurso!=null)ventana.getScene().getStylesheets().add(recurso.toExternalForm());ventana.getScene().getRoot().applyCss();aplicarFuentesDisponibles();}
    private void aplicarFuentesDisponibles(){
        var familias=Font.getFamilies();String display=familias.contains("Brasika Display")?"Brasika Display":"Georgia";String body=familias.contains("Arimo")?"Arimo":"Segoe UI";Parent raiz=ventana.getScene().getRoot();aplicarFamilia(raiz,body);
        for(Node n:raiz.lookupAll(".button, .text-field, .password-field, .combo-box, .date-picker, .spinner, .text-area, .table-view, .list-view"))aplicarFamilia(n,body);
        for(Node n:raiz.lookupAll(".section-title, .login-tagline, .login-heading, .brand-mark, .metric-value, .guide-title, .app-topbar .label"))aplicarFamilia(n,display);
    }
    private void aplicarFamilia(Node nodo,String familia){String estilo=nodo.getStyle();if(estilo==null)estilo="";nodo.setStyle((estilo.isBlank()?"":estilo+"; ")+"-fx-font-family: '"+familia+"';");}
}
