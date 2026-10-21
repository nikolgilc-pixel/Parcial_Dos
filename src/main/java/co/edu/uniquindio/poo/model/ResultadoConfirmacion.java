package co.edu.uniquindio.poo.model;

public class ResultadoConfirmacion {
    boolean exitoso,String mensaje,Reserva reserva,String voucher)

    {
        public static ResultadoConfirmacion exito (Reserva r, String v){
        return new ResultadoConfirmacion(true, "Reserva confirmada", r, v);
    }
        public static ResultadoConfirmacion fallo (String m){
        return new ResultadoConfirmacion(false, m, null, null);
    }

    }
}
