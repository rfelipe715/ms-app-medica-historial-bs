package cl.duoc.ms_historial_bs.service;

import cl.duoc.ms_historial_bs.client.CitasBsRestClient;
import cl.duoc.ms_historial_bs.client.HistorialDbRestClient;
import cl.duoc.ms_historial_bs.client.PacientesBsRestClient;
import cl.duoc.ms_historial_bs.exception.HistorialNotFoundException;
import cl.duoc.ms_historial_bs.exception.ServicioNoDisponibleException;
import cl.duoc.ms_historial_bs.model.dto.CitaDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialConDetallesDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bs.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bs.model.dto.PacienteBffDto;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistorialServiceTest {

    @Mock
    private HistorialDbRestClient historialDbRestClient;

    @Mock
    private PacientesBsRestClient pacientesBsRestClient;

    @Mock
    private CitasBsRestClient citasBsRestClient;

    @InjectMocks
    private HistorialService historialService;

    private HistorialDTO historialDTO;

    @BeforeEach
    void setUp() {
        historialDTO = new HistorialDTO(1L, 10L, 20L, "2026-08-01", "Resfrío común", "Reposo 3 días");
    }

    @Test
    void registrarHistorial_guardaElHistorialEnLaCapaDb() {
        when(historialDbRestClient.guardarHistorial(historialDTO)).thenReturn(historialDTO);

        HistorialDTO resultado = historialService.registrarHistorial(historialDTO);

        assertThat(resultado).isEqualTo(historialDTO);
    }

    @Test
    void registrarHistorial_lanzaServicioNoDisponible_cuandoFallaLaCapaDb() {
        when(historialDbRestClient.guardarHistorial(historialDTO)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> historialService.registrarHistorial(historialDTO))
                .isInstanceOf(ServicioNoDisponibleException.class);
    }

    @Test
    void obtenerHistoriales_retornaLosHistorialesDeLaCapaDb() {
        when(historialDbRestClient.obtenerHistoriales()).thenReturn(List.of(historialDTO));

        List<HistorialDTO> resultado = historialService.obtenerHistoriales();

        assertThat(resultado).containsExactly(historialDTO);
    }

    @Test
    void obtenerHistorialPorId_retornaElHistorial_cuandoExiste() {
        when(historialDbRestClient.obtenerHistorialPorId(1L)).thenReturn(historialDTO);

        HistorialDTO resultado = historialService.obtenerHistorialPorId(1L);

        assertThat(resultado).isEqualTo(historialDTO);
    }

    @Test
    void obtenerHistorialPorId_lanzaHistorialNotFoundException_cuandoDbRetorna404() {
        when(historialDbRestClient.obtenerHistorialPorId(99L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> historialService.obtenerHistorialPorId(99L))
                .isInstanceOf(HistorialNotFoundException.class);
    }

    @Test
    void eliminarHistorial_delegaEnLaCapaDb() {
        historialService.eliminarHistorial(1L);

        verify(historialDbRestClient).eliminarHistorial(1L);
    }

    @Test
    void eliminarHistorial_lanzaHistorialNotFoundException_cuandoDbRetorna404() {
        org.mockito.Mockito.doThrow(mock(FeignException.NotFound.class)).when(historialDbRestClient).eliminarHistorial(99L);

        assertThatThrownBy(() -> historialService.eliminarHistorial(99L))
                .isInstanceOf(HistorialNotFoundException.class);
    }

    @Test
    void actualizarHistorial_retornaElHistorialActualizado() {
        HistorialUpdateDTO update = new HistorialUpdateDTO(1L, 10L, 20L, "2026-08-02", "Gripe", "Reposo 5 días");
        when(historialDbRestClient.actualizarHistorial(1L, update)).thenReturn(historialDTO);

        HistorialDTO resultado = historialService.actualizarHistorial(1L, update);

        assertThat(resultado).isEqualTo(historialDTO);
    }

    @Test
    void obtenerHistorialesConDetalles_faneaAPacientesYCitasParaEnriquecer() {
        PacienteBffDto paciente = new PacienteBffDto(10L, "11111111-1", "R1", "F1", "Juan", "Perez", "M", "1990-01-01", "Calle 1", "123456");
        CitaDTO cita = new CitaDTO(20L, 10L, "2026-08-01", "10:00", "CONFIRMADA");
        when(historialDbRestClient.obtenerHistoriales()).thenReturn(List.of(historialDTO));
        when(pacientesBsRestClient.obtenerPaciente(10L)).thenReturn(paciente);
        when(citasBsRestClient.obtenerCita(20L)).thenReturn(cita);

        List<HistorialConDetallesDTO> resultado = historialService.obtenerHistorialesConDetalles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPaciente()).isEqualTo(paciente);
        assertThat(resultado.get(0).getCita()).isEqualTo(cita);
    }

    @Test
    void obtenerHistorialesConDetalles_dejaPacienteYCitaEnNull_siFallanLosServiciosRemotos() {
        when(historialDbRestClient.obtenerHistoriales()).thenReturn(List.of(historialDTO));
        when(pacientesBsRestClient.obtenerPaciente(10L)).thenThrow(new RuntimeException("paciente no encontrado"));
        when(citasBsRestClient.obtenerCita(20L)).thenThrow(new RuntimeException("cita no encontrada"));

        List<HistorialConDetallesDTO> resultado = historialService.obtenerHistorialesConDetalles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPaciente()).isNull();
        assertThat(resultado.get(0).getCita()).isNull();
    }

    @Test
    void obtenerHistorialConDetalles_retornaNull_siElHistorialNoExiste() {
        when(historialDbRestClient.obtenerHistoriales()).thenReturn(List.of(historialDTO));

        HistorialConDetallesDTO resultado = historialService.obtenerHistorialConDetalles(999L);

        assertThat(resultado).isNull();
    }

    @Test
    void obtenerHistorialConDetalles_retornaElHistorialEnriquecido_siExiste() {
        PacienteBffDto paciente = new PacienteBffDto(10L, "11111111-1", "R1", "F1", "Juan", "Perez", "M", "1990-01-01", "Calle 1", "123456");
        CitaDTO cita = new CitaDTO(20L, 10L, "2026-08-01", "10:00", "CONFIRMADA");
        when(historialDbRestClient.obtenerHistoriales()).thenReturn(List.of(historialDTO));
        when(pacientesBsRestClient.obtenerPaciente(10L)).thenReturn(paciente);
        when(citasBsRestClient.obtenerCita(20L)).thenReturn(cita);

        HistorialConDetallesDTO resultado = historialService.obtenerHistorialConDetalles(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPaciente()).isEqualTo(paciente);
        assertThat(resultado.getCita()).isEqualTo(cita);
    }
}
