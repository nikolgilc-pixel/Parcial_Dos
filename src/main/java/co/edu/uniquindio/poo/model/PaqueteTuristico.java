package co.edu.uniquindio.poo.model;

import java.time.LocalDate;
import java.util.*;

public class PaqueteTuristico implements IProductoTuristico{

    private final String codigo,nombre;
    private final double descuentoPorcentaje;
    private final List<IProductoTuristico> productos=new ArrayList<>();

    public PaqueteTuristico(String codigo,String nombre,double descuento){
        if(codigo==null||codigo.isBlank()||nombre==null||nombre.isBlank())
            throw new IllegalArgumentException("Código y nombre obligatorios");
        if(descuento<0||descuento>20)
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 20 %");
        this.codigo=codigo;
        this.nombre=nombre;
        this.descuentoPorcentaje=descuento;
    }
    public String obtenerCodigo(){return codigo;} public String obtenerNombre(){
        return nombre;
    }
    public double getDescuentoPorcentaje(){
        return descuentoPorcentaje;
    }
    public synchronized void agregarProducto(IProductoTuristico p){
        Objects.requireNonNull(p);if(p==this||contieneCodigo(p,codigo))
            throw new IllegalArgumentException("Un paquete no puede contenerse a sí mismo, ni indirectamente");
        Set<String> ids=new HashSet<>();
        for(Actividad a:listarActividades())ids.add(a.obtenerCodigo());
        for(Actividad a:p.listarActividades())
            if(!ids.add(a.obtenerCodigo()))
                throw new IllegalArgumentException("El plan no puede incluir dos veces la actividad "+a.obtenerCodigo());productos.add(p);
    }
    private boolean contieneCodigo(IProductoTuristico p,String buscado){
        if(p.obtenerCodigo().equals(buscado))
            return true;
        if(p instanceof PaqueteTuristico paquete)
            return paquete.productos.stream().anyMatch(hijo->contieneCodigo(hijo,buscado));
        return false;
    }
    public synchronized boolean eliminarProducto(String codigo){
        return productos.removeIf(p->p.obtenerCodigo().equals(codigo));
    }
    public synchronized List<IProductoTuristico> getProductos(){
        return List.copyOf(productos);
    }
    public double obtenerPrecio(){
        return productos.stream().mapToDouble(IProductoTuristico::obtenerPrecio).sum()*(1-descuentoPorcentaje/100.0);
    }
    public double obtenerDuracionHoras(){
        return productos.stream().mapToDouble(IProductoTuristico::obtenerDuracionHoras).sum();
    }
    public List<Actividad> listarActividades(){
        return productos.stream().flatMap(p->p.listarActividades().stream()).toList();
    }
    public boolean verificarDisponibilidad(LocalDate fecha,int cantidad){
        return !productos.isEmpty()&&productos.stream().allMatch(p->p.verificarDisponibilidad(fecha,cantidad));
    }
    public synchronized void actualizarCupos(LocalDate fecha, int cantidad){
        List<Actividad> actividades=listarActividades();if(!verificarDisponibilidad(fecha,cantidad))throw new IllegalStateException("No hay cupos para todas las actividades del paquete");List<Actividad> actualizadas=new ArrayList<>();try{for(Actividad actividad:actividades){actividad.actualizarCupos(fecha,cantidad);actualizadas.add(actividad);}}catch(RuntimeException e){actualizadas.forEach(a->a.liberarCupos(fecha,cantidad));throw e;}}
    public IProductoTuristico clonar(){PaqueteTuristico copia=new PaqueteTuristico(codigo+"-COPIA",nombre,descuentoPorcentaje);productos.forEach(p->copia.productos.add(p.clonar()));return copia;}
    @Override public String toString(){return nombre+" — $"+String.format("%,.0f",obtenerPrecio())+" por persona";}
}


