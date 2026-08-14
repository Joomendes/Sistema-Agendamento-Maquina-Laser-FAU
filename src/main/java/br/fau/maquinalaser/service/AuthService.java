package br.fau.maquinalaser.service;

import br.fau.maquinalaser.config.AlunoUserDetails;
import br.fau.maquinalaser.model.Aluno;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public Aluno getAlunoLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AlunoUserDetails detalhes)) {
            throw new IllegalStateException("Usuário não autenticado.");
        }

        return detalhes.getAluno();
    }
}
