package co.edu.uniquindio.poo.visual;

import co.edu.uniquindio.poo.controller.PrincipalController;
import co.edu.uniquindio.poo.model.EstadoReserva;
import co.edu.uniquindio.poo.model.Guia;
import co.edu.uniquindio.poo.model.Reserva;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class VistaGuia {
    private final PrincipalController c;private final Guia guia;private final ObservableList<Reserva> reservas=FXCollections.observableArrayList();private final TableView<Reserva> tabla=new TableView<>(reservas);private final Label total=new Label("0"),proximas=new Label("0");
    public VistaGuia(PrincipalController c,Guia guia){this.c=c;this.guia=guia;}
    public Parent crearVista(){
        Label titulo=new Label("Tu agenda de experiencias");titulo.getStyleClass().add("guide-title");Label saludo=new Label("Hola, "+guia.getNombreCompleto()+" · cada recorrido cuenta una historia.");saludo.getStyleClass().add("guide-subtitle");HBox metrics=new HBox(14,metric("SERVICIOS ASIGNADOS",total,"metric-sage"),metric("PRÓXIMOS RECORRIDOS",proximas,"metric-pewter"));VBox hero=new VBox(13,titulo,saludo,metrics);hero.getStyleClass().add("guide-hero");
        tabla.getColumns().add(col("Reserva",r->"RV-"+r.getNumero()));tabla.getColumns().add(col("Cliente",r->r.getCliente().getNombreCompleto()));tabla.getColumns().add(col("Experiencia",r->r.getProducto().obtenerNombre()));tabla.getColumns().add(col("Fecha",r->r.getFechaViaje().toString()));tabla.getColumns().add(col("Viajeros",r->String.valueOf(r.getViajeros())));tabla.getColumns().add(col("Estado",r->r.getEstado()==EstadoReserva.PENDIENTE?"Pendiente de confirmación":r.getEstado().name()));tabla.setPrefHeight(440);
        Button cancelar=new Button("Cancelar servicio seleccionado");cancelar.getStyleClass().add("secondary-button");Label resultado=new Label();cancelar.setOnAction(e->{Reserva selected=tabla.getSelectionModel().getSelectedItem();if(selected==null){resultado.setText("Selecciona primero una actividad confirmada de tu agenda.");return;}if(selected.getEstado()!=EstadoReserva.CONFIRMADA){resultado.setText("Solo puedes cancelar un servicio ya confirmado.");return;}TextInputDialog dialog=new TextInputDialog();dialog.setTitle("Cancelar servicio asignado");dialog.setHeaderText("Motivo operativo");dialog.setContentText("Por ejemplo, lluvia intensa o cierre de vía:");dialog.showAndWait().ifPresent(reason->{try{if(reason.isBlank())throw new IllegalArgumentException("Escribe un motivo para continuar");resultado.setText(c.cancelarReserva(guia,selected.getNumero(),reason).mensaje());refrescar();}catch(RuntimeException ex){resultado.setText(ex.getMessage());}});});
        VBox agenda=new VBox(14,new Label("Servicios asignados a tu usuario"),tabla,new HBox(12,cancelar,resultado));agenda.getStyleClass().add("surface-card");VBox root=new VBox(18,hero,agenda);root.setPadding(new Insets(26));root.getStyleClass().add("page-background");refrescar();return root;
    }
    private VBox metric(String caption,Label value,String style){value.getStyleClass().add("metric-value");Label label=new Label(caption);label.getStyleClass().add("metric-caption");VBox card=new VBox(3,label,value);card.getStyleClass().addAll("metric-card",style);return card;}
    private void refrescar(){reservas.setAll(c.reservas(guia));long confirmed=reservas.stream().filter(r->r.getEstado()==EstadoReserva.CONFIRMADA).count();long future=reservas.stream().filter(r->r.getEstado()==EstadoReserva.CONFIRMADA&&!r.getFechaViaje().isBefore(LocalDate.now())).count();total.setText(String.valueOf(confirmed));proximas.setText(String.valueOf(future));}
    private <T> TableColumn<T,String> col(String name,java.util.function.Function<T,String> f){TableColumn<T,String> col=new TableColumn<>(name);col.setCellValueFactory(v->new SimpleStringProperty(f.apply(v.getValue())));col.setPrefWidth(180);return col;}
}
