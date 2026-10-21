package co.edu.uniquindio.poo.model;

public class GeneradorVoucherAsesor extends CreadorVoucher {
    protected IVoucher crearVoucher(){
        return new VoucherAsesor();}
}
