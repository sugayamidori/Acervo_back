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