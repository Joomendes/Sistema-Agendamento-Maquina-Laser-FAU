package br.fau.maquinalaser.repository;

import br.fau.maquinalaser.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    
    boolean existsByEquipamentoAndStatusInAndInicioBeforeAndFimAfter(
            String equipamento,
            Collection<Reserva.Status> status,
            LocalDateTime fimNovo,
            LocalDateTime inicioNovo
    );

    
    boolean existsByEquipamentoAndStatusInAndInicioBeforeAndFimAfterAndIdNot(
            String equipamento,
            Collection<Reserva.Status> status,
            LocalDateTime fimNovo,
            LocalDateTime inicioNovo,
            Long idToIgnore
    );

    
    List<Reserva> findAllByTitularIdOrderByInicioDesc(Long titularId);

    List<Reserva> findAllByEquipamentoAndStatusInAndInicioGreaterThanEqualAndInicioLessThanOrderByInicioAsc(
            String equipamento,
            Collection<Reserva.Status> status,
            LocalDateTime inicio,
            LocalDateTime fim);
}
