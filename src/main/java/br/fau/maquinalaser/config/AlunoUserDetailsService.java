package br.fau.maquinalaser.config;

import br.fau.maquinalaser.model.Aluno;
import br.fau.maquinalaser.repository.AlunoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AlunoUserDetailsService implements UserDetailsService {

    private final AlunoRepository alunoRepository;

    public AlunoUserDetailsService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Aluno aluno = alunoRepository.findByEmail(username).orElse(null);

        if (aluno == null && username != null && username.matches("\\d{8}")) {
            aluno = alunoRepository.findById(Long.parseLong(username)).orElse(null);
        }

        if (aluno == null) {
            throw new UsernameNotFoundException("Aluno não encontrado: " + username);
        }

        return new AlunoUserDetails(aluno);
    }
}
