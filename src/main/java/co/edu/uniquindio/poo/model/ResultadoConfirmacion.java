package co.edu.uniquindio.poo.model;

public record ResultadoConfirmacion(boolean exitoso, String mensaje, Reserva reserva, String voucher) {
    public static ResultadoConfirmacion exito(Reserva reserva, String voucher) {
        return new ResultadoConfirmacion(true, "Reserva confirmada", reserva, voucher);
    }

    public static ResultadoConfirmacion fallo(String mensaje) {
        return new ResultadoConfirmacion(false, mensaje, null, null);
    }
}
