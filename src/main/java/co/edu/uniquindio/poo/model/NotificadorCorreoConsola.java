package co.edu.uniquindio.poo.model;

public class NotificadorCorreoConsola implements ServicioNotificacion {
    public void enviar(String correo,String asunto,String contenido)
    {System.out.println("\n[Correo simulado] Para: "+correo+" | "+asunto+"\n"
            +contenido+"\n");
    }
}
