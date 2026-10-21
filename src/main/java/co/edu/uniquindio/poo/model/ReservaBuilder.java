package co.edu.uniquindio.poo.model;

import java.time.LocalDate;

public class ReservaBuilder {private Cliente cliente;
    private IProductoTuristico producto;
    private LocalDate fecha;
    private int viajeros;
    private Guia guia;
    private boolean transporte,seguro;
    private String observaciones="";
    public ReservaBuilder cliente(Cliente v){
        cliente=v;return this;
    }
    public ReservaBuilder producto(IProductoTuristico v){
        producto=v;return this;
    }
    public ReservaBuilder fechaViaje(LocalDate v){
        fecha=v;return this;
    }
    public ReservaBuilder viajeros(int v){
        viajeros=v;return this;
    }
    public ReservaBuilder guia(Guia v){
        guia=v;return this;
    }
    public ReservaBuilder transporte(boolean v){
        transporte=v;return this;
    }
    public ReservaBuilder seguro(boolean v){
        seguro=v;return this;
    }
    public ReservaBuilder observaciones(String v){
        observaciones=v;
        return this;
    }
    public Reserva build(){
        if(cliente==null)throw new IllegalArgumentException("Selecciona un cliente");
        if(producto==null)throw new IllegalArgumentException("Selecciona un producto");
        if(fecha==null)throw new IllegalArgumentException("Selecciona la fecha del viaje");
        Reserva r=new Reserva(cliente,producto,fecha,viajeros,guia,transporte,seguro,observaciones);
        r.validarReglasNegocio();
        return r;}

}
