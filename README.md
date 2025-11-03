# API de Atendimento Hospitalar

API REST desenvolvida em Java 21 + Spring Boot 3 para simular o fluxo de atendimento de um hospital, contemplando 
recepção, triagem, avaliação médica e medicação.

## 1. Decisões Arquiteturais e Tecnologias

O projeto foi construído seguindo uma **Arquitetura em Camadas (N-Tier)** clássica, separando responsabilidades:

* **Controllers:** Exposição dos endpoints REST, validação de DTOs e controle de acesso (Roles).
* **Services:** Orquestração e lógica de negócio (regras, validações de status).
* **Repositories:** Acesso aos dados (Spring Data JPA).
* **Domain (Entities):** Mapeamento Objeto-Relacional (JPA) do banco.
* **DTOs:** Objetos de Transferência de Dados para desacoplar a API das entidades internas.

### Tecnologias Utilizadas
* **Java 21**
* **Spring Boot 3** (Web, Data JPA, Validation)
* **Spring Security 6** (Autenticação e Autorização)
* **JWT (JSON Web Tokens):** Para autenticação stateless.
* **PostgreSQL:** Banco de dados relacional obrigatório.
* **Springdoc (Swagger/OpenAPI):** Documentação interativa da API.
* **Lombok:** Redução de boilerplate.
* **Hibernate:** Implementação JPA (com `ddl-auto: update` para desenvolvimento).

---

## 2. Como Executar o Projeto

### Pré-requisitos
* JDK 21 (ou superior)
* Maven 3.8 (ou superior)
* PostgreSQL (local ou em container)

### 2.1. Configuração do Banco (PostgreSQL)

1.  Crie um banco de dados no PostgreSQL:
    ```sql
    CREATE DATABASE hospital_db;
    ```
2.  Abra o arquivo `src/main/resources/application.properties`.
3.  Configure suas credenciais do PostgreSQL:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/hospital_db
    spring.datasource.username=seu_usuario_postgres
    spring.datasource.password=sua_senha_postgres
    ```
4.  Configure o segredo do JWT (obrigatório):
    ```properties
    # Gere uma string longa e segura
    jwt.secret=SUA_CHAVE_SECRETA_MUITO_LONGA_E_SEGURA_AQUI
    ```

### 2.2. Executando a API

1.  Clone o repositório.
2.  Navegue até a raiz do projeto.
3.  Execute via Maven:
    ```bash
    mvn spring-boot:run
    ```
4.  A API estará disponível em `http://localhost:8080`.

---

## 3. Acesso à Documentação (Swagger)

A documentação completa e interativa dos endpoints está disponível via Swagger UI:

**URL:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## 4. Fluxo de Autenticação e Usuários de Teste

A API utiliza JWT. Para acessar endpoints protegidos, você deve primeiro obter um token.

### 4.1. Perfis (Roles)
O sistema possui 4 perfis:
* `ATENDENTE` (Recepção)
* `ENFERMEIRO` (Triagem)
* `MEDICO` (Avaliação e Conduta)
* `FARMACIA` (Administração de medicação)

### 4.2. Criando Usuários
Use o endpoint `POST /auth/register` para criar os usuários de teste.

**Exemplo (Atendente):**
```bash
# Request (POST /auth/register)
{
    "username": "atendente01",
    "password": "123",
    "role": "ATENDENTE"
}
````
Crie um usuário para cada role (ENFERMEIRO, MEDICO, FARMACIA) para testar o fluxo.

### 4.3. Obtendo o Token (Login)
Use o endpoint `POST /auth/login`.

**Exemplo (Login Atendente):**
```bash
# Request (POST /auth/login)
{
    "username": "atendente01",
    "password": "123"
}

# Response (200 OK)
{
    "token": "eyJh...[token longo]...s9A"
}
```

### 4.4. Usando o Token no Swagger

1. Copie o token gerado no login.
2. Acesse o Swagger UI: http://localhost:8080/swagger-ui/index.html
3. Clique no botão "Authorize" (canto superior direito).
4. Na janela, cole o token no campo "Value" prefixado por Bearer (ex: Bearer eyJh...s9A).
5. Clique em "Authorize" e feche a janela.
6. Agora você pode testar todos os endpoints protegidos.

## 5. Fluxo Principal da API (Cenário de Uso)

1. **(ATENDENTE)** Faz login (`POST /auth/login`).
2. **(ATENDENTE)** Cadastra um paciente (`POST /pacientes`), se ele não existir.
3. **(ATENDENTE)** Inicia o atendimento (`POST /atendimentos/iniciar`).
   - Status da Ficha: `AGUARDANDO_TRIAGEM`.
4. **(ENFERMEIRO)** Faz login.
5. **(ENFERMEIRO)** Consulta a fila (`GET /filas/triagem`).
6. **(ENFERMEIRO)** Registra a classificação de risco (`POST /atendimentos/{fichaId}/triagem`).
   - Status da Ficha: `AGUARDANDO_MEDICO`.
7. **(MEDICO)** Faz login.
8. **(MEDICO)** Consulta a fila (ordenada por prioridade) (`GET /filas/medico`).
9. **(MEDICO)** Registra a avaliação (`POST /atendimentos/{fichaId}/avaliacao-medica`).
  - **Cenário A (Conduta: MEDICACAO):**
    - Status da Ficha: `EM_MEDICACAO`.
10. **(FARMACIA)** Faz login.
11. **(FARMACIA)** Consulta a fila (`GET /filas/medicacao`).
12. **(FARMACIA)** Administra o(s) medicamento(s) (`POST /atendimentos/medicacao/{prescricaoId}/administrar`).
    - Após o último medicamento, Status da Ficha: `AGUARDANDO_REAVALIACAO`.
13. **(MEDICO)** Paciente retorna à fila (`GET /filas/medico`).
14. **(MEDICO)** Reavalia e dá alta (`POST /atendimentos/{fichaId}/avaliacao-medica com conduta ALTA`).
  - Status da Ficha: `FINALIZADO`.
  - `ativa` = `false`. 
  - `dataHoraSaida` = (agora).

  - **Cenário B (Conduta: ALTA / ENCAMINHAMENTO):**
    - Status da Ficha: `FINALIZADO`.
    - `ativa` = `false`.
    - `dataHoraSaida` = (agora).

## Autor
**Swetony Ancelmo**
- GitHub [Swetony Ancelmo](https://github.com/swetonyancelmo)
- Linkedin [Swetony Ancelmo](https://www.linkedin.com/in/swetony-ancelmo)
