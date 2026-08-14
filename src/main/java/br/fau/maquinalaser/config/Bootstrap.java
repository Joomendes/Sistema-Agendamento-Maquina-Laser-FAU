package br.fau.maquinalaser.config;

import br.fau.maquinalaser.model.Aluno;
import br.fau.maquinalaser.repository.AlunoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Bootstrap {

    @Bean
    CommandLineRunner seedAluno(AlunoRepository repo, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repo.count() == 0) {
                Aluno joao = new Aluno();
                joao.setNome("Joao Silva");
                joao.setEmail("joao10400123@fau.br");
                joao.setSenhaHash(passwordEncoder.encode("abc123"));
                joao.setSemestre(9);
                joao.setTipoTrabalho(Aluno.TipoTrabalho.TFG);
                repo.save(joao);

                Aluno maria = new Aluno();
                maria.setNome("Maria Souza");
                maria.setEmail("maria10400456@fau.br");
                maria.setSenhaHash(passwordEncoder.encode("abc123"));
                maria.setSemestre(7);
                maria.setTipoTrabalho(Aluno.TipoTrabalho.REGULAR);
                repo.save(maria);

                Aluno pedro = new Aluno();
                pedro.setNome("Pedro Lima");
                pedro.setEmail("pedro10400789@fau.br");
                pedro.setSenhaHash(passwordEncoder.encode("abc123"));
                pedro.setSemestre(5);
                pedro.setTipoTrabalho(Aluno.TipoTrabalho.REGULAR);
                repo.save(pedro);

                Aluno ana = new Aluno();
                ana.setNome("Ana Costa");
                ana.setEmail("ana10400912@fau.br");
                ana.setSenhaHash(passwordEncoder.encode("abc123"));
                ana.setSemestre(8);
                ana.setTipoTrabalho(Aluno.TipoTrabalho.TFG);
                repo.save(ana);
            }

            // Atualiza os dados de demonstracao antigos que ainda estavam em texto puro.
            repo.findAll().forEach(aluno -> {
                if (!aluno.getSenhaHash().startsWith("$2")) {
                    aluno.setSenhaHash(passwordEncoder.encode(aluno.getSenhaHash()));
                    repo.save(aluno);
                }
            });
        };
    }
}
