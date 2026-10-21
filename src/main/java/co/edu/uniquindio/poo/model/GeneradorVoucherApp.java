package co.edu.uniquindio.poo.model;

public class GeneradorVoucherApp extends CreadorVoucher{
    protected IVoucher crearVoucher(){
        return new VoucherApp();}
}
