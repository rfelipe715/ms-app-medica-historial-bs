package cl.duoc.ms_historial_bs.service;

import cl.duoc.ms_historial_bs.client.HistorialDbRestClient;
import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialService {

    @Autowired
    HistorialDbRestClient historialDbRestClient;

    public void registrarHistorial(HistorialDTO historialDTO) {
        historialDbRestClient.guardarHistorial(historialDTO);
    }

    public List<HistorialDTO> obtenerHistoriales() {
        return historialDbRestClient.obtenerHistoriales();
    }

    public void eliminarHistorial(Long id) {
        historialDbRestClient.eliminarHistorial(id);
    }

    public HistorialUpdateDTO actualizarHistorial(HistorialUpdateDTO historial) {
        return historialDbRestClient.actualizarHistorial(historial);
    }
}
