package co.edu.uniquindio.poo.model;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class GestorReservas {
    private final Map<Long, Reserva> reservas = new LinkedHashMap<>();
    private final AtomicLong consecutivo = new AtomicLong(1);
    private final IPasarelaPago pasarela;
    private final ServicioNotificacion notificador;
    private final String agencia;
    private final String rnt;

    public GestorReservas(IPasarelaPago pasarela, ServicioNotificacion notificador, String agencia, String rnt) {
        this.pasarela = Objects.requireNonNull(pasarela);
        this.notificador = Objects.requireNonNull(notificador);
        this.agencia = Objects.requireNonNull(agencia);
        this.rnt = Objects.requireNonNull(rnt);
    }

    public synchronized Reserva solicitar(Reserva reserva, String tarjeta) {
        Objects.requireNonNull(reserva).validarReglasNegocio();
        if (tarjeta == null || tarjeta.isBlank()) throw new IllegalArgumentException("Ingresa una tarjeta para el pago");
        registrar(reserva);
        tarjetaPendiente.put(reserva.getNumero(), tarjeta);
        return reserva;
    }

    public synchronized ResultadoConfirmacion confirmar(Reserva reserva, String tarjeta, CanalVenta canal) {
        Objects.requireNonNull(reserva).validarReglasNegocio();
        return confirmarPagoYReserva(reserva, tarjeta, canal);
    }

    public synchronized ResultadoConfirmacion aprobarSolicitud(long numero, CanalVenta canal) {
        Reserva reserva = buscarReserva(numero);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE)
            throw new IllegalArgumentException("La solicitud ya fue procesada");
        // The request stores card data only in an external payment token in a real system;
        // this educational model keeps it on the pending reservation record.
        return confirmarPagoYReserva(reserva, tarjetaPendiente.get(numero), canal);
    }

    private final Map<Long, String> tarjetaPendiente = new LinkedHashMap<>();

    private ResultadoConfirmacion confirmarPagoYReserva(Reserva reserva, String tarjeta, CanalVenta canal) {
        if (tarjeta == null || !ValidadorLuhn.validarLuhn(tarjeta))
            return ResultadoConfirmacion.fallo("La tarjeta no es válida");
        if (canal == null) throw new IllegalArgumentException("Selecciona un canal de venta");
        if (!reserva.getProducto().verificarDisponibilidad(reserva.getFechaViaje(), reserva.getViajeros()))
            return ResultadoConfirmacion.fallo("No hay cupos disponibles para la fecha seleccionada");
        ResultadoPago pago = pasarela.procesarPago(tarjeta, reserva.calcularTotal(), "RV-" + consecutivo.get());
        if (!pago.aprobado()) return ResultadoConfirmacion.fallo(pago.motivoRechazo());
        reserva.getProducto().actualizarCupos(reserva.getFechaViaje(), reserva.getViajeros());
        if (reserva.getNumero() == 0) registrar(reserva);
        reserva.cambiarEstado(EstadoReserva.CONFIRMADA);
        tarjetaPendiente.remove(reserva.getNumero());
        String voucher = creadorVoucher(canal).emitir(reserva, agencia, rnt);
        notificador.enviar(reserva.getCliente().getCorreo(), "Reserva confirmada", voucher);
        return ResultadoConfirmacion.exito(reserva, voucher);
    }

    private CreadorVoucher creadorVoucher(CanalVenta canal) {
        return switch (canal) {
            case ASESOR -> new GeneradorVoucherAsesor();
            case KIOSCO -> new GeneradorVoucherKiosco();
            case APP -> new GeneradorVoucherApp();
        };
    }

    private void registrar(Reserva reserva) {
        if (reserva.getNumero() == 0) reserva.asignarNumero(consecutivo.getAndIncrement());
        reservas.put(reserva.getNumero(), reserva);
    }

    private Reserva buscarReserva(long numero) {
        Reserva reserva = reservas.get(numero);
        if (reserva == null) throw new IllegalArgumentException("No existe la reserva seleccionada");
        return reserva;
    }

    public synchronized void rechazarSolicitud(long numero) {
        Reserva reserva = buscarReserva(numero);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) throw new IllegalArgumentException("La solicitud ya fue procesada");
        reserva.cambiarEstado(EstadoReserva.RECHAZADA);
        tarjetaPendiente.remove(numero);
        notificador.enviar(reserva.getCliente().getCorreo(), "Solicitud no aprobada", "La solicitud #" + numero + " no fue aprobada.");
    }

    public synchronized ResultadoCancelacion cancelarReserva(long numero, Usuario actor, String motivo) {
        Reserva reserva = buscarReserva(numero);
        if (actor == null || (!(actor instanceof Administrador)
                && !(actor instanceof Cliente && actor.equals(reserva.getCliente()))
                && !(actor instanceof Guia && actor.equals(reserva.getGuia()))) )
            throw new SecurityException("No tienes permiso para cancelar esta reserva");
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("Escribe el motivo de cancelación");
        if (reserva.getEstado() != EstadoReserva.PENDIENTE && reserva.getEstado() != EstadoReserva.CONFIRMADA)
            throw new IllegalArgumentException("La reserva ya fue cancelada o rechazada");
        BigDecimal cargo = BigDecimal.ZERO;
        BigDecimal reembolso = BigDecimal.ZERO;
        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            long horas = Duration.between(LocalDateTime.now(), reserva.getFechaViaje().atTime(LocalTime.MAX)).toHours();
            cargo = horas < 48 ? reserva.calcularTotal() : BigDecimal.ZERO;
            reembolso = reserva.calcularTotal().subtract(cargo);
            reserva.getProducto().liberarCupos(reserva.getFechaViaje(), reserva.getViajeros());
        }
        tarjetaPendiente.remove(numero);
        reserva.cambiarEstado(EstadoReserva.CANCELADA);
        String mensaje = "Reserva cancelada. Cargo retenido: $" + cargo.toPlainString() + "; reembolso calculado: $" + reembolso.toPlainString() + ". Motivo: " + motivo.trim();
        notificador.enviar(reserva.getCliente().getCorreo(), "Reserva cancelada", mensaje);
        return new ResultadoCancelacion(true, mensaje, cargo, reembolso);
    }

    public synchronized List<Reserva> obtenerTodasLasReservas() { return List.copyOf(reservas.values()); }
    public synchronized List<Reserva> obtenerReservasPorCliente(String id) {
        return reservas.values().stream().filter(r -> r.getCliente().getIdentificacion().equals(id)).toList();
    }
    public synchronized List<Reserva> obtenerReservasPorGuia(String id) {
        return reservas.values().stream().filter(r -> r.getGuia() != null && r.getGuia().getIdentificacion().equals(id)).toList();
    }
    public synchronized BigDecimal calcularIngresos(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        return reservas.values().stream().filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .filter(r -> enPeriodo(r, desde, hasta)).map(Reserva::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public synchronized Optional<IProductoTuristico> obtenerProductoMasPopular() {
        return obtenerResumenProductoMasPopular(null, null).map(Map.Entry::getKey);
    }
    public synchronized Optional<IProductoTuristico> obtenerProductoMasPopular(LocalDate desde, LocalDate hasta) {
        return obtenerResumenProductoMasPopular(desde, hasta).map(Map.Entry::getKey);
    }
    public synchronized Optional<Map.Entry<IProductoTuristico, Integer>> obtenerResumenProductoMasPopular(LocalDate desde, LocalDate hasta) {
        if ((desde == null) != (hasta == null)) throw new IllegalArgumentException("Indica ambas fechas del periodo");
        if (desde != null) validarPeriodo(desde, hasta);
        return reservas.values().stream().filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .filter(r -> desde == null || enPeriodo(r, desde, hasta))
                .collect(Collectors.groupingBy(Reserva::getProducto, LinkedHashMap::new,
                        Collectors.summingInt(Reserva::getViajeros))).entrySet().stream()
                .max(Map.Entry.comparingByValue());
    }
    private boolean enPeriodo(Reserva reserva, LocalDate desde, LocalDate hasta) {
        return !reserva.getFechaViaje().isBefore(desde) && !reserva.getFechaViaje().isAfter(hasta);
    }
    private void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) throw new IllegalArgumentException("El periodo de fechas no es válido");
    }
}
