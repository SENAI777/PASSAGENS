# 📚 Documentação da API REST - Aplicação de Reservas

Esta documentação destina-se a desenvolvedores e empresas terceiras que desejam integrar seus sistemas com a nossa plataforma de gestão de passagens aéreas e reservas.

---

## 🌐 Visão Geral

- **URL Base:** `http://localhost:8080/api` (ou a URL de produção fornecida no deploy)
- **Formato dos Dados:** `application/json`
- **Padrão Arquitetural:** RESTful
- **Cabeçalho Obrigatório para envio de dados:**
  ```http
  Content-Type: application/json
  Accept: application/json
  ```

---

## 🚦 Tabela Resumo de Códigos de Status HTTP

| Código | Significado | Quando é retornado |
| :--- | :--- | :--- |
| `200 OK` | Sucesso | Requisição processada com retorno de dados (GET, PUT). |
| `201 Created` | Criado | Recurso criado com sucesso. Retorna o cabeçalho `Location` com a URI do novo recurso e o objeto no corpo. |
| `204 No Content` | Sem Conteúdo | Operação executada com sucesso sem corpo de resposta (DELETE). |
| `400 Bad Request` | Requisição Inválida | Dados ausentes ou violação de regras de validação (ex: origem e destino iguais). |
| `404 Not Found` | Não Encontrado | O ID informado não existe na base de dados. |
| `409 Conflict` | Conflito | Violação de unicidade (CPF/e-mail já cadastrado) ou integridade relacional (tentativa de excluir registro com vínculos ativos). |

---

## 1. 👤 API de Passageiros (`/api/passageiros`)

Gerencia o cadastro e consulta de passageiros do sistema.

### 1.1. Listar todos os passageiros
- **Método:** `GET`
- **Endpoint:** `/api/passageiros`
- **Status de Sucesso:** `200 OK`
- **Exemplo de Resposta:**
  ```json
  [
    {
      "codPassageiro": 1,
      "nome": "Carlos Silva",
      "cpf": "123.456.789-00",
      "idade": 34,
      "email": "carlos.silva@exemplo.com"
    }
  ]
  ```

### 1.2. Buscar passageiro por ID
- **Método:** `GET`
- **Endpoint:** `/api/passageiros/{id}`
- **Status:** `200 OK` ou `404 Not Found`
- **Exemplo de Resposta (200):**
  ```json
  {
    "codPassageiro": 1,
    "nome": "Carlos Silva",
    "cpf": "123.456.789-00",
    "idade": 34,
    "email": "carlos.silva@exemplo.com"
  }
  ```

### 1.3. Cadastrar novo passageiro
- **Método:** `POST`
- **Endpoint:** `/api/passageiros`
- **Status:** `201 Created` ou `409 Conflict` (se CPF ou E-mail já existirem)
- **Cabeçalho de Retorno:** `Location: /api/passageiros/{id}`
- **Exemplo de Envio (Request Body):**
  ```json
  {
    "nome": "Mariana Santos",
    "cpf": "987.654.321-11",
    "idade": 29,
    "email": "mariana.santos@empresa.com"
  }
  ```
- **Exemplo de Resposta de Erro (409 Conflict):**
  ```json
  {
    "erro": "Já existe um passageiro cadastrado com este CPF."
  }
  ```

### 1.4. Atualizar passageiro existente
- **Método:** `PUT`
- **Endpoint:** `/api/passageiros/{id}`
- **Status:** `200 OK`, `404 Not Found` ou `409 Conflict`
- **Exemplo de Envio (Request Body):**
  ```json
  {
    "nome": "Mariana Santos Oliveira",
    "cpf": "987.654.321-11",
    "idade": 30,
    "email": "mariana.oliveira@empresa.com"
  }
  ```

### 1.5. Excluir passageiro
- **Método:** `DELETE`
- **Endpoint:** `/api/passageiros/{id}`
- **Status:** `204 No Content`, `404 Not Found` ou `409 Conflict`
- **Regra de Negócio:** Se o passageiro tiver reservas cadastradas, a exclusão é bloqueada.
- **Exemplo de Resposta de Erro (409 Conflict):**
  ```json
  {
    "erro": "Não é possível excluir: o passageiro possui reservas associadas."
  }
  ```

---

## 2. ✈️ API de Voos (`/api/voos`)

Gerencia o catálogo de voos da companhia aérea.

### 2.1. Listar todos os voos
- **Método:** `GET`
- **Endpoint:** `/api/voos`
- **Status de Sucesso:** `200 OK`
- **Exemplo de Resposta:**
  ```json
  [
    {
      "codVoo": 1,
      "origem": "São Paulo (GRU)",
      "destino": "Rio de Janeiro (GIG)",
      "horario": "2027-01-15T08:30:00",
      "modeloAviao": "Airbus A320",
      "descricao": "São Paulo (GRU) → Rio de Janeiro (GIG)"
    }
  ]
  ```

### 2.2. Buscar voo por ID
- **Método:** `GET`
- **Endpoint:** `/api/voos/{id}`
- **Status:** `200 OK` ou `404 Not Found`

### 2.3. Cadastrar novo voo
- **Método:** `POST`
- **Endpoint:** `/api/voos`
- **Status:** `201 Created` ou `400 Bad Request`
- **Regra de Negócio:** A origem e o destino não podem ser iguais.
- **Cabeçalho de Retorno:** `Location: /api/voos/{id}`
- **Exemplo de Envio (Request Body):**
  ```json
  {
    "origem": "Brasília (BSB)",
    "destino": "Salvador (SSA)",
    "horario": "2027-02-20T14:45:00",
    "modeloAviao": "Boeing 737-800"
  }
  ```
- **Exemplo de Resposta de Erro (400 Bad Request):**
  ```json
  {
    "erro": "A origem e o destino precisam ser diferentes."
  }
  ```

### 2.4. Atualizar voo existente
- **Método:** `PUT`
- **Endpoint:** `/api/voos/{id}`
- **Status:** `200 OK`, `400 Bad Request` ou `404 Not Found`
- **Exemplo de Envio (Request Body):**
  ```json
  {
    "origem": "Brasília (BSB)",
    "destino": "Salvador (SSA)",
    "horario": "2027-02-20T16:00:00",
    "modeloAviao": "Boeing 737 MAX 8"
  }
  ```

### 2.5. Excluir voo
- **Método:** `DELETE`
- **Endpoint:** `/api/voos/{id}`
- **Status:** `204 No Content`, `404 Not Found` ou `409 Conflict`
- **Regra de Negócio:** Se o voo já tiver passageiros com reservas confirmadas, a exclusão é bloqueada.
- **Exemplo de Resposta de Erro (409 Conflict):**
  ```json
  {
    "erro": "Não é possível excluir: o voo possui reservas associadas."
  }
  ```

---

## 3. 🎫 API de Reservas (`/api/reservas`)

Gerencia a emissão, alteração e cancelamento de reservas vinculadas a passageiros e voos.

### 3.1. Listar todas as reservas
- **Método:** `GET`
- **Endpoint:** `/api/reservas`
- **Status de Sucesso:** `200 OK`
- **Exemplo de Resposta:**
  ```json
  [
    {
      "codReserva": 1,
      "tarifa": 450.00,
      "qtdBagagens": 1,
      "localizador": "K7XQ2M",
      "dataReserva": "2026-11-23T20:10:00",
      "status": "Confirmada",
      "passageiro": {
        "codPassageiro": 1,
        "nome": "Carlos Silva",
        "cpf": "123.456.789-00",
        "idade": 34,
        "email": "carlos.silva@exemplo.com"
      },
      "voo": {
        "codVoo": 2,
        "origem": "São Paulo (GRU)",
        "destino": "Recife (REC)",
        "horario": "2026-12-01T08:30:00",
        "modeloAviao": "Airbus A320"
      }
    }
  ]
  ```

### 3.2. Buscar reserva por ID
- **Método:** `GET`
- **Endpoint:** `/api/reservas/{id}`
- **Status:** `200 OK` ou `404 Not Found`

### 3.3. Criar nova reserva
- **Método:** `POST`
- **Endpoint:** `/api/reservas`
- **Status:** `201 Created` ou `400 Bad Request`
- **Cabeçalho de Retorno:** `Location: /api/reservas/{id}`
- **Automações Executadas pelo Sistema:**
  1. Geração automática de **código localizador único** de 6 caracteres (ex: `A9FZ3K`) via gerador criptograficamente seguro.
  2. Registro da data e hora da reserva (`dataReserva`).
  3. Definição do status inicial como `Confirmada`.
  4. Disparo automático de e-mail de confirmação em formato HTML para o e-mail cadastrado do passageiro.
- **Exemplo de Envio (Request Body - `ReservaRequestDTO`):**
  ```json
  {
    "idPassageiro": 1,
    "idVoo": 2,
    "tarifa": 589.90,
    "qtdBagagens": 2
  }
  ```
  *(Nota: tanto `idPassageiro` / `codPassageiro` quanto `idVoo` / `codVoo` são aceitos)*

- **Exemplo de Resposta de Sucesso (201 Created):**
  ```json
  {
    "codReserva": 10,
    "tarifa": 589.90,
    "qtdBagagens": 2,
    "localizador": "X9Q4L2",
    "dataReserva": "2026-11-23T21:15:30.123",
    "status": "Confirmada",
    "passageiro": {
      "codPassageiro": 1,
      "nome": "Carlos Silva",
      "cpf": "123.456.789-00",
      "idade": 34,
      "email": "carlos.silva@exemplo.com"
    },
    "voo": {
      "codVoo": 2,
      "origem": "São Paulo (GRU)",
      "destino": "Recife (REC)",
      "horario": "2026-12-01T08:30:00",
      "modeloAviao": "Airbus A320"
    }
  }
  ```

### 3.4. Atualizar reserva existente
- **Método:** `PUT`
- **Endpoint:** `/api/reservas/{id}`
- **Status:** `200 OK`, `400 Bad Request` ou `404 Not Found`
- **Exemplo de Envio (Request Body):**
  ```json
  {
    "tarifa": 620.00,
    "qtdBagagens": 3
  }
  ```

### 3.5. Cancelar / Excluir reserva
- **Método:** `DELETE`
- **Endpoint:** `/api/reservas/{id}`
- **Status:** `204 No Content` ou `404 Not Found`

---

## 💻 Exemplos de Consumo com cURL

### Criar um Passageiro
```bash
curl -X POST http://localhost:8080/api/passageiros \
  -H "Content-Type: application/json" \
  -d '{"nome":"Lucas Moura","cpf":"111.222.333-99","idade":28,"email":"lucas@empresa.com"}'
```

### Criar um Voo
```bash
curl -X POST http://localhost:8080/api/voos \
  -H "Content-Type: application/json" \
  -d '{"origem":"Florianópolis","destino":"Belo Horizonte","horario":"2027-04-10T09:00:00","modeloAviao":"Embraer E195"}'
```

### Emitir uma Reserva
```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{"idPassageiro":1,"idVoo":1,"tarifa":420.50,"qtdBagagens":1}'
```
# 📚 Documentação da API REST - Aplicação de Reservas

Esta documentação destina-se a desenvolvedores e empresas terceiras que desejam integrar seus sistemas com a nossa plataforma de gestão de passagens aéreas e reservas.

---

## 🌐 Visão Geral

- **URL Base:** `http://localhost:8080/api`
- **Formato dos Dados:** `application/json`
- **Padrão Arquitetural:** RESTful
- **Cabeçalho Obrigatório para Envio de Dados:**
  ```http
  Content-Type: application/json
  Accept: application/json
  ```

---

## 🚦 Tabela de Códigos de Status HTTP

| Código | Significado | Quando é retornado |
| :--- | :--- | :--- |
| `200 OK` | Sucesso | Requisição processada com retorno de dados (GET, PUT). |
| `201 Created` | Criado | Recurso criado com sucesso. Retorna cabeçalho `Location` com a URL do recurso criado. |
| `204 No Content` | Sem Conteúdo | Operação de exclusão realizada com sucesso (DELETE). |
| `400 Bad Request` | Requisição Inválida | Violação de regra de negócio (ex: origem e destino iguais) ou campos obrigatórios ausentes. |
| `404 Not Found` | Não Encontrado | O ID informado não existe na base de dados. |
| `409 Conflict` | Conflito | Violação de integridade (CPF/e-mail já cadastrado ou exclusão de registro com vínculos). |

---

## 1. 👤 API de Passageiros (`/api/passageiros`)

Responsável pelo cadastro, atualização, listagem e remoção de passageiros.

### 1.1. Listar todos os passageiros
* **Método:** `GET`
* **Rota:** `/api/passageiros`
* **Status:** `200 OK`
* **Exemplo de Resposta:**
  ```json
  [
    {
      "codPassageiro": 1,
      "nome": "Carlos Silva",
      "cpf": "123.456.789-00",
      "idade": 34,
      "email": "carlos.silva@exemplo.com"
    }
  ]
  ```

### 1.2. Buscar passageiro por ID
* **Método:** `GET`
* **Rota:** `/api/passageiros/{id}`
* **Status:** `200 OK` (se encontrado) ou `404 Not Found`

### 1.3. Cadastrar novo passageiro
* **Método:** `POST`
* **Rota:** `/api/passageiros`
* **Status:** `201 Created` ou `409 Conflict` (se CPF ou e-mail já existirem)
* **Cabeçalho de Retorno:** `Location: /api/passageiros/{id}`
* **Corpo da Requisição (Body):**
  ```json
  {
    "nome": "Mariana Santos",
    "cpf": "987.654.321-11",
    "idade": 29,
    "email": "mariana.santos@empresa.com"
  }
  ```

### 1.4. Atualizar passageiro
* **Método:** `PUT`
* **Rota:** `/api/passageiros/{id}`
* **Status:** `200 OK`, `404 Not Found` ou `409 Conflict`
* **Corpo da Requisição (Body):**
  ```json
  {
    "nome": "Mariana Santos Oliveira",
    "cpf": "987.654.321-11",
    "idade": 30,
    "email": "mariana.oliveira@empresa.com"
  }
  ```

### 1.5. Excluir passageiro
* **Método:** `DELETE`
* **Rota:** `/api/passageiros/{id}`
* **Status:** `204 No Content`, `404 Not Found` ou `409 Conflict`
* **Regra de Negócio:** Se o passageiro possuir reservas vinculadas, a exclusão é rejeitada com `409 Conflict`.

---

## 2. ✈️ API de Voos (`/api/voos`)

Responsável pela consulta e gerenciamento da malha aérea de voos.

### 2.1. Listar todos os voos
* **Método:** `GET`
* **Rota:** `/api/voos`
* **Status:** `200 OK`
* **Exemplo de Resposta:**
  ```json
  [
    {
      "codVoo": 1,
      "origem": "São Paulo (GRU)",
      "destino": "Rio de Janeiro (GIG)",
      "horario": "2027-01-15T08:30:00",
      "modeloAviao": "Airbus A320",
      "descricao": "São Paulo (GRU) → Rio de Janeiro (GIG)"
    }
  ]
  ```

### 2.2. Buscar voo por ID
* **Método:** `GET`
* **Rota:** `/api/voos/{id}`
* **Status:** `200 OK` ou `404 Not Found`

### 2.3. Cadastrar novo voo
* **Método:** `POST`
* **Rota:** `/api/voos`
* **Status:** `201 Created` ou `400 Bad Request`
* **Regra de Negócio:** Origem e destino obrigatoriamente precisam ser diferentes.
* **Corpo da Requisição (Body):**
  ```json
  {
    "origem": "Brasília (BSB)",
    "destino": "Salvador (SSA)",
    "horario": "2027-02-20T14:45:00",
    "modeloAviao": "Boeing 737-800"
  }
  ```

### 2.4. Atualizar voo
* **Método:** `PUT`
* **Rota:** `/api/voos/{id}`
* **Status:** `200 OK`, `400 Bad Request` ou `404 Not Found`

### 2.5. Excluir voo
* **Método:** `DELETE`
* **Rota:** `/api/voos/{id}`
* **Status:** `204 No Content`, `404 Not Found` ou `409 Conflict`
* **Regra de Negócio:** Voos com reservas atreladas não podem ser excluídos.

---

## 3. 🎫 API de Reservas (`/api/reservas`)

Responsável pela emissão e gerenciamento de bilhetes e reservas.

### 3.1. Listar todas as reservas
* **Método:** `GET`
* **Rota:** `/api/reservas`
* **Status:** `200 OK`

### 3.2. Buscar reserva por ID
* **Método:** `GET`
* **Rota:** `/api/reservas/{id}`
* **Status:** `200 OK` ou `404 Not Found`

### 3.3. Criar nova reserva
* **Método:** `POST`
* **Rota:** `/api/reservas`
* **Status:** `201 Created` ou `400 Bad Request`
* **Automações em Segundo Plano:**
  1. Criação do **código localizador único** de 6 caracteres (ex.: `K7XQ2M`).
  2. Data e hora gravadas automaticamente com o timestamp do sistema.
  3. Status padrão definido como `Confirmada`.
  4. Envio de e-mail automático em formato HTML para o passageiro informando a confirmação da reserva.
* **Corpo da Requisição (Body - `ReservaRequestDTO`):**
  ```json
  {
    "idPassageiro": 1,
    "idVoo": 2,
    "tarifa": 589.90,
    "qtdBagagens": 2
  }
  ```

### 3.4. Atualizar reserva
* **Método:** `PUT`
* **Rota:** `/api/reservas/{id}`
* **Status:** `200 OK`, `400 Bad Request` ou `404 Not Found`
* **Corpo da Requisição (Body):**
  ```json
  {
    "tarifa": 620.00,
    "qtdBagagens": 3
  }
  ```

### 3.5. Excluir / Cancelar reserva
* **Método:** `DELETE`
* **Rota:** `/api/reservas/{id}`
* **Status:** `204 No Content` ou `404 Not Found`

---

## 💻 Exemplos Rápidos com cURL

```bash
# 1. Cadastrar Passageiro
curl -X POST http://localhost:8080/api/passageiros \
  -H "Content-Type: application/json" \
  -d '{"nome":"Lucas Moura","cpf":"111.222.333-99","idade":28,"email":"lucas@empresa.com"}'

# 2. Cadastrar Voo
curl -X POST http://localhost:8080/api/voos \
  -H "Content-Type: application/json" \
  -d '{"origem":"Curitiba","destino":"Salvador","horario":"2027-04-10T09:00:00","modeloAviao":"Embraer E195"}'

# 3. Emitir Reserva
curl -X POST http://localhost:8080/api/reservas \
  -H "Content-Type: application/json" \
  -d '{"idPassageiro":1,"idVoo":1,"tarifa":420.50,"qtdBagagens":1}'
```

