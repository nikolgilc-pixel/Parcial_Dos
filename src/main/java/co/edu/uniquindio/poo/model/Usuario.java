package co.edu.uniquindio.poo.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

public abstract class Usuario {
        private final String identificacion;
        private String nombreCompleto;
        private String telefono;
        private String correo;
        private final String contrasenaHash;
        protected Usuario(String identificacion, String nombreCompleto, String telefono, String correo) {
            this(identificacion,nombreCompleto,telefono,correo,"1234");
        }
        protected Usuario(String identificacion, String nombreCompleto, String telefono, String correo, String contrasena) {
            this.identificacion = requerido(identificacion, "La identificación es obligatoria");
            this.nombreCompleto = requerido(nombreCompleto, "El nombre es obligatorio");
            this.telefono = requerido(telefono, "El teléfono es obligatorio");
            this.correo = requerido(correo, "El correo es obligatorio");
            this.contrasenaHash = hash(requerido(contrasena,"La contraseña es obligatoria"));
        }
        private static String hash(String valor){
            try
            {
                return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8)));
            }
            catch(NoSuchAlgorithmException e){
                throw new IllegalStateException(e);
            }
        }
        public boolean validarContrasena(String candidata){
            return candidata!=null&&MessageDigest.isEqual(contrasenaHash.getBytes(StandardCharsets.UTF_8),hash(candidata).getBytes(StandardCharsets.UTF_8));
        }
        protected static String requerido(String valor, String mensaje) {
            if (valor == null || valor.isBlank()) throw new IllegalArgumentException(mensaje);
            return valor.trim();
        }
        public String getIdentificacion() {
            return identificacion;
        }
        public String getNombreCompleto() {
            return nombreCompleto;
        }
        public void setNombreCompleto(String v) {
            nombreCompleto = requerido(v, "Nombre obligatorio");
        }
        public String getTelefono() {
            return telefono;
        }
        public void setTelefono(String v) {
            telefono = requerido(v, "Teléfono obligatorio");
        }
        public String getCorreo() {
            return correo;
        }
        public void setCorreo(String v) {
            correo = requerido(v, "Correo obligatorio");
        }
        public abstract String obtenerRol();
        public abstract String mostrarMenuEspecifico();
        @Override public boolean equals(Object o) {
            return o instanceof co.edu.uniquindio.poo.model.Usuario u && identificacion.equals(u.identificacion);
        }
        @Override public int hashCode() {
            return Objects.hash(identificacion);
        }
    }

