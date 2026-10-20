package co.edu.uniquindio.poo.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestorUsuario {
    private final Map<String,Cliente> clientes=new LinkedHashMap<>();
    private final Map<String,Guia> guias=new LinkedHashMap<>();
    private final Map<String,Administrador> administradores=new LinkedHashMap<>();

    public void registrarCliente(Cliente cliente){registrar(clientes,cliente);}
    public void registrarGuia(Guia guia){registrar(guias,guia);}
    public void registrarAdministrador(Administrador admin){registrar(administradores,admin);}
    private <T extends Usuario> void registrar(Map<String,T> mapa,T usuario){if(existeEnOtroRol(usuario.getIdentificacion()))throw new IllegalArgumentException("Ya existe una cuenta con ese documento");mapa.put(usuario.getIdentificacion(),usuario);}
    private boolean existeEnOtroRol(String id){return clientes.containsKey(id)||guias.containsKey(id)||administradores.containsKey(id);}
    public Usuario autenticar(String documento,String contrasena){if(documento==null||contrasena==null)return null;Usuario u=clientes.get(documento);if(u==null)u=guias.get(documento);if(u==null)u=administradores.get(documento);return u!=null&&u.validarContrasena(contrasena)?u:null;}
    public List<Cliente> obtenerClientes(){return List.copyOf(clientes.values());}
    public List<Guia> obtenerGuias(){return List.copyOf(guias.values());}
    public List<Administrador> obtenerAdministradores(){return List.copyOf(administradores.values());}
    public Cliente buscarCliente(String id){return clientes.get(id);}
    public Guia buscarGuia(String id){return guias.get(id);}
    public boolean eliminarUsuario(String id){return clientes.remove(id)!=null||guias.remove(id)!=null||(administradores.size()>1&&administradores.remove(id)!=null);}
}
