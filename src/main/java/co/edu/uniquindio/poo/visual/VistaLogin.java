package co.edu.uniquindio.poo.visual;

import co.edu.uniquindio.poo.controller.PrincipalController;
import co.edu.uniquindio.poo.model.Idioma;
import co.edu.uniquindio.poo.model.Pais;
import co.edu.uniquindio.poo.model.Usuario;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

/** Pantalla de acceso centrada; la creación pública solo permite cuentas cliente. */
public class VistaLogin {
    private final PrincipalController controlador;private final Consumer<Usuario> alIngresar;
    public VistaLogin(PrincipalController controlador,Consumer<Usuario> alIngresar){this.controlador=controlador;this.alIngresar=alIngresar;}
    public Parent crearVista(){
        StackPane backdrop=new StackPane();backdrop.getStyleClass().add("login-background");
        VBox card=new VBox(15);card.setAlignment(Pos.CENTER_LEFT);card.setMaxWidth(500);card.setPadding(new Insets(42));card.getStyleClass().add("login-card");
        Label brand=new Label("RUTAVERDE");brand.getStyleClass().add("brand-mark");Label region=new Label("QUINDÍO · COLOMBIA");region.getStyleClass().add("brand-region");Label tagline=new Label("Experiencias que se viven,\nrecuerdos que se quedan.");tagline.getStyleClass().add("login-tagline");
        Label instruction=new Label("Ingresa con tu documento y contraseña");instruction.getStyleClass().add("muted");TextField doc=new TextField();doc.setPromptText("Documento de identidad");PasswordField pass=new PasswordField();pass.setPromptText("Contraseña");Label feedback=new Label();feedback.getStyleClass().add("error-message");Button enter=new Button("Ingresar a mi cuenta");enter.getStyleClass().add("primary-button");enter.setMaxWidth(Double.MAX_VALUE);
        Runnable login=()->{try{alIngresar.accept(controlador.ingresar(doc.getText().trim(),pass.getText()));}catch(RuntimeException ex){feedback.setText(ex.getMessage());}};enter.setOnAction(e->login.run());pass.setOnAction(e->login.run());Hyperlink signup=new Hyperlink("¿No tienes cuenta? Crear cuenta de cliente");signup.getStyleClass().add("signup-link");signup.setOnAction(e->mostrarRegistro());
        Label demo=new Label("ADMINISTRACIÓN   admin / admin123\nCLIENTE   1001 / Cliente123!      GUÍA   2001 / Guia123!");demo.getStyleClass().add("demo-hint");card.getChildren().addAll(brand,region,tagline,instruction,doc,pass,enter,feedback,signup,new Separator(),demo);
        backdrop.getChildren().add(card);StackPane.setAlignment(card,Pos.CENTER);StackPane.setMargin(card,new Insets(28));return backdrop;
    }
    private void mostrarRegistro(){
        Dialog<ButtonType> dialog=new Dialog<>();dialog.setTitle("Crear cuenta de cliente");dialog.setHeaderText("Empieza a planear tus experiencias en el Quindío");GridPane form=new GridPane();form.setHgap(12);form.setVgap(10);form.setPadding(new Insets(14));TextField id=new TextField(),name=new TextField(),phone=new TextField(),email=new TextField();PasswordField password=new PasswordField();id.setPromptText("Documento");name.setPromptText("Nombre completo");phone.setPromptText("Teléfono");email.setPromptText("Correo electrónico");password.setPromptText("Mínimo 6 caracteres");ComboBox<Pais> country=new ComboBox<>(javafx.collections.FXCollections.observableArrayList(Pais.values()));country.setPromptText("País de origen");ComboBox<Idioma> language=new ComboBox<>(javafx.collections.FXCollections.observableArrayList(Idioma.values()));language.setPromptText("Idioma preferido");form.addRow(0,new Label("Documento"),id);form.addRow(1,new Label("Nombre"),name);form.addRow(2,new Label("Teléfono"),phone);form.addRow(3,new Label("Correo"),email);form.addRow(4,new Label("País"),country);form.addRow(5,new Label("Idioma"),language);form.addRow(6,new Label("Contraseña"),password);dialog.getDialogPane().setContent(form);dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,new ButtonType("Crear cuenta",ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait().ifPresent(result->{if(result.getButtonData()==ButtonBar.ButtonData.OK_DONE){try{controlador.registrarClientePublico(id.getText().trim(),name.getText().trim(),phone.getText().trim(),email.getText().trim(),country.getValue(),language.getValue(),password.getText());new Alert(Alert.AlertType.INFORMATION,"Tu cuenta está lista. Ya puedes ingresar.").showAndWait();}catch(RuntimeException ex){new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();}}});
    }
}
