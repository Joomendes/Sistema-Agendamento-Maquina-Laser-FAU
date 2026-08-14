package br.fau.maquinalaser.service;

import br.fau.maquinalaser.model.Aluno;
import br.fau.maquinalaser.model.Reserva;
import br.fau.maquinalaser.dto.SlotDisponibilidadeResponse;
import br.fau.maquinalaser.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    private static final LocalTime INICIO_DIA = LocalTime.of(10, 0);
    private static final LocalTime FIM_DIA = LocalTime.of(17, 0);
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm");
    public static final List<String> CORTADORAS = List.of(
            "Cortadora a Laser 1",
            "Cortadora a Laser 2",
            "Cortadora a Laser 3",
            "Cortadora a Laser 4"
    );

    private final ReservaRepository reservaRepository;

    public AgendamentoService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    
    @Transactional
    public Reserva agendarHorario(Aluno aluno,
                                  LocalDateTime inicio,
                                  LocalDateTime fim,
                                  String equipamento) {

        if (aluno == null || aluno.getId() == null) {
            throw new IllegalArgumentException("Aluno inválido.");
        }
        if (equipamento == null || equipamento.isBlank()) {
            throw new IllegalArgumentException("É necessário escolher uma cortadora.");
        }
        if (!CORTADORAS.contains(equipamento)) {
            throw new IllegalArgumentException("Cortadora inválida.");
        }
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("É necessário escolher um horário.");
        }
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("Você deve escolher o horário de início antes.");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível agendar nesse horário.");
        }
        validarSlotFixo(inicio, fim);

        // Duas reservas ativas nao podem ocupar a mesma cortadora no mesmo intervalo.
        boolean conflito = reservaRepository
                .existsByEquipamentoAndStatusInAndInicioBeforeAndFimAfter(
                        equipamento,
                        List.of(Reserva.Status.ATIVA),
                        fim,
                        inicio
                );

        if (conflito) {
            throw new IllegalStateException("Horário já está reservado para esta cortadora.");
        }

        // As regras de semestre sao conferidas no servidor, nao apenas na interface.
        if (!podeAgendar(aluno, inicio)) {
            throw new IllegalStateException("Você não tem permissão para agendar esse horário.");
        }

        Reserva reserva = new Reserva();
        reserva.setTitular(aluno);
        reserva.setEquipamento(equipamento);
        reserva.setInicio(inicio);
        reserva.setFim(fim);
        reserva.setStatus(Reserva.Status.ATIVA);
        reserva.setCriadoEm(LocalDateTime.now());
        reserva.setAtualizadoEm(LocalDateTime.now());

        return reservaRepository.save(reserva);
    }

    
    public boolean podeAgendar(Aluno aluno, LocalDateTime inicioSlot) {
        if (aluno == null || aluno.getId() == null) {
            return false;
        }

        LocalTime hora = inicioSlot.toLocalTime();
        if (hora.isBefore(INICIO_DIA) || hora.isAfter(FIM_DIA)) {
            return false;
        }

        Integer semestre = aluno.getSemestre();
        if (semestre == null || semestre < 4 || semestre > 10) {
            return false;
        }

        DayOfWeek dia = inicioSlot.getDayOfWeek();
        if (semestre <= 8 && dia != DayOfWeek.THURSDAY && dia != DayOfWeek.FRIDAY) {
            return false;
        }
        if (semestre >= 9 && (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY)) {
            return false;
        }

        return inicioSlot.isAfter(LocalDateTime.now());
    }

    public List<SlotDisponibilidadeResponse> listarDisponibilidade(LocalDate data,
                                                                   String equipamento,
                                                                   Aluno aluno) {
        if (aluno == null || aluno.getId() == null) {
            throw new IllegalArgumentException("Aluno inválido.");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data é obrigatória.");
        }
        if (equipamento == null || equipamento.isBlank()) {
            throw new IllegalArgumentException("É necessário escolher uma máquina.");
        }

        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.plusDays(1).atStartOfDay();

        Set<LocalTime> ocupados = reservaRepository
                .findAllByEquipamentoAndStatusInAndInicioGreaterThanEqualAndInicioLessThanOrderByInicioAsc(
                        equipamento,
                        List.of(Reserva.Status.ATIVA),
                        inicioDia,
                        fimDia
                )
                .stream()
                .map(r -> r.getInicio().toLocalTime())
                .collect(Collectors.toSet());
        LocalDateTime agora = LocalDateTime.now();

        return IntStream.rangeClosed(INICIO_DIA.getHour(), FIM_DIA.getHour())
                .mapToObj(hora -> {
                    LocalTime slot = LocalTime.of(hora, 0);
                    LocalDateTime inicioSlot = LocalDateTime.of(data, slot);
                    boolean passado = inicioSlot.isBefore(agora);
                    return new SlotDisponibilidadeResponse(
                            HORA_FMT.format(slot),
                            passado || ocupados.contains(slot) || !podeAgendar(aluno, inicioSlot)
                    );
                })
                .toList();
    }

    @Transactional
    public void cancelarReserva(Aluno aluno, Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada."));

        if (!Objects.equals(reserva.getTitular().getId(), aluno.getId())) {
            throw new IllegalStateException("Você não pode cancelar essa reserva.");
        }
        if (reserva.getStatus() != Reserva.Status.ATIVA) {
            throw new IllegalStateException("Apenas reservas ativas podem ser canceladas.");
        }

        Duration antecedencia = Duration.between(LocalDateTime.now(), reserva.getInicio());
        if (antecedencia.toMinutes() < 60) {
            throw new IllegalStateException("Cancelamento não permitido: falta menos de 1h para o início.");
        }

        reserva.setStatus(Reserva.Status.CANCELADA);
        reserva.setAtualizadoEm(LocalDateTime.now());
        reservaRepository.save(reserva);
    }

    @Transactional
    public void incluirSuplente(Aluno titular, Long reservaId, Aluno suplente) {
        if (suplente == null || suplente.getId() == null) {
            throw new IllegalArgumentException("Suplente inválido.");
        }

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada."));

        if (!Objects.equals(reserva.getTitular().getId(), titular.getId())) {
            throw new IllegalStateException("Você não é o titular dessa reserva.");
        }
        if (Objects.equals(reserva.getTitular().getId(), suplente.getId())) {
            throw new IllegalStateException("Titular não pode ser suplente da própria reserva.");
        }

        if (LocalDateTime.now().isAfter(reserva.getFim())) {
            throw new IllegalStateException("Não é possível adicionar suplente após o término.");
        }

        reserva.setSuplente(suplente);
        reserva.setAtualizadoEm(LocalDateTime.now());
        reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva editarHorario(Aluno aluno,
                                 Long reservaId,
                                 LocalDateTime novoInicio,
                                 LocalDateTime novoFim,
                                 String equipamento) {

        if (aluno == null || aluno.getId() == null)
            throw new IllegalArgumentException("Aluno inválido.");
        if (equipamento == null || equipamento.isBlank())
            throw new IllegalArgumentException("Equipamento é obrigatório.");
        if (!CORTADORAS.contains(equipamento))
            throw new IllegalArgumentException("Cortadora inválida.");
        if (novoInicio == null || novoFim == null)
            throw new IllegalArgumentException("Início e fim são obrigatórios.");
        if (!novoFim.isAfter(novoInicio))
            throw new IllegalArgumentException("Fim deve ser depois do início.");
        if (novoInicio.isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Não é possível mover para o passado.");

        Reserva r = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada."));

        if (!Objects.equals(r.getTitular().getId(), aluno.getId()))
            throw new IllegalStateException("Você não é o titular.");

        if (r.getStatus() != Reserva.Status.ATIVA)
            throw new IllegalStateException("Apenas reservas ativas podem ser editadas.");
        validarSlotFixo(novoInicio, novoFim);

        
        boolean conflito = reservaRepository
                .existsByEquipamentoAndStatusInAndInicioBeforeAndFimAfterAndIdNot(
                        equipamento,
                        List.of(Reserva.Status.ATIVA),
                        novoFim,
                        novoInicio,
                        r.getId()
                );

        if (conflito) {
            throw new IllegalStateException("Conflito de horário para o equipamento selecionado.");
        }

        if (!podeAgendar(aluno, novoInicio)) {
            throw new IllegalStateException("Sem permissão neste novo horário.");
        }

        r.setEquipamento(equipamento);
        r.setInicio(novoInicio);
        r.setFim(novoFim);
        r.setAtualizadoEm(LocalDateTime.now());
        return reservaRepository.save(r);
    }

    public List<Reserva> listarReservasDoAluno(Aluno aluno) {
        return reservaRepository.findAllByTitularIdOrderByInicioDesc(aluno.getId());
    }

    private void validarSlotFixo(LocalDateTime inicio, LocalDateTime fim) {
        if (!fim.equals(inicio.plusHours(1))) {
            throw new IllegalArgumentException("Cada reserva deve durar exatamente 1 hora.");
        }

        if (inicio.getMinute() != 0 || inicio.getSecond() != 0 || inicio.getNano() != 0) {
            throw new IllegalArgumentException("Os horários devem começar na hora cheia.");
        }

        LocalTime hora = inicio.toLocalTime();
        if (hora.isBefore(INICIO_DIA) || hora.isAfter(FIM_DIA)) {
            throw new IllegalArgumentException("Horários disponíveis vão de 10h às 17h.");
        }
    }
}
