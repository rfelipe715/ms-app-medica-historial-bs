package cl.duoc.ms_historial_bs.model.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HistorialConDetallesDTO {

    private Long id;
    private Long pacienteId;
    private Long citaId;
    private String fecha;
    private String diagnostico;
    private String observaciones;
    
    // Datos relacionados
    private PacienteBffDto paciente;
    private CitaDTO cita;

}
