package cl.duoc.ms_historial_bs.client;

import cl.duoc.ms_historial_bs.model.dto.CitaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "citas-bs", url = "${citas-bs.url:http://localhost:8091/api/v1/citas}")
public interface CitasBsRestClient {

    @GetMapping("/{id}")
    CitaDTO obtenerCita(@PathVariable Long id);

}
