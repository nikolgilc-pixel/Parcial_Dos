package co.edu.uniquindio.poo.model;

import java.math.BigDecimal;

public interface IPasarelaPago {
    ResultadoPago procesarPago(String numeroTarjeta, BigDecimal valorPesos, String referencia);
}


