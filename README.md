
# 💰 Banco Digital CDB - Spring Boot

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green?logo=springboot)
![Maven](https://img.shields.io/badge/Maven-Dependency--Manager-red?logo=apachemaven)
![JWT](https://img.shields.io/badge/Security-JWT-orange?logo=jsonwebtokens)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)
![License](https://img.shields.io/badge/Licença-MIT-lightgrey)

---

## 📌 Visão Geral  
API RESTful desenvolvida em **Java 17** com **Spring Boot**, simulando um ambiente completo de banco digital. O projeto oferece operações bancárias como abertura de contas, movimentações financeiras, gestão de cartões, seguros e muito mais — tudo isso com foco em segurança, escalabilidade e boas práticas.

---

## 🛠️ Tecnologias e Arquitetura

- **Java 17**
- **Spring Boot** (Web, Data JPA, Security)
- **Maven** (gerenciamento de dependências)
- **Spring Security + JWT** (autenticação e autorização)
- **JPA/Hibernate** (persistência com banco relacional)
- **Swagger/OpenAPI** (documentação interativa)
- **DTOs** (para separação de camadas)
- **Tratamento global de exceções** com `@RestControllerAdvice`

---

## 📁 Estrutura de Pacotes

| Pacote        | Responsabilidade                                        |
|---------------|---------------------------------------------------------|
| `controller`  | Exposição de endpoints REST                             |
| `service`     | Lógica de negócio e orquestração                        |
| `entity`      | Entidades persistentes do domínio                       |
| `dto`         | Objetos de transferência entre camadas                  |
| `handler`     | Tratamento global de erros e exceções                   |
| `security`    | Configurações de autenticação e filtros de segurança    |
| `config`      | Configurações gerais do projeto                         |

---

## ✅ Funcionalidades Implementadas

- **Contas Bancárias**  
  Abertura, consulta, exclusão, depósito, saque, transferência, Pix, aplicação de rendimentos e taxa de manutenção.

- **Clientes**  
  Cadastro, consulta e exclusão.

- **Cartões**  
  Geração de cartões, consulta, fatura e tipos disponíveis.

- **Seguros**  
  Contratação, consulta, exclusão e listagem.

- **Recuperação de Senha**  
  Endpoint dedicado para redefinir acesso.

- **Integrações externas**  
  CEP, CPF e e-mail.

- **Segurança**  
  JWT, filtros customizados e controle por roles.

---

## 🧱 Boas Práticas Aplicadas

- Identificadores com **UUID**
- Uso de **DTOs** para separar as camadas
- **Validações e exceções** centralizadas
- Documentação interativa com **Swagger/OpenAPI**
- Princípios **SOLID** e **Clean Code**
- Separação de responsabilidades bem definida

---

## 🔗 Principais Endpoints

- `POST /contas` – Criar nova conta  
- `GET /clientes/{id}` – Consultar cliente  
- `POST /cartoes` – Gerar cartão  
- `POST /seguros` – Contratar seguro  
- `POST /esqueci-minha-senha` – Recuperar senha  

📚 A documentação completa (com exemplos de payloads) está disponível via Swagger:



[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
ou
[http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)


---

## 📸 Interface Swagger (exemplo)

![Swagger UI](https://media.licdn.com/dms/image/v2/D4D22AQE7JszU7CbhQw/feedshare-shrink_2048_1536/B4DZfX1m0qGYAs-/0/1751672844317?e=1755129600&v=beta&t=UxvhSHWFlzrQe6mER4osUZpNHaSt3P6wEn54ebJnzyQ)


---

## 🚀 Como Executar e Testar

1. Clone o repositório:
   git clone https://github.com/WashingtonDDS/bancoDigitalSpringCDB


2. Configure o banco de dados (H2 ou PostgreSQL).
3. Execute o projeto via Maven ou diretamente pelo IntelliJ.
4. Acesse o Swagger e explore os endpoints.

---

## 🌟 Diferenciais do Projeto

* Cobertura de operações bancárias reais
* Estrutura modular e escalável
* Segurança robusta com JWT
* Integrações externas úteis (CEP, CPF, e-mail)
* Código limpo e bem documentado
* Pronto para ambientes em nuvem (Cloud Ready)

---

## 🙋‍♂️ Sobre Mim

Olá! Sou Washington Luiz, desenvolvedor Full Stack apaixonado por criar soluções robustas e bem estruturadas.
Atualmente me especializo em **Java com Spring Boot**, além de já ter experiências sólidas com **Node.js, TypeScript e integração de APIs**.
Este projeto representa meu comprometimento com boas práticas, aprendizado contínuo e entrega de valor real em ambientes profissionais.

📫 Me encontre no [LinkedIn](https://www.linkedin.com/in/washington-silva-dds/) ou confira outros projetos no [GitHub](https://github.com/WashingtonDDS).

---


