package co.edu.uniquindio.poo.model;
public class Administrador extends Usuario {
        private final String nivelPermisos;
        public Administrador(String id, String nombre, String telefono, String correo, String nivelPermisos) {
            this(id,nombre,telefono,correo,nivelPermisos,"1234");
        }
        public Administrador(String id, String nombre, String telefono, String correo, String nivelPermisos, String contrasena) {
            super(id,nombre,telefono,correo,contrasena);
            this.nivelPermisos = requerido(nivelPermisos,"Nivel de permisos obligatorio");
        }
        public String getNivelPermisos() {
            return nivelPermisos;
        }
        @Override public String obtenerRol() {
            return "ADMINISTRADOR";
        }
        @Override public String mostrarMenuEspecifico() {
            return "Administrar usuarios, productos, reservas y reportes";
        }
    }

