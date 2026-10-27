# Sistema de Agendamento Máquina Laser - FAU

Aplicação web desenvolvida para a disciplina de Práticas Profissionais em Análise e Desenvolvimento de Sistemas. A aplicação web simula o ambiente de login no portal do aluno TOTVS da Universidade Presbiteriana Mackenzie e inclui a função de agendamentos de máquinas de corte a laser para os alunos da FAU (Faculdade de Arquitetura e Urbanismo).

## Vídeo de demonstração:


## Principais funcionalidades:
- Autenticação de usuários (papéis: aluno, administrador).
- Calendário de agendamentos com verificação de conflitos.
- Interface web responsiva com templates Thymeleaf.
- Testes unitários para a camada de serviço (JUnit).

## Tecnologias
- Java 17
- Spring Boot
- Thymeleaf (templates server-side)
- H2 (banco em memória para desenvolvimento)
- Maven (pom.xml)
- Dockerfile
- BCrypt (Senhas são armazenada com hash)
- JUnit (testes)

## Como fazer login
A aplicação usa autenticação com Spring Security e aceita login com email do aluno ou pela matrícula (RA).

URL de acesso:
- http://localhost:8080/login

Credenciais de demonstração
Ao iniciar a aplicação, o sistema cria automaticamente usuários de teste em banco em memória para simular o fluxo real do sistema.

- João Silva | Email: joao10400123@fau.br ou 10400123 | Senha: abc123
- Maria Souza | Email: maria10400456@fau.br ou 10400456 | Senha: abc123
- Pedro Lima | Email: pedro10400789@fau.br ou 10400789 | Senha: abc123
- Ana Costa | Email: ana10400912@fau.br ou 10400912 | Senha: abc123

Observações:
- As senhas são armazenadas com hash BCrypt.
- O login pode ser feito pelo email ou pela matrícula.
- Usuários de exemplo são criados automaticamente ao subir a aplicação.

## Banco de dados
O projeto utiliza H2 em memória para desenvolvimento. Os dados iniciais são carregados automaticamente pelos arquivos:

- src/main/resources/data.sql
- src/main/java/br/fau/maquinalaser/config/Bootstrap.java

Esses arquivos preenchem a tabela aluno com registros fictícios para validar o fluxo de autenticação e agendamento.
