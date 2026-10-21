package co.edu.uniquindio.poo.model;

public abstract class CreadorVoucher {
    protected abstract IVoucher crearVoucher();

    public String emitir(Reserva r,String agencia,String rnt){
        return crearVoucher().emitir(r,agencia,rnt);}
}



