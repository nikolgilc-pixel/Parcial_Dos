package co.edu.uniquindio.poo.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Actividad implements IProductoTuristico {
    private final String codigo, nombre;
    private final MunicipioQuindio municipio;
    private final double duracionHoras, precioPorPersona;
    private final int cupoMaximoPorDia;
    private final Dificultad dificultad;
    private EstadoActividad estado;
    private final Map<LocalDate,Integer> cuposUsados = new ConcurrentHashMap<>();

    public Actividad(String codigo,String nombre,MunicipioQuindio municipio,double duracionHoras,double precio,int cupo,
                     Dificultad dificultad,EstadoActividad estado) {
        if(codigo==null||codigo.isBlank()||nombre==null||nombre.isBlank())
            throw new IllegalArgumentException("Código y nombre obligatorios");
        if(!Double.isFinite(duracionHoras)||!Double.isFinite(precio)
                ||duracionHoras<=0||precio<0||cupo<1)
            throw new IllegalArgumentException("Duración, precio o cupo inválido");
        this.codigo=codigo.trim();this.nombre=nombre.trim();this.municipio= Objects.requireNonNull(municipio);
        this.duracionHoras=duracionHoras;
        this.precioPorPersona=precio;
        this.cupoMaximoPorDia=cupo;
        this.dificultad=Objects.requireNonNull(dificultad);
        this.estado=Objects.requireNonNull(estado);
    }
    public String obtenerCodigo(){
        return codigo;
    }
    public String obtenerNombre(){
        return nombre;
    }
    public double obtenerPrecio(){
        return precioPorPersona;
    }
    public double obtenerDuracionHoras(){
        return duracionHoras;
    }
    public MunicipioQuindio getMunicipio(){
        return municipio;
    }
    public int getCupoMaximoPorDia(){
        return cupoMaximoPorDia;
    }
    public Dificultad getDificultad(){
        return dificultad;
    }
    public EstadoActividad getEstado(){
        return estado;
    }
    public void setEstado(EstadoActividad e){
        estado=Objects.requireNonNull(e);
    }
    public List<Actividad> listarActividades(){
        return List.of(this);
    }
    public synchronized boolean verificarDisponibilidad(LocalDate fecha,int cantidad){
        return fecha!=null&&cantidad>0&&estado==EstadoActividad.DISPONIBLE&&cuposMaximoRestante(fecha)>=cantidad;
    }
    private int cuposMaximoRestante(LocalDate fecha){
        return cupoMaximoPorDia-cuposUsados.getOrDefault(fecha,0);}
    public synchronized void actualizarCupos(LocalDate fecha,int cantidad){
        if(fecha==null||cantidad<1||cuposMaximoRestante(fecha)<cantidad)
            throw new IllegalStateException("No hay cupos suficientes para "+nombre+" el "+fecha);
        cuposUsados.merge(fecha,cantidad,Integer::sum);
    }
    public synchronized void liberarCupos(LocalDate fecha,int cantidad){
        if(fecha==null||cantidad<1)throw new IllegalArgumentException("La fecha y la cantidad de cupos deben ser válidas");
        int quedan=cuposUsados.getOrDefault(fecha,0)-cantidad;
        if(quedan<=0)cuposUsados.remove(fecha);
        else cuposUsados.put(fecha,quedan);
    }
    public IProductoTuristico clonar(){
        return new Actividad(codigo+"-COPIA",nombre,municipio,duracionHoras,precioPorPersona,cupoMaximoPorDia,dificultad,estado);
    }
    @Override
    public String toString(){return nombre+" — $"+String.format("%,.0f",precioPorPersona)+" por persona";}
}
