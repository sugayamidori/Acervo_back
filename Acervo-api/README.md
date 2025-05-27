# 📚 AcervoAPI

**AcervoAPI** é um projeto Java com Spring Boot que simula o funcionamento de uma biblioteca via requisições RESTful. Ele contempla tecnologias modernas como:

- ✅ Spring Boot
- ✅ JPA/Hibernate
- ✅ REST APIs
- ✅ Autenticação com JWT e OAuth2
- ✅ Docker
- ✅ Integração com AWS

---

## ⚙️ Requisitos

- **Java JDK 21**
- **Maven**
- **Docker e Docker Compose (opcional)**
- **PostgreSQL 16.3**
- (Opcional) **pgAdmin 4 para gerenciamento**

---

## 🚀 Como Rodar Localmente

### 1. Clone o repositório

```bash
git clone https://github.com/sugayamidori/Acervo_back.git
cd acervoapi
```

### 2. Compile e instale as dependências

```bash
mvn clean -U install
```

---

## 🗃️ Configurando o Banco de Dados

Este projeto utiliza **PostgreSQL**. Recomenda-se utilizar o Docker para subir o ambiente rapidamente.

### 🔧 Passos com Docker

1. Crie uma rede Docker para comunicação entre os containers:

```bash
docker network create acervo-network
```

2. Suba o container do PostgreSQL:

```bash
docker run --name acervodb \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=acervo \
  -p 5432:5432 \
  -d --network acervo-network \
  postgres:16.3
```

3. (Opcional) Suba o pgAdmin para gerenciar o banco:

```bash
docker run --name pgadmin4 \
  -e PGADMIN_DEFAULT_EMAIL=admin@admin.com \
  -e PGADMIN_DEFAULT_PASSWORD=admin \
  -p 15432:80 \
  -d --network acervo-network \
  dpage/pgadmin4:8.9
```

📝 *Você pode alterar os valores das variáveis de ambiente conforme preferir.*

---

## 🛠️ Configurações da Aplicação

Configure as seguintes variáveis de ambiente na sua IDE ou terminal antes de rodar o projeto:

```env
DATASOURCE_URL=jdbc:postgresql://localhost:5432/acervo
DATASOURCE_USERNAME=postgres
DATASOURCE_PASSWORD=postgres
MERCADO_PAGO_TOKEN=token
```

### 🔐 (Opcional) Login com Google

Se desejar usar autenticação via Google OAuth2, crie credenciais no [Google Cloud Console](https://console.cloud.google.com/) e adicione:

```env
GOOGLE_CLIENT_ID=xxxxxxxxxxxx.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=xxxxxxxxxxxxxxxx
```

---

## 📬 Testando as Rotas

Importe o arquivo `operacoes_postman.json` no [Postman](https://www.postman.com/) para testar as operações da API.

---

## 🧾 Criação das Tabelas

As instruções SQL estão no arquivo [`comandos-sql.txt`](./comandos-sql.txt). Verifique esse arquivo caso queira criar manualmente as tabelas.

---
---

### 📖 Funcionalidades de Empréstimos e Multas

Abaixo, um guia para uso das funcionalidades de **empréstimos** e **multas** disponíveis nesta API.

---

#### 📚 Empréstimos

✅ **Registrar um Empréstimo**
* **Descrição:** Cria um novo empréstimo de um livro para um membro específico. A data de empréstimo será a data atual e a data limite de devolução será calculada automaticamente. O usuário que realiza a requisição será registrado como o responsável pelo empréstimo (`registradoPor`).
* **Método:** `POST`
* **URL:** `{{baseURL}}/emprestimos?idLivro={idLivro}&idMembro={idMembro}`

**Respostas Esperadas:**
    * `200 OK`: Empréstimo criado com sucesso. Retorna o objeto `Emprestimo` com os detalhes.
    * `400 Bad Request`: Livro não disponível, membro inválido, parâmetros ausentes ou mal formatados.
    * `404 Not Found`: Livro ou membro não encontrados com os IDs fornecidos.

✅ **Devolver Livro**
* **Descrição:** Marca um empréstimo como devolvido. A `dataDevolucao` será a data atual. Se a devolução ocorrer após a `dataLimiteDevolucao`, uma multa será gerada e associada ao empréstimo.
* **Método:** `PUT`
* **URL:** `{{baseURL}}/emprestimos/{idEmprestimo}/devolver`
* **Autorização:** **Protegida** (Necessário Token JWT válido com autoridade `BIBLIOTECARIO`ou `ADMIN`).
* **Corpo da Requisição (JSON):** `{}` (Requisição sem corpo, dados são passados na URL e contextualmente).
* **Respostas Esperadas:**
    * `200 OK`: Livro devolvido com sucesso. Retorna o objeto `Emprestimo` atualizado (incluindo dados da `multa` se gerada).
    * `404 Not Found`: Empréstimo não encontrado com o ID fornecido.
    * `400 Bad Request`: Empréstimo já devolvido ou status inválido para devolução.
    * `403 Forbidden`: Token sem autoridade autoridade `BIBLIOTECARIO`ou `ADMIN`.

✅ **Reservar Livro**
* **Descrição:** Cria uma reserva para um livro disponível para um usuário (membro). Um livro só pode ser reservado se estiver `DISPONIVEL`.
* **Método:** `POST`
* **URL:** `{{baseURL}}/emprestimos/reserva?idLivro={idLivro}`
* **Autorização:** **Livre** (Não exige autenticação JWT).
* **Corpo da Requisição (JSON):**
 
* **Respostas Esperadas:**
    * `200 OK`: Livro reservado com sucesso. Retorna o objeto `Emprestimo` com status `RESERVADO`.
    * `400 Bad Request`: Livro não disponível para reserva, membro inválido.
    * `404 Not Found`: Livro ou membro não encontrados.

✅ **Liberar Empréstimo Reservado**
* **Descrição:** Autoriza o empréstimo que estava no status `RESERVADO`, permitindo que ele seja efetivado e o emprestimo fique `EM_VIGENCIA`.
* **Método:** `PUT`
* **URL:** `{{baseURL}}/emprestimos/{idEmprestimo}/liberar`
* **Autorização:** **Protegida** (Necessário Token JWT válido com autoridade `BIBLIOTECARIO`).
* **Corpo da Requisição (JSON):** `{}`
* **Respostas Esperadas:**
    * `200 OK`: Empréstimo liberado com sucesso. Retorna o objeto `Emprestimo` atualizado.
    * `404 Not Found`: Empréstimo não encontrado.
    * `400 Bad Request`: Empréstimo não está em status `RESERVADO`.
    * `403 Forbidden`: Token sem autoridade `BIBLIOTECARIO`.

✅ **Listar Empréstimos**
* **Descrição:** Retorna a lista de todos os empréstimos registrados no sistema.
* **Método:** `GET`
* **URL:** `{{baseURL}}/emprestimos`
* **Autorização:** **Protegida** (Necessário Token JWT válido com autoridade `BIBLIOTECARIO`).
* **Respostas Esperadas:**
    * `200 OK`: Retorna uma lista de objetos `Emprestimo` detalhados (Título do livro, Nome do usuário, Status do empréstimo, Data de devolução, etc.). Pode ser uma lista vazia se não houver empréstimos.
    * `403 Forbidden`: Token sem autoridade `BIBLIOTECARIO`.

---

#### 💰 Multas e Pagamentos

Para todas as rotas de pagamento, a autorização com um **Token JWT válido** e com a autoridade `MEMBRO` é **obrigatória**.
A rota de multas é acionada quando um empréstimo é DEVOLVIDO_COM_ATRASO. 

✅ **Processar Pagamento de Multa (Simulado)**
* **Descrição:** Inicia o processamento de pagamento de uma multa associada a um empréstimo. **Importante:** A integração bancária é **simulada internamente no sistema**. Os campos de pagamento (ID de transação, status, QR Code, etc.) serão preenchidos por lógica interna do sistema.
* **Método:** `POST`
* **URL:** `{{baseURL}}/pagamentos/{idEmprestimo}` (O ID do empréstimo ao qual a multa está associada.)
* **Autorização:** **Protegida** (Necessário Token JWT válido com autoridade `MEMBRO`).
* **Corpo da Requisição (JSON) - Exemplos de Simulação:**

    * **Pagamento Aprovado:**
        ```json
        {
            "metodoPagamento": "CARTAO",      
            "sucesso": true                   
        }
        ```
    * ** Pagamento Rejeitado (Falha):**
        ```json
        {
            "metodoPagamento": "BOLETO",
            "sucesso": false                  
        }
        ```
    * **Pagamento Pendente:**
        * Ao omitir o campo `"sucesso"`, a simulação gerará um resultado aleatório. Há uma chance de que o pagamento seja `PENDENTE`, especialmente se o `metodoPagamento` for `PIX`.
        ```json
        {
            "metodoPagamento": "PIX"
        }
        ```
* **Respostas Esperadas:**
    * `200 OK`: Pagamento processado com sucesso. Retorna o objeto `Multa` atualizado com o `status` (ex: `PAGA`, `PENDENTE`, `REJEITADA`), `statusPagamento`, e (para PIX pendente) `qrCodeBase64` e `linkPagamento`.
    * `404 Not Found`: Empréstimo não encontrado com o ID fornecido, ou multa não encontrada para o empréstimo.
    * `400 Bad Request`: Multa já paga e não pode ser reprocessada.
    * `403 Forbidden`: Token sem autoridade `MEMBRO`.

---

## 🐳 Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Security
- JWT e OAuth2
- JPA + Hibernate
- PostgreSQL
- Docker
- AWS (para deploy futuro)

---

## 📌 Observações Finais

- Certifique-se de que as portas `5432` (PostgreSQL) e `15432` (pgAdmin) estejam livres no seu sistema.
- O projeto está estruturado para fácil deploy em containers e integração com serviços externos.
