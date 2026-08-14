package br.fau.maquinalaser.controller;

import br.fau.maquinalaser.dto.AgendamentoRequest;
import br.fau.maquinalaser.dto.SlotDisponibilidadeResponse;
import br.fau.maquinalaser.model.Aluno;
import br.fau.maquinalaser.model.Reserva;
import br.fau.maquinalaser.repository.AlunoRepository;
import br.fau.maquinalaser.service.AgendamentoService;
import br.fau.maquinalaser.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgendamentoController.class);

    private final AgendamentoService agendamentoService;
    private final AlunoRepository alunoRepository;
    private final AuthService authService;

    public AgendamentoController(AgendamentoService agendamentoService,
                                 AlunoRepository alunoRepository,
                                 AuthService authService) {
        this.agendamentoService = agendamentoService;
        this.alunoRepository = alunoRepository;
        this.authService = authService;
    }

    
    @PostMapping
    public ResponseEntity<?> criarAgendamento(@RequestBody AgendamentoRequest solicitacao) {
        try {
            Aluno aluno = authService.getAlunoLogado();

            Reserva reserva = agendamentoService.agendarHorario(
                    aluno, solicitacao.getInicio(), solicitacao.getFim(), solicitacao.getEquipamento()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "reserva", reserva,
                    "mensagem", "Agendamento criado com sucesso."
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return erroInterno("criar o agendamento", e);
        }
    }


   
    @GetMapping("/meus")
    public ResponseEntity<?> listarReservasDoAluno() {
        try {
            Aluno aluno = authService.getAlunoLogado();
            List<Reserva> reservas = agendamentoService.listarReservasDoAluno(aluno);
            return ResponseEntity.ok(reservas);

        } catch (Exception e) {
            return erroInterno("listar as reservas", e);
        }
    }

    @GetMapping("/disponibilidade")
    public ResponseEntity<List<SlotDisponibilidadeResponse>> listarDisponibilidade(
            @RequestParam LocalDate data,
            @RequestParam String equipamento) {
        Aluno aluno = authService.getAlunoLogado();
        return ResponseEntity.ok(agendamentoService.listarDisponibilidade(data, equipamento, aluno));
    }


   
    @PostMapping("/{reservaId}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long reservaId) {
        try {
            Aluno aluno = authService.getAlunoLogado();
            agendamentoService.cancelarReserva(aluno, reservaId);

            return ResponseEntity.ok("Reserva cancelada com sucesso.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return erroInterno("cancelar a reserva", e);
        }
    }


   
    @PostMapping("/{reservaId}/suplente")
    public ResponseEntity<?> incluirSuplente(@PathVariable Long reservaId,
                                             @RequestParam String suplenteEmail) {
        try {
            Aluno titular = authService.getAlunoLogado();

            Aluno suplente = alunoRepository.findByEmail(suplenteEmail)
                    .orElseThrow(() -> new IllegalArgumentException("Suplente não encontrado."));

            agendamentoService.incluirSuplente(titular, reservaId, suplente);

            return ResponseEntity.ok("Suplente " + suplente.getNome()
                    + " incluído na reserva " + reservaId + " com sucesso.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return erroInterno("incluir o suplente", e);
        }
    }

    // O detalhe fica no log do servidor; a resposta ao navegador nao revela informacoes internas.
    private ResponseEntity<String> erroInterno(String operacao, Exception erro) {
        LOGGER.error("Nao foi possivel {}.", operacao, erro);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Nao foi possivel " + operacao + ". Tente novamente.");
    }
}
