package co.edu.uniquindio.poo.model;

public class VoucherAsesor implements IVoucher {
    public String emitir(Reserva r,String agencia,String rnt){
        return "=== "+agencia+" | RNT "+rnt+" ===\nReserva "+r.getNumero()+" | "+r.getFechaViaje()+
                "\nCliente: "+r.getCliente().getNombreCompleto()+"\nProducto: "
                +r.getProducto().obtenerNombre()+"\nActividades: "
                +r.getProducto().listarActividades().stream().map(a->a.obtenerNombre()+
                " ("+a.getMunicipio()+")").toList()+"\nGuía: "+(r.getGuia()==null?
                "No solicitado":r.getGuia().getNombreCompleto())+"\nTransporte: "+r.incluyeTransporte()+
                " | Seguro: "+r.incluyeSeguro()+"\nViajeros: "+r.getViajeros()+" | Total: $"+
                r.calcularTotal()+"\nCancelación sin costo hasta 48 horas antes del viaje.";}
}
