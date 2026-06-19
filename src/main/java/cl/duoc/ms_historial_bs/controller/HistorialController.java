package cl.duoc.ms_historial_bs.controller;

import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bs.service.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @DeleteMapping("/{id}")
    public void eliminarHistorial(@RequestParam Long id) {
        historialService.eliminarHistorial(id);
    }

    @PutMapping
    public HistorialUpdateDTO actualizarHistorial (@RequestBody HistorialUpdateDTO historial) {
        return historialService.actualizarHistorial(historial);
    }
}
