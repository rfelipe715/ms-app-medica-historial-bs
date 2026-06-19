package cl.duoc.ms_historial_bs.model.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PacienteBffDto {
    
    private Long id;
    private String run;
    private String numeroRegistro;
    private String numeroFichaClinica;
    private String nombres;
    private String apellidos;
    private String sexo;
    private String fechaNacimiento;
    private String direccion;
    private String telefonoContacto;
}
