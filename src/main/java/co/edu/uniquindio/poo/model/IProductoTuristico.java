package co.edu.uniquindio.poo.model;

import java.time.LocalDate;
import java.util.List;

public interface IProductoTuristico {
    String obtenerCodigo(); String obtenerNombre();
    double obtenerPrecio();
    double obtenerDuracionHoras();
    List<Actividad> listarActividades();
    boolean verificarDisponibilidad(LocalDate fecha, int cantidad);
    void actualizarCupos(LocalDate fecha, int cantidad);
    void liberarCupos(LocalDate fecha, int cantidad);
    IProductoTuristico clonar();
}
