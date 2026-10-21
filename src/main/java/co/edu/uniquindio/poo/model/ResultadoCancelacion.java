package co.edu.uniquindio.poo.model;

import java.math.BigDecimal;

public record ResultadoCancelacion(boolean exitosa, String mensaje, BigDecimal cargoRetenido,
                                   BigDecimal reembolsoCalculado) {
}
