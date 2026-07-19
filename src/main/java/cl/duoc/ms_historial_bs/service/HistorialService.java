package cl.duoc.ms_historial_bs.service;

import cl.duoc.ms_historial_bs.client.HistorialDbRestClient;
import cl.duoc.ms_historial_bs.client.PacientesBsRestClient;
import cl.duoc.ms_historial_bs.client.CitasBsRestClient;
import cl.duoc.ms_historial_bs.exception.HistorialNotFoundException;
import cl.duoc.ms_historial_bs.exception.ServicioNoDisponibleException;
import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialConDetallesDTO;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    private static final Logger log = LoggerFactory.getLogger(HistorialService.class);

    @Autowired
    HistorialDbRestClient historialDbRestClient;

    @Autowired
    private PacientesBsRestClient pacientesBsRestClient;

    @Autowired
    private CitasBsRestClient citasBsRestClient;

    public HistorialDTO registrarHistorial(HistorialDTO historialDTO) {
        try {
            HistorialDTO guardado = historialDbRestClient.guardarHistorial(historialDTO);
            log.info("Historial registrado con id={}, pacienteId={}, citaId={}",
                    guardado.getId(), guardado.getPacienteId(), guardado.getCitaId());
            return guardado;
        } catch (FeignException e) {
            log.error("ms-historial-db no disponible al registrar historial para pacienteId={}: {}",
                    historialDTO.getPacienteId(), e.getMessage());
            throw new ServicioNoDisponibleException("ms-historial-db", e);
        }
    }

    public List<HistorialDTO> obtenerHistoriales() {
        return historialDbRestClient.obtenerHistoriales();
    }

    public HistorialDTO obtenerHistorialPorId(Long id) {
        try {
            return historialDbRestClient.obtenerHistorialPorId(id);
        } catch (FeignException.NotFound e) {
            log.warn("Historial id={} no encontrado en ms-historial-db", id);
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            log.error("ms-historial-db no disponible al buscar historial id={}: {}", id, e.getMessage());
            throw new ServicioNoDisponibleException("ms-historial-db", e);
        }
    }

    public List<HistorialConDetallesDTO> obtenerHistorialesConDetalles() {
        return historialDbRestClient.obtenerHistoriales().stream()
            .map(this::enriquecerHistorial)
            .collect(Collectors.toList());
    }

    public void eliminarHistorial(Long id) {
        try {
            historialDbRestClient.eliminarHistorial(id);
            log.info("Historial id={} eliminado correctamente", id);
        } catch (FeignException.NotFound e) {
            log.warn("Intento de eliminar un historial inexistente, id={}", id);
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            log.error("ms-historial-db no disponible al eliminar historial id={}: {}", id, e.getMessage());
            throw new ServicioNoDisponibleException("ms-historial-db", e);
        }
    }

    public HistorialDTO actualizarHistorial(Long id, HistorialUpdateDTO historial) {
        try {
            HistorialDTO actualizado = historialDbRestClient.actualizarHistorial(id, historial);
            log.info("Historial id={} actualizado correctamente", id);
            return actualizado;
        } catch (FeignException.NotFound e) {
            log.warn("Intento de actualizar un historial inexistente, id={}", id);
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            log.error("ms-historial-db no disponible al actualizar historial id={}: {}", id, e.getMessage());
            throw new ServicioNoDisponibleException("ms-historial-db", e);
        }
    }

    public HistorialConDetallesDTO obtenerHistorialConDetalles(Long id) {
        HistorialDTO historial = historialDbRestClient.obtenerHistoriales().stream()
            .filter(h -> h.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (historial != null) {
            return enriquecerHistorial(historial);
        }
        return null;
    }

    private HistorialConDetallesDTO enriquecerHistorial(HistorialDTO historialDTO) {
        HistorialConDetallesDTO detalles = new HistorialConDetallesDTO();
        detalles.setId(historialDTO.getId());
        detalles.setPacienteId(historialDTO.getPacienteId());
        detalles.setCitaId(historialDTO.getCitaId());
        detalles.setFecha(historialDTO.getFecha());
        detalles.setDiagnostico(historialDTO.getDiagnostico());
        detalles.setObservaciones(historialDTO.getObservaciones());
        
        try {
            detalles.setPaciente(pacientesBsRestClient.obtenerPaciente(historialDTO.getPacienteId()));
        } catch (Exception e) {
            log.warn("No se pudo enriquecer el historial id={} con datos del paciente id={}: {}",
                    historialDTO.getId(), historialDTO.getPacienteId(), e.getMessage());
        }

        try {
            detalles.setCita(citasBsRestClient.obtenerCita(historialDTO.getCitaId()));
        } catch (Exception e) {
            log.warn("No se pudo enriquecer el historial id={} con datos de la cita id={}: {}",
                    historialDTO.getId(), historialDTO.getCitaId(), e.getMessage());
        }
        
        return detalles;
    }
}
