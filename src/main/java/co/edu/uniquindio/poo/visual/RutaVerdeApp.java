package co.edu.uniquindio.poo.visual;

import co.edu.uniquindio.poo.controller.PrincipalController;
import javafx.application.Application;
import javafx.stage.Stage;

public class RutaVerdeApp extends Application {
    @Override
    public void start(Stage stage) {
        VistaPrincipal vistaPrincipal = new VistaPrincipal(new PrincipalController(), stage);
        vistaPrincipal.mostrarIngreso();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
