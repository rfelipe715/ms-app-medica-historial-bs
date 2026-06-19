package cl.duoc.ms_historial_bs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HistorialUpdateDTO {

    private Long id;
    private Long pacienteId;
    private Long citaId;
    private String fecha;
    private String diagnostico;
    private String observaciones;

}
