package br.fau.maquinalaser.dto;

import java.time.LocalDateTime;

public class AgendamentoRequest {

    private Long alunoId;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private String equipamento;

    public Long getAlunoId() {
        return alunoId;
    }
    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }
    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }
    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }

    public String getEquipamento() {
        return equipamento;
    }
    public void setEquipamento(String equipamento) {
        this.equipamento = equipamento;
    }
}
