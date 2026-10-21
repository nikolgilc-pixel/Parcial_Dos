package co.edu.uniquindio.poo.model;

public class VoucherKiosco implements IVoucher {
    public String emitir(Reserva r,String agencia,String rnt){
        return agencia+" · RNT "+rnt+" · Ticket RV-"+r.getNumero()+" · "
                +r.getProducto().obtenerNombre()+" · "+r.getFechaViaje()+" · Código: RVQ"
                +String.format("%06d",r.getNumero());}
}

