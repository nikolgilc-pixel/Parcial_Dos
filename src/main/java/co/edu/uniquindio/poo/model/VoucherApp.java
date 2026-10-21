package co.edu.uniquindio.poo.model;

public class VoucherApp implements IVoucher {
    public String emitir(Reserva r,String agencia,String rnt){
        return "{"+"\"agencia\":\""+agencia+"\",\"rnt\":\""+rnt+"\",\"reserva\":"
                +r.getNumero()+",\"producto\":\""+r.getProducto().obtenerNombre()+"\",\"fecha\":\""+r.getFechaViaje()+
                "\",\"total\":\""+r.calcularTotal()+"\",\"politica\":\"" +
                "Cancelación sin costo hasta 48 horas antes del viaje\"}";}
}
