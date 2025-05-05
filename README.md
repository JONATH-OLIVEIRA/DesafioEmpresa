# Cadastro de Usuários - Desafio Técnico

Este projeto foi desenvolvido como parte de um desafio técnico com o objetivo de implementar um sistema completo de cadastro de usuários.
A solução utiliza **Spring Boot**, **Thymeleaf**, **PostgreSQL**, entre outras tecnologias modernas e alinhadas com o mercado, conforme conversa com a recrutadora,
foi alterado a tecnologia solicitado de JSF 2.0 para Thymeleaf devidos a erros de intregração.

---

## 🧩 Tecnologias Utilizadas

- Java 8+
- Spring Boot (Web, Security, Data JPA)
- Thymeleaf (substituindo JSF/Primefaces)
- Hibernate
- PostgreSQL
- Bootstrap 5 (para visual moderno)
- API de CEP (ViaCEP ou Correios)
- Maven
- Git

---

## 🔐 Funcionalidades

### 👤 Usuário
- Cadastro de novo usuário com validações:
  - Nome (mínimo 30 caracteres)
  - Username único
  - Senha segura (mínimo 8 caracteres, com número e letra maiúscula)
  - Email válido
  - Data de nascimento (mínimo 18 anos)
  - CPF/CNPJ com máscara e validação dinâmica baseada no tipo de pessoa
  - Sexo (Masculino, Feminino, Outro)
  - Tipo de pessoa (Física/Jurídica)
  - Upload de foto de perfil
  - Endereço preenchido automaticamente via CEP (API)

### 📋 Listagem e Filtro
- Página com listagem dos usuários
- Filtro por nome em tempo real

### ✏️ Edição e Exclusão
- Atualização e remoção de usuários existentes

### 🔒 Autenticação e Autorização
- Login com validação de credenciais
- Controle de acesso a funcionalidades internas

---

## 🗄️ Banco de Dados

- Banco utilizado: **PostgreSQL**
- Estrutura criada via JPA + scripts SQL (Stored Procedures)
- Operações complexas são feitas via procedures, simples via ORM

---

## 📦 Instalação e Execução

### Pré-requisitos

- Java 8 ou superior
- Maven 3.8+
- PostgreSQL 12+
- IDE (recomendado: IntelliJ, STS ou VS Code)

### Passos

1. Clone o repositório:
   ```bash
   git clone https://github.com/seuusuario/nome-do-projeto.git
   cd nome-do-projeto
Configure o application.properties:

properties

spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
Execute o projeto:

mvn spring-boot:run
Acesse via navegador:
http://localhost:8080


📁 Estrutura do Projeto
src/
 └── main/
     ├── java/
     │   └── com.seuprojeto/
     │       ├── controller/
     │       ├── model/
     │       ├── repository/
     │       ├── service/
     │       └── security/
     ├── resources/
     │   ├── templates/     # Thymeleaf views
     │   ├── static/        # CSS, JS, Imagens
     │   └── application.properties
⚙️ Padrões e Arquitetura
MVC (Model-View-Controller)

Spring Security com autenticação baseada em sessão

Camada de serviços separada da camada de controle

Repositórios JPA com uso de @Query e Stored Procedures para consultas específicas

Validações com Bean Validation (JSR-380)

Templates organizados com Thymeleaf + Bootstrap

Boas práticas com princípios SOLID

teste de api com o link abaixo do swagger. 
http://localhost:8080/swagger-ui/index.html#/

📬 API de CEP
O preenchimento de endereço é feito via requisição à API pública do ViaCEP, utilizando JavaScript e integração com Thymeleaf.

🔑 Usuário Padrão
Caso necessário, crie um usuário administrador direto no banco para acessar o sistema inicialmente:


INSERT INTO usuario (nome, username, password, email, data_nascimento, tipo, cpf_cnpj, sexo)
VALUES ('Administrador', 'admin', '{bcrypt}senhaCriptografada', 'admin@email.com', '1990-01-01', 'FISICA', '123.456.789-00', 'M');
✅ TODO
 Validações completas no back-end

 Upload de imagem

 Máscaras dinâmicas para CPF/CNPJ

 Busca de endereço via CEP

 Filtro por nome na listagem

 Autenticação e autorização

 Melhorias em testes automatizados (em andamento)

👨‍💻 Autor
Desenvolvido por Jonah Oliveira.
Email: [jonatholiveira78@gmail.com]
LinkedIn:https://www.linkedin.com/in/jonatholiveira/
