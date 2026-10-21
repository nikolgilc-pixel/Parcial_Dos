package co.edu.uniquindio.poo.visual;

import co.edu.uniquindio.poo.controller.PrincipalController;
import co.edu.uniquindio.poo.model.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class VistaCliente {
    private final PrincipalController c;private final Cliente cliente;
    private final ObservableList<IProductoTuristico> productos=FXCollections.observableArrayList();
    private final ObservableList<Reserva> reservas=FXCollections.observableArrayList();
    private final TableView<Reserva> tablaReservas=new TableView<>(reservas);
    private final Map<IProductoTuristico,SimpleBooleanProperty> seleccionPropuesta=new IdentityHashMap<>();
    public VistaCliente(PrincipalController c,Cliente cliente){this.c=c;this.cliente=cliente;}
    public Parent crearVista(){
        actualizar();
        Label title=new Label("Qué bueno tenerte, "+cliente.getNombreCompleto().split(" ")[0]);title.getStyleClass().add("section-title");Label sub=new Label("Tu próxima aventura por el Quindío está más cerca.");sub.getStyleClass().add("muted");
        TabPane tabs=new TabPane(tab("Descubrir",explorar()),tab("Mis reservas",misReservas()),tab("Crear mi plan",proponerPaquete()));
        VBox root=new VBox(18,title,sub,tabs);root.setPadding(new Insets(26));root.getStyleClass().add("page-background");VBox.setVgrow(tabs,Priority.ALWAYS);return root;
    }
    private Parent explorar(){
        VBox content=new VBox(18);content.setPadding(new Insets(12));content.setFillWidth(true);
        VBox hero=new VBox(12);hero.setPadding(new Insets(26));hero.getStyleClass().add("client-hero");Label eyebrow=new Label("RUTAVERDE  ·  QUINDÍO");eyebrow.getStyleClass().add("hero-pill");Label headline=new Label("El Quindío te está esperando.");headline.getStyleClass().add("login-tagline");Label line=new Label("Bosques de niebla, café recién hecho y caminos para recordar.");line.getStyleClass().add("hero-copy");hero.getChildren().addAll(eyebrow,headline,line);
        Label heading=new Label("Experiencias para elegir");heading.getStyleClass().add("section-title");ListView<IProductoTuristico> catalogo=new ListView<>(productos);catalogo.setPrefHeight(158);catalogo.setCellFactory(v->new ListCell<>(){protected void updateItem(IProductoTuristico p,boolean empty){super.updateItem(p,empty);if(empty||p==null){setText(null);setGraphic(null);}else{setText((p instanceof PaqueteTuristico?"PAQUETE  ·  ":"ACTIVIDAD  ·  ")+p.obtenerNombre()+"     |     "+String.format("$%,.0f",p.obtenerPrecio())+" por persona");setStyle("-fx-padding: 13px; -fx-font-size: 14px;");}}});
        ComboBox<IProductoTuristico> producto=new ComboBox<>(productos);producto.setPromptText("Elige una actividad o un paquete");producto.setMaxWidth(Double.MAX_VALUE);Label detalle=new Label("Selecciona una experiencia para ver qué actividades incluye.");detalle.getStyleClass().add("product-detail");Runnable mostrarDetalle=()->{IProductoTuristico p=producto.getValue();if(p!=null)detalle.setText(detalleProducto(p));};catalogo.getSelectionModel().selectedItemProperty().addListener((o,a,b)->{if(b!=null)producto.setValue(b);});producto.setOnAction(e->mostrarDetalle.run());
        DatePicker fecha=new DatePicker(LocalDate.now().plusDays(1));Spinner<Integer> viajeros=new Spinner<>(1,15,1);ComboBox<Guia> guia=new ComboBox<>(FXCollections.observableArrayList(c.guiasDisponibles()));guia.setPromptText("Guía (opcional)");guia.setMaxWidth(Double.MAX_VALUE);
        CheckBox transporte=new CheckBox("Transporte · $35.000 por viajero"),seguro=new CheckBox("Seguro · $15.000 por viajero");TextField tarjeta=new TextField();tarjeta.setPromptText("Tarjeta para el pago si tu solicitud es aprobada");TextArea obs=new TextArea();obs.setPromptText("Observaciones para el equipo (opcional)");obs.setPrefRowCount(2);
        Label estimado=new Label("Selecciona una experiencia para estimar el total.");estimado.getStyleClass().add("price-badge");Runnable calc=()->{IProductoTuristico p=producto.getValue();if(p!=null){int n=viajeros.getValue();double total=(p.obtenerPrecio()+(transporte.isSelected()?35000:0)+(seguro.isSelected()?15000:0))*n;estimado.setText("Estimado para "+n+" viajero(s): $"+String.format("%,.0f",total)+" · se cobra solo si se aprueba la solicitud");}};producto.valueProperty().addListener((o,a,b)->calc.run());viajeros.valueProperty().addListener((o,a,b)->calc.run());transporte.setOnAction(e->calc.run());seguro.setOnAction(e->calc.run());
        Button pedir=new Button("Enviar solicitud de reserva");pedir.getStyleClass().add("primary-button");Label estado=new Label();pedir.setOnAction(e->{try{Reserva r=c.solicitarReserva(cliente,producto.getValue(),fecha.getValue(),viajeros.getValue(),guia.getValue(),transporte.isSelected(),seguro.isSelected(),obs.getText(),tarjeta.getText());estado.setText("Solicitud #"+r.getNumero()+" enviada. El cobro se hará únicamente si el administrador la aprueba.");actualizar();}catch(RuntimeException ex){estado.setText(ex.getMessage());}});
        VBox booking=new VBox(13,new Label("Solicita una reserva"),new Label("Elige el plan y la fecha; el administrador revisará la solicitud antes de procesar el pago."),new HBox(10,producto,fecha,viajeros),detalle,guia,new HBox(14,transporte,seguro),estimado,tarjeta,obs,pedir,estado);booking.getStyleClass().add("surface-card");content.getChildren().addAll(hero,heading,catalogo,booking);ScrollPane scroll=new ScrollPane(content);scroll.setFitToWidth(true);scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);return scroll;
    }
    private String detalleProducto(IProductoTuristico producto){
        String actividades=producto.listarActividades().stream().map(a->a.obtenerNombre()+" · "+a.getMunicipio().name()).reduce((a,b)->a+"   /   "+b).orElse("Sin actividades asociadas");
        return (producto instanceof PaqueteTuristico?"Este paquete incluye: ":"Actividad: ")+actividades;
    }
    private Parent misReservas(){
        if(tablaReservas.getColumns().isEmpty()){tablaReservas.getColumns().add(col("N.º",r->String.valueOf(r.getNumero())));tablaReservas.getColumns().add(col("Producto",r->r.getProducto().obtenerNombre()));tablaReservas.getColumns().add(col("Fecha",r->r.getFechaViaje().toString()));tablaReservas.getColumns().add(col("Viajeros",r->String.valueOf(r.getViajeros())));tablaReservas.getColumns().add(col("Estado",r->switch(r.getEstado()){case PENDIENTE->"Pendiente de aprobación";case CONFIRMADA->"Confirmada";case CANCELADA->"Cancelada";case RECHAZADA->"No aprobada";}));}tablaReservas.setPrefHeight(360);
        Button cancelar=new Button("Retirar solicitud / cancelar reserva");cancelar.getStyleClass().add("secondary-button");Label response=new Label();cancelar.setOnAction(e->{Reserva r=tablaReservas.getSelectionModel().getSelectedItem();if(r==null){response.setText("Selecciona primero tu reserva.");return;}TextInputDialog dialog=new TextInputDialog();dialog.setTitle("Cancelar o retirar solicitud");dialog.setHeaderText("Indica el motivo");dialog.setContentText("Motivo:");dialog.showAndWait().ifPresent(m->{try{response.setText(c.cancelarReserva(cliente,r.getNumero(),m).mensaje());actualizar();}catch(RuntimeException ex){response.setText(ex.getMessage());}});});
        VBox box=new VBox(14,new Label("Las solicitudes nuevas aparecen pendientes hasta que el administrador las revise."),tablaReservas,cancelar,response,new Label("Para reservas confirmadas, con menos de 48 horas se retiene el 100 % del total."));box.setPadding(new Insets(16));box.getStyleClass().add("surface-card");return box;
    }
    private Parent proponerPaquete(){
        seleccionPropuesta.clear();List<IProductoTuristico> actividades=productos.stream().filter(p->p instanceof Actividad).toList();actividades.forEach(p->seleccionPropuesta.put(p,new SimpleBooleanProperty(false)));
        ListView<IProductoTuristico> choices=new ListView<>(FXCollections.observableArrayList(actividades));choices.setPrefHeight(270);choices.setCellFactory(list->new ListCell<>(){private final CheckBox check=new CheckBox();{check.setOnAction(e->{IProductoTuristico p=getItem();if(p!=null)seleccionPropuesta.get(p).set(check.isSelected());});}protected void updateItem(IProductoTuristico p,boolean empty){super.updateItem(p,empty);if(empty||p==null){setGraphic(null);}else{check.setText(p.obtenerNombre()+"  ·  "+p.listarActividades().get(0).getMunicipio().name());check.setSelected(seleccionPropuesta.get(p).get());setGraphic(check);}}});
        Label detalle=new Label("Marca una o varias actividades y podrás ver tu selección antes de enviarla.");detalle.getStyleClass().add("product-detail");Runnable update=()->{List<IProductoTuristico> selected=seleccionados();detalle.setText(selected.isEmpty()?"Aún no has seleccionado actividades.":"Tu plan incluye: "+selected.stream().flatMap(p->p.listarActividades().stream()).map(Actividad::obtenerNombre).distinct().reduce((a,b)->a+" · "+b).orElse(""));};seleccionPropuesta.values().forEach(p->p.addListener((o,a,b)->update.run()));
        TextField name=new TextField();name.setPromptText("Nombre para tu plan (opcional)");Button send=new Button("Enviar propuesta al equipo RutaVerde");send.getStyleClass().add("primary-button");Label status=new Label();send.setOnAction(e->{try{List<IProductoTuristico> selected=seleccionados();String nombre=name.getText().isBlank()?"Plan de "+cliente.getNombreCompleto():name.getText().trim();PropuestaPaquete proposal=c.proponerPaquete(cliente,nombre,0,selected);status.setText("Propuesta "+proposal.getCodigo()+" enviada. El administrador revisará las actividades y definirá el precio antes de publicarla.");}catch(RuntimeException ex){status.setText(ex.getMessage());}});
        VBox panel=new VBox(13,new Label("Crea un paquete a tu medida"),new Label("Marca varias actividades. Puedes proponer una combinación diferente a los paquetes existentes; no necesitas calcular ni sugerir el precio."),choices,detalle,name,send,status);panel.getStyleClass().add("surface-card");return panel;
    }
    private List<IProductoTuristico> seleccionados(){return seleccionPropuesta.entrySet().stream().filter(e->e.getValue().get()).map(Map.Entry::getKey).toList();}
    private void actualizar(){productos.setAll(c.productos());reservas.setAll(c.reservas(cliente));}
    private Tab tab(String nombre,Parent contenido){Tab t=new Tab(nombre,contenido);t.setClosable(false);return t;}
    private <T> TableColumn<T,String> col(String nombre,java.util.function.Function<T,String> f){TableColumn<T,String> col=new TableColumn<>(nombre);col.setCellValueFactory(v->new SimpleStringProperty(f.apply(v.getValue())));col.setPrefWidth(190);return col;}
}

