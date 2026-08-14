package br.fau.maquinalaser.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_titular_id")
    private Aluno titular;

    
    @ManyToOne
    @JoinColumn(name = "aluno_suplente_id")
    private Aluno suplente;

    
    private LocalDateTime inicio;
    private LocalDateTime fim;

    
    private String equipamento;

    
    @Enumerated(EnumType.STRING)
    private Status status;

    
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public enum Status {
        ATIVA,
        CANCELADA,
        FINALIZADA
    }

    @PrePersist
    public void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        if (this.status == null) {
            this.status = Status.ATIVA;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    

    public Long getId() {
        return id;
    }

    public Aluno getTitular() {
        return titular;
    }

    public void setTitular(Aluno titular) {
        this.titular = titular;
    }

    public Aluno getSuplente() {
        return suplente;
    }

    public void setSuplente(Aluno suplente) {
        this.suplente = suplente;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
