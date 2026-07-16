package cl.duoc.ms_historial_bs.client;

import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "historial-db", url = "${historial-db.url:http://localhost:8095/api/v1/historiales}")
public interface HistorialDbRestClient {

    @PostMapping
    HistorialDTO guardarHistorial(@RequestBody HistorialDTO historialDTO);

    @GetMapping
    List<HistorialDTO> obtenerHistoriales();

    @GetMapping("/{id}")
    HistorialDTO obtenerHistorialPorId(@PathVariable("id") Long id);

    @DeleteMapping("/{id}")
    void eliminarHistorial(@PathVariable("id") Long id);

    @PutMapping("/{id}")
    HistorialDTO actualizarHistorial(@PathVariable("id") Long id, @RequestBody HistorialUpdateDTO historial);
}
