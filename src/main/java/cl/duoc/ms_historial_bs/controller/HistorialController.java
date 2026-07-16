package cl.duoc.ms_historial_bs.controller;

import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialConDetallesDTO;
import cl.duoc.ms_historial_bs.service.HistorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historiales")
@Tag(name = "Historial (BS)", description = "Lógica de negocio del historial clínico: consolida datos de pacientes y citas")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @Operation(summary = "Registrar un nuevo historial", description = "Valida y persiste un nuevo historial clínico a través de la capa de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Historial registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud"),
            @ApiResponse(responseCode = "502", description = "El servicio de datos de historial no respondió correctamente")
    })
    @PostMapping
    public ResponseEntity<HistorialDTO> guardarHistorial(@Valid @RequestBody HistorialDTO historialDTO) {
        HistorialDTO historialCreado = historialService.registrarHistorial(historialDTO);
        return new ResponseEntity<>(historialCreado, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los historiales", description = "Retorna todos los historiales clínicos registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de historiales obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<HistorialDTO>> obtenerHistoriales() {
        return ResponseEntity.ok(historialService.obtenerHistoriales());
    }

    @Operation(summary = "Buscar historial por ID", description = "Retorna un historial clínico específico según su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un historial con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistorialDTO> obtenerHistorialPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historialService.obtenerHistorialPorId(id));
    }

    @Operation(summary = "Listar historiales con detalles", description = "Retorna todos los historiales enriquecidos con los datos del paciente y la cita asociada, consultados a ms-pacientes-bs y ms-citas-bs.")
    @ApiResponse(responseCode = "200", description = "Lista de historiales con detalles obtenida exitosamente")
    @GetMapping("/detalles")
    public ResponseEntity<List<HistorialConDetallesDTO>> obtenerHistorialesConDetalles() {
        return ResponseEntity.ok(historialService.obtenerHistorialesConDetalles());
    }

    @Operation(summary = "Buscar historial con detalles", description = "Retorna un historial específico enriquecido con los datos del paciente y la cita asociada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un historial con el ID indicado")
    })
    @GetMapping("/{id}/detalles")
    public ResponseEntity<HistorialConDetallesDTO> obtenerHistorialConDetalles(@PathVariable Long id) {
        HistorialConDetallesDTO historial = historialService.obtenerHistorialConDetalles(id);
        if (historial != null) {
            return ResponseEntity.ok(historial);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un historial", description = "Elimina de forma permanente un historial clínico identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Historial eliminado exitosamente, sin contenido de respuesta"),
            @ApiResponse(responseCode = "404", description = "No existe un historial con el ID indicado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarHistorial(@PathVariable Long id) {
        historialService.eliminarHistorial(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar un historial existente", description = "Actualiza los datos de un historial clínico ya registrado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No existe un historial con el ID indicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<HistorialDTO> actualizarHistorial(@PathVariable Long id, @Valid @RequestBody HistorialUpdateDTO historial) {
        return ResponseEntity.ok(historialService.actualizarHistorial(id, historial));
    }
}
