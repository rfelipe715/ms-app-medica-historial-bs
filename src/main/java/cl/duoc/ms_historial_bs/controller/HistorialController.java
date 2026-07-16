package cl.duoc.ms_historial_bs.controller;

import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialConDetallesDTO;
import cl.duoc.ms_historial_bs.service.HistorialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historiales")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @PostMapping
    public ResponseEntity<HistorialDTO> guardarHistorial(@Valid @RequestBody HistorialDTO historialDTO) {
        HistorialDTO historialCreado = historialService.registrarHistorial(historialDTO);
        return new ResponseEntity<>(historialCreado, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HistorialDTO>> obtenerHistoriales() {
        return ResponseEntity.ok(historialService.obtenerHistoriales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistorialDTO> obtenerHistorialPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historialService.obtenerHistorialPorId(id));
    }

    @GetMapping("/detalles")
    public ResponseEntity<List<HistorialConDetallesDTO>> obtenerHistorialesConDetalles() {
        return ResponseEntity.ok(historialService.obtenerHistorialesConDetalles());
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
    public ResponseEntity<Void> eliminarHistorial(@PathVariable Long id) {
        historialService.eliminarHistorial(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistorialDTO> actualizarHistorial(@PathVariable Long id, @Valid @RequestBody HistorialUpdateDTO historial) {
        return ResponseEntity.ok(historialService.actualizarHistorial(id, historial));
    }
}
