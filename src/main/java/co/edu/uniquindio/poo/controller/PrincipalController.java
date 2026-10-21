package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Actividad;
import co.edu.uniquindio.poo.model.Administrador;
import co.edu.uniquindio.poo.model.CanalVenta;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Dificultad;
import co.edu.uniquindio.poo.model.EstadoActividad;
import co.edu.uniquindio.poo.model.EstadoReserva;
import co.edu.uniquindio.poo.model.Guia;
import co.edu.uniquindio.poo.model.Idioma;
import co.edu.uniquindio.poo.model.IProductoTuristico;
import co.edu.uniquindio.poo.model.MunicipioQuindio;
import co.edu.uniquindio.poo.model.PaqueteTuristico;
import co.edu.uniquindio.poo.model.Pais;
import co.edu.uniquindio.poo.model.PropuestaPaquete;
import co.edu.uniquindio.poo.model.Reserva;
import co.edu.uniquindio.poo.model.ReservaBuilder;
import co.edu.uniquindio.poo.model.ResultadoCancelacion;
import co.edu.uniquindio.poo.model.ResultadoConfirmacion;
import co.edu.uniquindio.poo.model.RutaVerdeQuindio;
import co.edu.uniquindio.poo.model.Usuario;
import co.edu.uniquindio.poo.model.ValidadorLuhn;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/** Punto de entrada entre las vistas y las reglas de negocio. */
public class PrincipalController {
    private final RutaVerdeQuindio sistema = RutaVerdeQuindio.getInstancia();

    public Usuario ingresar(String documento, String contrasena) {
        Usuario usuario = sistema.getGestorUsuarios().autenticar(documento, contrasena);
        if (usuario == null) {
            throw new IllegalArgumentException("Documento o contraseña incorrectos");
        }
        return usuario;
    }

    public List<Cliente> clientes(Usuario actor) {
        exigirAdministrador(actor);
        return sistema.getGestorUsuarios().obtenerClientes();
    }

    public List<Guia> guias(Usuario actor) {
        exigirAdministrador(actor);
        return sistema.getGestorUsuarios().obtenerGuias();
    }

    public List<IProductoTuristico> productos() {
        return sistema.getGestorProductos().obtenerCatalogo();
    }

    public List<Guia> guiasDisponibles() {
        return sistema.getGestorUsuarios().obtenerGuias().stream()
                .filter(Guia::isDisponible)
                .toList();
    }

    public List<Reserva> reservas(Usuario actor) {
        if (actor instanceof Administrador) {
            return sistema.getGestorReservas().obtenerTodasLasReservas();
        }
        if (actor instanceof Cliente cliente) {
            return sistema.getGestorReservas().obtenerReservasPorCliente(cliente.getIdentificacion());
        }
        if (actor instanceof Guia guia) {
            return sistema.getGestorReservas().obtenerReservasPorGuia(guia.getIdentificacion()).stream()
                    .filter(reserva -> reserva.getEstado() == EstadoReserva.CONFIRMADA
                            || reserva.getEstado() == EstadoReserva.CANCELADA)
                    .toList();
        }
        throw new SecurityException("Perfil sin acceso a reservas");
    }

    public List<PropuestaPaquete> propuestasPendientes(Usuario actor) {
        exigirAdministrador(actor);
        return sistema.getGestorProductos().obtenerPropuestasPendientes();
    }

    public void registrarCliente(Usuario actor, String id, String nombre, String telefono,
                                 String correo, Pais pais, Idioma idioma, String contrasena) {
        exigirAdministrador(actor);
        validarContrasena(contrasena);
        sistema.getGestorUsuarios().registrarCliente(
                new Cliente(id, nombre, telefono, correo, pais, idioma, contrasena));
    }

    /** El registro público solo permite crear cuentas de cliente. */
    public Cliente registrarClientePublico(String id, String nombre, String telefono,
                                           String correo, Pais pais, Idioma idioma, String contrasena) {
        validarContrasena(contrasena);
        Cliente cliente = new Cliente(id, nombre, telefono, correo, pais, idioma, contrasena);
        sistema.getGestorUsuarios().registrarCliente(cliente);
        return cliente;
    }

    public void registrarGuia(Usuario actor, String id, String nombre, String telefono, String correo,
                              Set<Idioma> idiomas, BigDecimal tarifa, String contrasena) {
        exigirAdministrador(actor);
        validarContrasena(contrasena);
        sistema.getGestorUsuarios().registrarGuia(
                new Guia(id, nombre, telefono, correo, idiomas, tarifa, contrasena));
    }

    public void registrarActividad(Usuario actor, String codigo, String nombre, MunicipioQuindio municipio,
                                   double horas, double precio, int cupos, Dificultad dificultad) {
        exigirAdministrador(actor);
        sistema.getGestorProductos().registrarProducto(new Actividad(codigo, nombre, municipio, horas,
                precio, cupos, dificultad, EstadoActividad.DISPONIBLE));
    }

    public void registrarPaquete(Usuario actor, String codigo, String nombre, double descuento,
                                 List<IProductoTuristico> productos) {
        exigirAdministrador(actor);
        sistema.getGestorProductos().registrarProducto(crearPaquete(codigo, nombre, descuento, productos));
    }

    public PropuestaPaquete proponerPaquete(Cliente actor, String codigo, String nombre, double descuento,
                                            List<IProductoTuristico> productos) {
        exigirCliente(actor);
        return sistema.getGestorProductos().proponerPaquete(actor,
                crearPaquete(codigo, nombre, descuento, productos));
    }

    public PropuestaPaquete proponerPaquete(Cliente actor, String nombre, double descuento,
                                            List<IProductoTuristico> productos) {
        String codigo = "PROP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return proponerPaquete(actor, codigo, nombre, descuento, productos);
    }

    public PropuestaPaquete proponerPaquete(Cliente actor, String nombre, double descuento,
                                            List<IProductoTuristico> productos, BigDecimal precioSugerido) {
        exigirCliente(actor);
        PaqueteTuristico paquete = crearPaquete(
                "PROP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT),
                nombre, descuento, productos);
        return sistema.getGestorProductos().proponerPaquete(actor, paquete, precioSugerido);
    }

    private PaqueteTuristico crearPaquete(String codigo, String nombre, double descuento,
                                         List<IProductoTuristico> productos) {
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("Selecciona al menos una actividad");
        }
        PaqueteTuristico paquete = new PaqueteTuristico(codigo, nombre, descuento);
        productos.forEach(paquete::agregarProducto);
        return paquete;
    }

    public void aprobarPropuesta(Usuario actor, String codigo) {
        exigirAdministrador(actor);
        sistema.getGestorProductos().aprobarPropuesta(codigo);
    }

    public void rechazarPropuesta(Usuario actor, String codigo) {
        exigirAdministrador(actor);
        sistema.getGestorProductos().rechazarPropuesta(codigo);
    }

    public void eliminarUsuario(Usuario actor, String id) {
        exigirAdministrador(actor);
        if (!sistema.getGestorUsuarios().eliminarUsuario(id)) {
            throw new IllegalArgumentException("No se eliminó la cuenta seleccionada");
        }
    }

    public void eliminarProducto(Usuario actor, String codigo) {
        exigirAdministrador(actor);
        if (!sistema.getGestorProductos().eliminarProducto(codigo)) {
            throw new IllegalArgumentException("No se eliminó el producto seleccionado");
        }
    }

    public ResultadoConfirmacion confirmarReserva(Usuario actor, Cliente cliente, IProductoTuristico producto,
                                                   LocalDate fecha, int viajeros, Guia guia,
                                                   boolean transporte, boolean seguro, String observaciones,
                                                   String tarjeta, CanalVenta canal) {
        if (!(actor instanceof Administrador)
                && !(actor instanceof Cliente && actor.equals(cliente))) {
            throw new SecurityException("Solo el cliente puede reservar para sí mismo o un administrador para terceros");
        }
        Reserva reserva = new ReservaBuilder().cliente(cliente).producto(producto).fechaViaje(fecha)
                .viajeros(viajeros).guia(guia).transporte(transporte).seguro(seguro)
                .observaciones(observaciones).build();
        return sistema.getGestorReservas().confirmar(reserva, tarjeta, canal);
    }

    public Reserva solicitarReserva(Cliente actor, IProductoTuristico producto, LocalDate fecha, int viajeros,
                                    Guia guia, boolean transporte, boolean seguro, String observaciones,
                                    String tarjeta) {
        exigirCliente(actor);
        Reserva reserva = new ReservaBuilder().cliente(actor).producto(producto).fechaViaje(fecha)
                .viajeros(viajeros).guia(guia).transporte(transporte).seguro(seguro)
                .observaciones(observaciones).build();
        return sistema.getGestorReservas().solicitar(reserva, tarjeta);
    }

    public ResultadoConfirmacion aprobarSolicitud(Usuario actor, long numero) {
        exigirAdministrador(actor);
        return sistema.getGestorReservas().aprobarSolicitud(numero, CanalVenta.ASESOR);
    }

    public void rechazarSolicitud(Usuario actor, long numero) {
        exigirAdministrador(actor);
        sistema.getGestorReservas().rechazarSolicitud(numero);
    }

    public ResultadoCancelacion cancelarReserva(Usuario actor, long numero, String motivo) {
        return sistema.getGestorReservas().cancelarReserva(numero, actor, motivo);
    }

    public BigDecimal ingresos(Usuario actor, LocalDate desde, LocalDate hasta) {
        exigirAdministrador(actor);
        return sistema.getGestorReservas().calcularIngresos(desde, hasta);
    }

    public Optional<IProductoTuristico> masPopular(Usuario actor) {
        exigirAdministrador(actor);
        return sistema.getGestorReservas().obtenerProductoMasPopular();
    }

    public Optional<IProductoTuristico> masPopular(Usuario actor, LocalDate desde, LocalDate hasta) {
        exigirAdministrador(actor);
        return sistema.getGestorReservas().obtenerProductoMasPopular(desde, hasta);
    }

    public Optional<Map.Entry<IProductoTuristico, Integer>> reporteProductoMasPopular(
            Usuario actor, LocalDate desde, LocalDate hasta) {
        exigirAdministrador(actor);
        return sistema.getGestorReservas().obtenerResumenProductoMasPopular(desde, hasta);
    }

    public static boolean tarjetaValida(String tarjeta) {
        return ValidadorLuhn.validarLuhn(tarjeta);
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    private void exigirCliente(Cliente actor) {
        if (actor == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
    }

    private void exigirAdministrador(Usuario actor) {
        if (!(actor instanceof Administrador)) {
            throw new SecurityException("Solo el administrador puede realizar esta operación");
        }
    }
}
