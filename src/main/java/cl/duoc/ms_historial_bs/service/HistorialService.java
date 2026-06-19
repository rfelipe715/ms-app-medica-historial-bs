package cl.duoc.ms_historial_bs.service;

import cl.duoc.ms_historial_bs.client.HistorialDbRestClient;
import cl.duoc.ms_historial_bs.client.PacientesBsRestClient;
import cl.duoc.ms_historial_bs.client.CitasBsRestClient;
import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialConDetallesDTO;
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

    public void registrarHistorial(HistorialDTO historialDTO) {
        historialDbRestClient.guardarHistorial(historialDTO);
    }

    public List<HistorialDTO> obtenerHistoriales() {
        return historialDbRestClient.obtenerHistoriales();
    }

    public List<HistorialConDetallesDTO> obtenerHistorialesConDetalles() {
        return historialDbRestClient.obtenerHistoriales().stream()
            .map(this::enriquecerHistorial)
            .collect(Collectors.toList());
    }

    public void eliminarHistorial(Long id) {
        historialDbRestClient.eliminarHistorial(id);
    }

    public HistorialUpdateDTO actualizarHistorial(HistorialUpdateDTO historial) {
        return historialDbRestClient.actualizarHistorial(historial);
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
