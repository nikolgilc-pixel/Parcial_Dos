package co.edu.uniquindio.poo.model;

import java.math.BigDecimal;
import java.util.Set;

public final class RutaVerdeQuindio {
    private static final RutaVerdeQuindio INSTANCIA = new RutaVerdeQuindio();
    private final GestorUsuario gestorUsuarios = new GestorUsuario();
    private final GestorProducto gestorProductos = new GestorProducto();
    private final GestorReservas gestorReservas = new GestorReservas(
            new PagoAndinoAdapter(new PagoAndinoAPI()), new NotificadorCorreoConsola(),
            "RutaVerde Quindío", "RNT-000000");

    private RutaVerdeQuindio() {
        cargarCuentasDeDemostracion();
        cargarCatalogoDeDemostracion();
    }

    private void cargarCuentasDeDemostracion() {
        gestorUsuarios.registrarAdministrador(new Administrador("admin", "Administradora RutaVerde",
                "6060000000", "admin@rutaverde.co", "SUPER_ADMIN", "admin123"));
        gestorUsuarios.registrarCliente(new Cliente("1001", "Valentina Cliente", "3000000000",
                "cliente@rutaverde.co", Pais.COLOMBIA, Idioma.ESPANOL, "Cliente123!"));
        gestorUsuarios.registrarGuia(new Guia("2001", "Santiago Guía", "3100000000", "guia@rutaverde.co",
                Set.of(Idioma.ESPANOL, Idioma.INGLES), BigDecimal.valueOf(80000), "Guia123!"));
    }

    private void cargarCatalogoDeDemostracion() {
        Actividad cocora = new Actividad("ACT-001", "Caminata por el Valle de Cocora",
                MunicipioQuindio.SALENTO, 6, 55000, 15, Dificultad.MEDIA, EstadoActividad.DISPONIBLE);
        Actividad cafe = new Actividad("ACT-002", "Recorrido cafetero", MunicipioQuindio.FILANDIA,
                4, 42000, 12, Dificultad.BAJA, EstadoActividad.DISPONIBLE);
        gestorProductos.registrarProducto(cocora);
        gestorProductos.registrarProducto(cafe);
        PaqueteTuristico esencia = new PaqueteTuristico("PKG-001", "Esencia del Quindío", 8);
        esencia.agregarProducto(cocora);
        esencia.agregarProducto(cafe);
        gestorProductos.registrarProducto(esencia);
    }

    public static RutaVerdeQuindio getInstancia() { return INSTANCIA; }
    public GestorUsuario getGestorUsuarios() { return gestorUsuarios; }
    public GestorProducto getGestorProductos() { return gestorProductos; }
    public GestorReservas getGestorReservas() { return gestorReservas; }
}
