package cl.duoc.ms_historial_bs.controller;

import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialConDetallesDTO;
import cl.duoc.ms_historial_bs.service.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historiales")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @PostMapping()
    public void guardarHistorial(@RequestBody HistorialDTO historialDTO) {
        historialService.registrarHistorial(historialDTO);
    }

    @GetMapping
    public List<HistorialDTO> obtenerHistoriales() {
        return historialService.obtenerHistoriales();
    }

    @GetMapping("/detalles")
    public List<HistorialConDetallesDTO> obtenerHistorialesConDetalles() {
        return historialService.obtenerHistorialesConDetalles();
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<HistorialConDetallesDTO> obtenerHistorialConDetalles(@PathVariable Long id) {
        HistorialConDetallesDTO historial = historialService.obtenerHistorialConDetalles(id);
        if (historial != null) {
            return ResponseEntity.ok(historial);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public void eliminarHistorial(@RequestParam Long id) {
        historialService.eliminarHistorial(id);
    }

    @PutMapping
    public HistorialUpdateDTO actualizarHistorial (@RequestBody HistorialUpdateDTO historial) {
        return historialService.actualizarHistorial(historial);
    }
}
