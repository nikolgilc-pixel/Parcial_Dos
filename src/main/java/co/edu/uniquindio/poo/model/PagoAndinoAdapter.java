import co.edu.uniquindio.poo.model.IPasarelaPago;
import co.edu.uniquindio.poo.model.PagoAndinoAPI;
import co.edu.uniquindio.poo.model.ResultadoPago;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class PagoAndinoAdapter implements IPasarelaPago {
    private final PagoAndinoAPI api;
    public PagoAndinoAdapter(PagoAndinoAPI api){
        this.api= Objects.requireNonNull(api);
    }
    public ResultadoPago procesarPago(String tarjeta, BigDecimal pesos, String referencia){
        if(pesos==null||pesos.signum()<0)
            return ResultadoPago.rechazado("El valor del cobro no es válido");
        long centavos;
        try{centavos=pesos.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.UNNECESSARY).longValueExact();}
        catch(ArithmeticException e){
            return ResultadoPago.rechazado("El monto no se puede representar en centavos");}
        int codigo=api.procesarCobro(tarjeta,centavos,referencia);
        return switch(codigo){
            case 0->ResultadoPago.aprobado(api.ultimaAutorizacion());
            case 51->ResultadoPago.rechazado("Fondos insuficientes");
            case 14->ResultadoPago.rechazado("Tarjeta inválida");
            case 91->ResultadoPago.rechazado("Pasarela no disponible");
            default->ResultadoPago.rechazado("Respuesta desconocida de PagoAndino: "+codigo);
        }
        ;
    }
}
