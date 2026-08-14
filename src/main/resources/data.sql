-- Recria/garante alunos sempre que o app subir
DELETE FROM RESERVA;
DELETE FROM ALUNO;

INSERT INTO ALUNO (id, nome, email, senha_hash, semestre, tipo_trabalho)
VALUES (10400123, 'Joao Silva', 'joao10400123@fau.br', '$2a$10$CvLZ6CQ6qudN9qO2l2p6ZuaY4PHXqWCxWUnqJlE/f9SV4IPUjqGLW', 9, 'TFG');

INSERT INTO ALUNO (id, nome, email, senha_hash, semestre, tipo_trabalho)
VALUES (10400456, 'Maria Souza', 'maria10400456@fau.br', '$2a$10$CvLZ6CQ6qudN9qO2l2p6ZuaY4PHXqWCxWUnqJlE/f9SV4IPUjqGLW', 7, 'REGULAR');

INSERT INTO ALUNO (id, nome, email, senha_hash, semestre, tipo_trabalho)
VALUES (10400789, 'Pedro Lima', 'pedro10400789@fau.br', '$2a$10$CvLZ6CQ6qudN9qO2l2p6ZuaY4PHXqWCxWUnqJlE/f9SV4IPUjqGLW', 5, 'REGULAR');

INSERT INTO ALUNO (id, nome, email, senha_hash, semestre, tipo_trabalho)
VALUES (10400912, 'Ana Costa', 'ana10400912@fau.br', '$2a$10$CvLZ6CQ6qudN9qO2l2p6ZuaY4PHXqWCxWUnqJlE/f9SV4IPUjqGLW', 8, 'TFG');
