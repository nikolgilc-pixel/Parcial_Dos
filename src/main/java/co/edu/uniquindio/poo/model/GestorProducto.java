package co.edu.uniquindio.poo.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestorProducto {
    private final Map<String,IProductoTuristico> catalogo=new LinkedHashMap<>();
    private final Map<String,PropuestaPaquete> propuestas=new LinkedHashMap<>();
    public void registrarProducto(IProductoTuristico producto) {
        if (producto instanceof PaqueteTuristico paquete && paquete.getProductos().isEmpty())
            throw new IllegalArgumentException("Un paquete debe incluir al menos un producto");
        if (catalogo.putIfAbsent(producto.obtenerCodigo(), producto) != null)
            throw new IllegalArgumentException("Ya existe ese código de producto");
    }
    public IProductoTuristico buscarProducto(String codigo){
        return catalogo.get(codigo);
    }
    public boolean eliminarProducto(String codigo){
        return catalogo.remove(codigo)!=null;
    }
    public List<IProductoTuristico> obtenerCatalogo(){
        return List.copyOf(catalogo.values());
    }
    public PropuestaPaquete proponerPaquete(Cliente cliente,PaqueteTuristico paquete){
        PropuestaPaquete p=new PropuestaPaquete(cliente,paquete);propuestas.put(p.getCodigo(),p);
        return p;
    }
    public PropuestaPaquete proponerPaquete(Cliente cliente,PaqueteTuristico paquete,java.math.BigDecimal precioSugerido){
        PropuestaPaquete p=new PropuestaPaquete(cliente,paquete,precioSugerido);propuestas.put(p.getCodigo(),p);
        return p;
    }
    public List<PropuestaPaquete> obtenerPropuestasPendientes(){
        return propuestas.values().stream().filter(p->p.getEstado()==PropuestaPaquete.Estado.PENDIENTE).toList();
    }
    public void aprobarPropuesta(String codigo){
        PropuestaPaquete p=propuestas.get(codigo);
        if(p==null||p.getEstado()!=PropuestaPaquete.Estado.PENDIENTE)
            throw new IllegalArgumentException("No hay una propuesta pendiente con ese código");
        registrarProducto(p.getPaquete());
        p.setEstado(PropuestaPaquete.Estado.APROBADA);
    }
    public void rechazarPropuesta(String codigo){
        PropuestaPaquete p=propuestas.get(codigo);
        if(p==null||p.getEstado()!=PropuestaPaquete.Estado.PENDIENTE)
            throw new IllegalArgumentException("No hay una propuesta pendiente con ese código");
        p.setEstado(PropuestaPaquete.Estado.RECHAZADA);
    }
}
