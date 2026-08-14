package br.fau.maquinalaser.dto;

public record SlotDisponibilidadeResponse(
        String hora,
        boolean ocupado
) {
}
