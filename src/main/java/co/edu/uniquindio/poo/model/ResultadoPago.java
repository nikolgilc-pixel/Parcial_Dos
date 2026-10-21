package co.edu.uniquindio.poo.model;
public record ResultadoPago(boolean aprobado,String motivoRechazo,String codigoAutorizacion){
    public static co.edu.uniquindio.poo.model.ResultadoPago aprobado(String codigo){
        return new co.edu.uniquindio.poo.model.ResultadoPago(true,"",codigo);
    }public static co.edu.uniquindio.poo.model.ResultadoPago rechazado(String motivo){
        return new co.edu.uniquindio.poo.model.ResultadoPago(false,motivo,null);
    }
}


