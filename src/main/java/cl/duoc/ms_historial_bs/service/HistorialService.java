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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    @Autowired
    HistorialDbRestClient historialDbRestClient;

    @Autowired
    private PacientesBsRestClient pacientesBsRestClient;

    @Autowired
    private CitasBsRestClient citasBsRestClient;

    public HistorialDTO registrarHistorial(HistorialDTO historialDTO) {
        try {
            return historialDbRestClient.guardarHistorial(historialDTO);
        } catch (FeignException e) {
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
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
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
        } catch (FeignException.NotFound e) {
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-historial-db", e);
        }
    }

    public HistorialDTO actualizarHistorial(Long id, HistorialUpdateDTO historial) {
        try {
            return historialDbRestClient.actualizarHistorial(id, historial);
        } catch (FeignException.NotFound e) {
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
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
            // Silenciar error si no encuentra paciente
        }
        
        try {
            detalles.setCita(citasBsRestClient.obtenerCita(historialDTO.getCitaId()));
        } catch (Exception e) {
            // Silenciar error si no encuentra cita
        }
        
        return detalles;
    }
}
