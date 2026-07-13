package cl.duoc.ms_historial_bs.client;

import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "historial-db", url = "${historial-db.url:http://localhost:8095/api/v1/historiales}")
public interface HistorialDbRestClient {

    @PostMapping
    public void guardarHistorial(@RequestBody HistorialDTO historialDTO);

    @GetMapping
    public List<HistorialDTO> obtenerHistoriales();

    @DeleteMapping
    public void eliminarHistorial(Long id);

    @PutMapping
    public HistorialUpdateDTO actualizarHistorial(@RequestBody HistorialUpdateDTO historial);
}
