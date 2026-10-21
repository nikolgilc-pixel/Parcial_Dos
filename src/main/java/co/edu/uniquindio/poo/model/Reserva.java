package co.edu.uniquindio.poo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
public class Reserva {
        private final Cliente cliente;
        private final IProductoTuristico producto;
        private final LocalDate fechaViaje;
        private final int viajeros;
        private final Guia guia;
        private final boolean transporte,seguro;
        private final String observaciones;
        private long numero;
        private EstadoReserva estado=EstadoReserva.PENDIENTE;
        public static final BigDecimal COSTO_TRANSPORTE=BigDecimal.valueOf(35000), COSTO_SEGURO=BigDecimal.valueOf(15000);
        Reserva(Cliente c,IProductoTuristico p,LocalDate f,int v,Guia g,boolean t,boolean s,String o){
            cliente= Objects.requireNonNull(c);
            producto=Objects.requireNonNull(p);
            fechaViaje=Objects.requireNonNull(f);
            viajeros=v;
            guia=g;
            transporte=t;
            seguro=s;
            observaciones=o==null?"":o;
        }
        public void validarReglasNegocio(){
            if(fechaViaje.isBefore(LocalDate.now()))throw new IllegalArgumentException("La fecha del viaje no puede estar en el pasado");
            if(viajeros<1||viajeros>15)throw new IllegalArgumentException("El grupo debe tener entre 1 y 15 viajeros");
            if(guia!=null&&!guia.habla(cliente.getIdiomaPreferido()))throw new IllegalArgumentException("El guía no habla el idioma preferido del cliente");
            if(guia!=null&&!guia.isDisponible())throw new IllegalArgumentException("El guía no está disponible");
            if(producto.listarActividades().stream().anyMatch(a->a.getEstado()!=EstadoActividad.DISPONIBLE))throw new IllegalArgumentException("El producto incluye una actividad suspendida");}
        public BigDecimal calcularTotal(){BigDecimal total=BigDecimal.valueOf(producto.obtenerPrecio()).multiply(BigDecimal.valueOf(viajeros));
            if(transporte)total=total.add(COSTO_TRANSPORTE.multiply(BigDecimal.valueOf(viajeros)));if(seguro)total=total.add(COSTO_SEGURO.multiply(BigDecimal.valueOf(viajeros)));
            if(guia!=null)total=total.add(guia.calcularCosto(producto.obtenerDuracionHoras()));return total.setScale(0,java.math.RoundingMode.HALF_UP);
        }
        public Cliente getCliente(){
            return cliente;
        }
    public IProductoTuristico getProducto(){
            return producto;
        }
        public LocalDate getFechaViaje(){
            return fechaViaje;
        }public int getViajeros(){
            return viajeros;
        }
        public Guia getGuia(){
            return guia;
        }
        public boolean incluyeTransporte(){
            return transporte;
        }public boolean incluyeSeguro(){
            return seguro;
        }public String getObservaciones(){
            return observaciones;}public long getNumero(){
            return numero;
        }
        public void asignarNumero(long n){
            if(numero!=0)throw new IllegalStateException("La reserva ya tiene número");
            numero=n;
        }
        public EstadoReserva getEstado(){
            return estado;
        }
        public void cambiarEstado(EstadoReserva e){
            estado=Objects.requireNonNull(e);
        }
    }

