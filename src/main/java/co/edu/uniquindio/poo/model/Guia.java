package co.edu.uniquindio.poo.model;

import java.math.BigDecimal;
import java.util.*;


    public class Guia extends Usuario {
        private final Set<Idioma> idiomas;
        private final BigDecimal tarifaPorDia;
        private boolean disponible = true;
        public Guia(String id, String nombre, String telefono, String correo, Set<Idioma> idiomas, BigDecimal tarifaPorDia) {
            this(id,nombre,telefono,correo,idiomas,tarifaPorDia,"1234");
        }
        public Guia(String id, String nombre, String telefono, String correo, Set<Idioma> idiomas, BigDecimal tarifaPorDia, String contrasena) {
            super(id, nombre, telefono, correo, contrasena);
            if (idiomas == null || idiomas.isEmpty()) throw new IllegalArgumentException("El guía debe hablar al menos un idioma");
            if (tarifaPorDia == null || tarifaPorDia.signum() < 0) throw new IllegalArgumentException("Tarifa diaria inválida");
            this.idiomas = EnumSet.copyOf(idiomas); this.tarifaPorDia = tarifaPorDia;
        }
        public Set<Idioma> getIdiomas() {
            return Collections.unmodifiableSet(idiomas);
        }
        public BigDecimal getTarifaPorDia() {
            return tarifaPorDia;
        }
        public boolean habla(Idioma idioma) {
            return idiomas.contains(idioma);
        }
        public boolean isDisponible() {
            return disponible;
        }
        public void cambiarDisponibilidad(boolean disponible) {
            this.disponible = disponible;
        }
        public BigDecimal calcularCosto(double horas) {
            if (horas <= 0) return BigDecimal.ZERO;
            long dias = (long)Math.ceil(horas / 8.0);
            return tarifaPorDia.multiply(BigDecimal.valueOf(dias));
        }
        public List<Reserva> consultarToursAsignados(Collection<Reserva> reservas) {
            return reservas.stream().filter(r -> this.equals(r.getGuia())).toList();
        }
        @Override public String obtenerRol() {
            return "GUIA";
        }
        @Override public String mostrarMenuEspecifico() { return "Consultar tours asignados";
        }
        @Override public String toString() {
            return getNombreCompleto() + " (" + String.join(", ", idiomas.stream().map(Enum::name).toList()) + ")";
        }
    }

