package cl.duoc.ms_historial_bs.client;

import cl.duoc.ms_historial_bs.model.dto.PacienteBffDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pacientes-bs", url = "http://localhost:8082/api/v1/pacientes")
public interface PacientesBsRestClient {

    @GetMapping("/{id}")
    PacienteBffDto obtenerPaciente(@PathVariable Long id);

}
