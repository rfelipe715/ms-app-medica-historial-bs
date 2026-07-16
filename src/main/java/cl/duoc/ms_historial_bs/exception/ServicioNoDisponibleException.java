package cl.duoc.ms_historial_bs.exception;

public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String servicio, Throwable causa) {
        super("El servicio '" + servicio + "' no respondió correctamente: " + causa.getMessage(), causa);
    }
}
