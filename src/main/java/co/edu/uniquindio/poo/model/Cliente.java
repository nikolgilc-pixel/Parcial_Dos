package co.edu.uniquindio.poo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public class Cliente extends Usuario {
        private final Pais paisOrigen;
        private final Idioma idiomaPreferido;
        private final List<Reserva> historialReservas = new ArrayList<>();
        public Cliente(String documento, String nombre, String telefono, String correo, Pais pais, Idioma idioma) {
            this(documento,nombre,telefono,correo,pais,idioma,"1234");
        }
        public Cliente(String documento, String nombre, String telefono, String correo, Pais pais,
                       Idioma idioma, String contrasena) {
            super(documento, nombre, telefono, correo, contrasena);
            paisOrigen = Objects.requireNonNull(pais);
            idiomaPreferido = Objects.requireNonNull(idioma);
        }
        public Pais getPaisOrigen() {
            return paisOrigen;
        }
        public Idioma getIdiomaPreferido() {
            return idiomaPreferido;
        }
        public List<Reserva> getHistorialReservas() {
            return Collections.unmodifiableList(historialReservas);
        }
        public void agregarReserva(Reserva r) {
            historialReservas.add(Objects.requireNonNull(r));
        }
        public void quitarReserva(Reserva r) {
            historialReservas.remove(r);
        }
        @Override public String obtenerRol() {
            return "CLIENTE"; }
        @Override public String mostrarMenuEspecifico() {
            return "Consultar productos y realizar reservas";
        }
        @Override public String toString() {
            return getNombreCompleto() + " (" + getIdentificacion() + ")";
        }
    }

