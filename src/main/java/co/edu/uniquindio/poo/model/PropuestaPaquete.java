package co.edu.uniquindio.poo.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class PropuestaPaquete {
    public enum Estado { PENDIENTE, APROBADA, RECHAZADA }

    private final String codigo;
    private final Cliente solicitante;
    private final PaqueteTuristico paquete;
    private final BigDecimal precioSugerido;
    private Estado estado = Estado.PENDIENTE;

    public PropuestaPaquete(Cliente solicitante, PaqueteTuristico paquete) {
        this(solicitante, paquete, null);
    }

    public PropuestaPaquete(Cliente solicitante, PaqueteTuristico paquete, BigDecimal precioSugerido) {
        this.codigo = "PROP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.solicitante = Objects.requireNonNull(solicitante, "El cliente es obligatorio");
        this.paquete = Objects.requireNonNull(paquete, "El paquete es obligatorio");
        if (paquete.getProductos().isEmpty()) throw new IllegalArgumentException("El paquete debe incluir al menos una actividad");
        if (precioSugerido != null && precioSugerido.signum() < 0) throw new IllegalArgumentException("El precio sugerido no puede ser negativo");
        this.precioSugerido = precioSugerido;
    }

    public String getCodigo() { return codigo; }
    public Cliente getSolicitante() { return solicitante; }
    public PaqueteTuristico getPaquete() { return paquete; }
    public BigDecimal getPrecioSugerido() { return precioSugerido; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = Objects.requireNonNull(estado); }
}
