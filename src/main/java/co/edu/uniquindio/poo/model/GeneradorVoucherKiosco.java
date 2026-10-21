package co.edu.uniquindio.poo.model;

public class GeneradorVoucherKiosco extends CreadorVoucher {
    protected IVoucher crearVoucher(){
        return new VoucherKiosco();}
}
