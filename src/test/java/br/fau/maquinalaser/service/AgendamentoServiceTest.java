package br.fau.maquinalaser.service;

import br.fau.maquinalaser.model.Aluno;
import br.fau.maquinalaser.model.Reserva;
import br.fau.maquinalaser.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    private AgendamentoService service;

    @BeforeEach
    void setUp() {
        service = new AgendamentoService(reservaRepository);
    }

    @Test
    void alunoDoSetimoSemestreNaoPodeAgendarNaSegunda() {
        Aluno aluno = aluno(7, 10400456L);

        assertFalse(service.podeAgendar(aluno, proximo(DayOfWeek.MONDAY, 10)));
    }

    @Test
    void alunoDoSetimoSemestrePodeAgendarNaQuinta() {
        Aluno aluno = aluno(7, 10400456L);

        assertTrue(service.podeAgendar(aluno, proximo(DayOfWeek.THURSDAY, 10)));
    }

    @Test
    void alunoDoNonoSemestrePodeAgendarEmDiaUtil() {
        Aluno aluno = aluno(9, 10400123L);

        assertTrue(service.podeAgendar(aluno, proximo(DayOfWeek.MONDAY, 10)));
    }

    @Test
    void recusaReservaQuandoHorarioDaCortadoraEstaOcupado() {
        Aluno aluno = aluno(9, 10400123L);
        LocalDateTime inicio = proximo(DayOfWeek.MONDAY, 10);
        when(reservaRepository.existsByEquipamentoAndStatusInAndInicioBeforeAndFimAfter(
                anyString(), anyCollection(), any(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.agendarHorario(
                aluno, inicio, inicio.plusHours(1), "Cortadora a Laser 1"));
    }

    @Test
    void recusaCancelamentoDeReservaDeOutroAluno() {
        Aluno titular = aluno(9, 10400123L);
        Aluno outroAluno = aluno(7, 10400456L);
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setTitular(titular);
        reserva.setStatus(Reserva.Status.ATIVA);
        reserva.setInicio(proximo(DayOfWeek.THURSDAY, 10));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(IllegalStateException.class, () -> service.cancelarReserva(outroAluno, 1L));
    }

    @Test
    void permiteReservaValida() {
        Aluno aluno = aluno(9, 10400123L);
        LocalDateTime inicio = proximo(DayOfWeek.MONDAY, 10);
        when(reservaRepository.existsByEquipamentoAndStatusInAndInicioBeforeAndFimAfter(
                anyString(), anyCollection(), any(), any())).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> service.agendarHorario(
                aluno, inicio, inicio.plusHours(1), "Cortadora a Laser 1"));
    }

    private Aluno aluno(int semestre, Long id) {
        Aluno aluno = new Aluno();
        aluno.setId(id);
        aluno.setSemestre(semestre);
        aluno.setNome("Aluno de teste");
        aluno.setEmail("teste" + id + "@fau.br");
        return aluno;
    }

    private LocalDateTime proximo(DayOfWeek dia, int hora) {
        LocalDate data = LocalDate.now().plusDays(1);
        while (data.getDayOfWeek() != dia) {
            data = data.plusDays(1);
        }
        return data.atTime(hora, 0);
    }
}
