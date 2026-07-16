package cl.duoc.ms_historial_bs.model.dto;

import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "La fecha debe tener el formato yyyy-MM-dd")
    private String fecha;

    private String diagnostico;

    private String observaciones;

}
