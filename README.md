# 🚨 Alerta360 - Sistema de Monitoramento de Sensores

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Oracle](https://img.shields.io/badge/Oracle-Database-red.svg)](https://www.oracle.com/database/)
[![Maven](https://img.shields.io/badge/Maven-Build-blue.svg)](https://maven.apache.org/)

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Executando a Aplicação](#executando-a-aplicação)
- [API Endpoints](#api-endpoints)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Modelo de Dados](#modelo-de-dados)
- [Sistema de Alertas](#sistema-de-alertas)
- [Dashboard](#dashboard)
- [Contribuição](#contribuição)
- [Licença](#licença)

## 🎯 Sobre o Projeto

O **Alerta360** é uma aplicação robusta de monitoramento de sensores em tempo real, desenvolvida com Spring Boot. O sistema permite o gerenciamento completo de sensores, coleta de dados, geração automática de alertas baseada em regras personalizáveis e visualização através de um dashboard interativo.

### Principais Características

- 📊 **Monitoramento em Tempo Real**: Coleta e processa dados de sensores continuamente
- 🔔 **Sistema de Alertas Inteligente**: Geração automática de alertas baseada em regras configuráveis
- 👥 **Gerenciamento de Usuários**: Sistema completo de autenticação e autorização
- 📈 **Dashboard Analítico**: Visualização de dados e métricas em tempo real
- 🏗️ **Arquitetura RESTful**: APIs bem estruturadas seguindo padrões REST
- 🔒 **Segurança**: Implementação robusta de segurança com Spring Security

## ⚡ Funcionalidades

### 🔧 Gestão de Sensores
- ✅ Cadastro, edição e remoção de sensores
- ✅ Monitoramento do status dos sensores (ativo/inativo)
- ✅ Controle de localização e tipo de sensor
- ✅ Histórico de leituras por sensor

### 📊 Coleta de Dados
- ✅ Recepção de dados em formato JSON flexível
- ✅ Validação automática de dados recebidos
- ✅ Armazenamento de timestamps para análise temporal
- ✅ Status de qualidade das leituras

### 🚨 Sistema de Alertas
- ✅ Regras de alerta configuráveis por parâmetro
- ✅ Detecção automática de sensores offline
- ✅ Prevenção de spam de alertas (throttling)
- ✅ Resolução manual de alertas
- ✅ Categorização por tipos de alerta

### 👤 Gerenciamento de Usuários
- ✅ Três níveis de acesso: Admin, Operador, Visualizador
- ✅ Cadastro e autenticação de usuários
- ✅ Controle de permissões por funcionalidade

### 📈 Dashboard e Relatórios
- ✅ Resumo executivo do sistema
- ✅ Status em tempo real de todos os sensores
- ✅ Métricas de saúde do sistema
- ✅ Indicadores de performance

## 🏗️ Arquitetura

A aplicação segue uma arquitetura em camadas bem definida:

```
┌─────────────────┐
│   Controllers   │ ← Camada de Apresentação (REST APIs)
├─────────────────┤
│    Services     │ ← Camada de Negócio (Lógica de Aplicação)
├─────────────────┤
│  Repositories   │ ← Camada de Dados (Acesso ao Banco)
├─────────────────┤
│     Models      │ ← Camada de Entidades (Mapeamento JPA)
└─────────────────┘
```

### Componentes Principais

- **Controllers**: Exposição das APIs REST
- **Services**: Implementação da lógica de negócio
- **Repositories**: Acesso aos dados via Spring Data JPA
- **Models**: Entidades JPA mapeadas para o banco Oracle
- **Utils**: Utilitários e regras de negócio
- **Config**: Configurações de segurança e aplicação
- **Exception**: Tratamento centralizado de exceções

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **Spring Web** - APIs REST
- **Hibernate** - ORM (Object-Relational Mapping)

### Banco de Dados
- **Oracle Database** - Banco de dados principal
- **Oracle JDBC Driver** - Conectividade com Oracle

### Ferramentas de Desenvolvimento
- **Maven** - Gerenciamento de dependências e build
- **Lombok** - Redução de código boilerplate
- **Spring DevTools** - Desenvolvimento com hot reload

### Utilitários
- **Jackson** - Serialização/deserialização JSON
- **BCrypt** - Criptografia de senhas

## 📋 Pré-requisitos

Antes de executar a aplicação, certifique-se de ter instalado:

- ☑️ **Java 17** ou superior
- ☑️ **Maven 3.6** ou superior
- ☑️ **Oracle Database** (versão 11g ou superior)
- ☑️ **Git** para controle de versão

### Verificação dos Pré-requisitos

```bash
# Verificar versão do Java
java -version

# Verificar versão do Maven
mvn -version

# Verificar conectividade com Oracle
sqlplus usuario/senha@localhost:1521/FREEPDB1
```

## 🚀 Instalação

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/alerta360.git
cd alerta360
```

### 2. Configure o Banco de Dados Oracle

Crie um banco de dados Oracle e um usuário específico para a aplicação:

```sql
-- Conecte como SYSDBA
CREATE USER alerta360 IDENTIFIED BY DBalerta360;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE TRIGGER TO alerta360;
GRANT UNLIMITED TABLESPACE TO alerta360;

-- Conecte como o usuário alerta360
-- As tabelas serão criadas automaticamente pelo Hibernate
```

### 3. Instale as Dependências

```bash
mvn clean install
```

## ⚙️ Configuração

### Arquivo application.properties

Edite o arquivo `src/main/resources/application.properties`:

```properties
# Configurações da Aplicação
spring.application.name=alerta360
server.port=3000

# Configurações do Banco de Dados Oracle
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/FREEPDB1
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# Configurações do JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=true
```

### Configurações Personalizáveis

| Propriedade | Descrição | Valor Padrão |
|-------------|-----------|--------------|
| `server.port` | Porta da aplicação | 3000 |
| `spring.jpa.show-sql` | Exibir queries SQL | true |
| `spring.jpa.hibernate.ddl-auto` | Criação automática de tabelas | update |

## 🏃‍♂️ Executando a Aplicação

### Modo Desenvolvimento

```bash
# Usando Maven
mvn spring-boot:run

# Ou usando Java diretamente
mvn clean package
java -jar target/alerta360-0.0.1-SNAPSHOT.jar
```

### Modo Produção

```bash
# Gerar JAR otimizado
mvn clean package -Pprod

# Executar aplicação
java -jar -Dspring.profiles.active=prod target/alerta360-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:3000`

## 🌐 API Endpoints

### 📊 Dashboard

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/dashboard/resumo` | Resumo geral do sistema |
| GET | `/api/dashboard/sensores/status` | Status de todos os sensores |
| GET | `/api/dashboard/monitoramento` | Dados completos de monitoramento |
| GET | `/api/dashboard/health` | Verificação de saúde do sistema |

### 🔧 Sensores

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/sensores` | Listar todos os sensores |
| GET | `/api/sensores/{codigo}` | Buscar sensor por código |
| POST | `/api/sensores` | Cadastrar novo sensor |
| PUT | `/api/sensores/{id}` | Atualizar sensor |
| DELETE | `/api/sensores/{id}` | Remover sensor |

### 📈 Leituras de Sensores

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/leituras` | Registrar nova leitura |
| GET | `/api/leituras/sensor/{codigo}` | Leituras de um sensor |
| GET | `/api/leituras/recentes` | Últimas leituras do sistema |

### 🚨 Alertas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/alertas` | Listar alertas ativos |
| GET | `/api/alertas/sensor/{codigo}` | Alertas de um sensor |
| PUT | `/api/alertas/{id}/resolver` | Resolver alerta |
| POST | `/api/alertas` | Criar alerta manual |

### 👥 Usuários

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/usuarios` | Listar usuários |
| POST | `/api/usuarios` | Cadastrar usuário |
| PUT | `/api/usuarios/{id}` | Atualizar usuário |
| DELETE | `/api/usuarios/{id}` | Remover usuário |

### Exemplos de Uso

#### Registrar Leitura de Sensor

```bash
curl -X POST http://localhost:3000/api/leituras \
  -H "Content-Type: application/json" \
  -d '{
    "codigoSensor": "TEMP001",
    "dados": {
      "temperatura": 25.5,
      "umidade": 60.2,
      "pressao": 1013.25
    },
    "status": "OK"
  }'
```

#### Cadastrar Sensor

```bash
curl -X POST http://localhost:3000/api/sensores \
  -H "Content-Type: application/json" \
  -d '{
    "codigoSensor": "TEMP001",
    "nome": "Sensor Temperatura Sala 1",
    "tipo": "TEMPERATURA",
    "localizacao": "Sala 1 - Andar 2",
    "ativo": true
  }'
```

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/alerta360/
│   │   ├── config/              # Configurações
│   │   │   └── SecurityConfiguration.java
│   │   ├── controller/          # Controllers REST
│   │   │   ├── AlertaController.java
│   │   │   ├── DashboardController.java
│   │   │   ├── LeituraSensorController.java
│   │   │   ├── SensorController.java
│   │   │   └── UsuarioController.java
│   │   ├── exception/           # Tratamento de exceções
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── alerta/
│   │   │   ├── leitura_sensor/
│   │   │   ├── sensor/
│   │   │   └── usuario/
│   │   ├── model/               # Entidades JPA
│   │   │   ├── Alerta.java
│   │   │   ├── LeituraSensor.java
│   │   │   ├── Sensor.java
│   │   │   └── Usuario.java
│   │   ├── repository/          # Repositórios de dados
│   │   │   ├── AlertaRepository.java
│   │   │   ├── LeituraSensorRepository.java
│   │   │   ├── SensorRepository.java
│   │   │   └── UsuarioRepository.java
│   │   ├── service/             # Camada de negócio
│   │   │   ├── AlertaService.java
│   │   │   ├── DashboardService.java
│   │   │   ├── LeituraSensorService.java
│   │   │   ├── SensorService.java
│   │   │   └── UsuarioService.java
│   │   ├── utils/               # Utilitários
│   │   │   ├── RegraAlerta.java
│   │   │   └── RegrasAlertaProvider.java
│   │   └── Alerta360Application.java
│   └── resources/
│       ├── application.properties
│       ├── static/
│       └── templates/
└── test/                        # Testes unitários e integração
```

## 🗄️ Modelo de Dados

### Entidades Principais

#### Sensor
```java
- id: Long (PK)
- codigoSensor: String (UNIQUE)
- nome: String
- tipo: String
- localizacao: String
- ativo: Boolean
- dataCadastro: LocalDateTime
- ultimaLeitura: LocalDateTime
```

#### LeituraSensor
```java
- id: Long (PK)
- sensor: Sensor (FK)
- dados: String (JSON)
- timestamp: LocalDateTime
- status: String
```

#### Alerta
```java
- id: Long (PK)
- sensor: Sensor (FK)
- leitura: LeituraSensor (FK)
- tipoAlerta: String
- mensagem: String
- dataHora: LocalDateTime
- resolvido: Boolean
- dataResolucao: LocalDateTime
```

#### Usuario
```java
- id: Long (PK)
- email: String (UNIQUE)
- nome: String
- senha: String (encrypted)
- ativo: Boolean
- dataCadastro: LocalDateTime
- tipo: TipoUsuario (ADMIN/OPERADOR/VISUALIZADOR)
```

### Relacionamentos

- Um **Sensor** pode ter muitas **LeiturasSensor** (1:N)
- Uma **LeituraSensor** pode gerar muitos **Alertas** (1:N)
- Um **Sensor** pode ter muitos **Alertas** (1:N)

## 🚨 Sistema de Alertas

### Tipos de Alertas

| Tipo | Descrição |
|------|-----------|
| `TEMPERATURA_ALTA` | Temperatura acima do limite |
| `TEMPERATURA_BAIXA` | Temperatura abaixo do limite |
| `UMIDADE_ALTA` | Umidade acima do limite |
| `UMIDADE_BAIXA` | Umidade abaixo do limite |
| `PRESSAO_ALTA` | Pressão acima do limite |
| `PRESSAO_BAIXA` | Pressão abaixo do limite |
| `SENSOR_OFFLINE` | Sensor não envia dados |
| `ERRO_LEITURA` | Erro na coleta de dados |

### Regras de Alerta

As regras são configuradas na classe `RegrasAlertaProvider` e incluem:

- **Operadores suportados**: `>`, `<`, `>=`, `<=`, `==`
- **Campos monitorados**: Qualquer campo numérico dos dados JSON
- **Throttling**: Previne spam - apenas 1 alerta do mesmo tipo por sensor a cada 2 horas
- **Mensagens personalizadas**: Com interpolação de valores

### Configuração de Regras

Exemplo de configuração de regra:

```java
new RegraAlerta("temperatura", 30.0, ">", "TEMPERATURA_ALTA", 
                "Temperatura crítica detectada: {valor}°C")
```

## 📊 Dashboard

### Métricas Disponíveis

- **Total de Sensores**: Sensores cadastrados no sistema
- **Sensores Ativos**: Sensores enviando dados
- **Alertas Ativos**: Alertas não resolvidos
- **Última Atualização**: Timestamp da última sincronização

### Status do Sistema

| Status | Critério |
|--------|----------|
| `OK` | Sem alertas críticos, >80% sensores ativos |
| `ATENÇÃO` | 1-5 alertas ativos ou <80% sensores ativos |
| `CRÍTICO` | Mais de 5 alertas ativos |

### Endpoints do Dashboard

- `/api/dashboard/resumo` - Métricas gerais
- `/api/dashboard/sensores/status` - Status detalhado dos sensores
- `/api/dashboard/health` - Indicadores de saúde
- `/api/dashboard/monitoramento` - Dados completos para dashboards

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=AlertaServiceTest

# Testes de integração
mvn verify
```

### Cobertura de Código

```bash
mvn jacoco:report
```

Relatório disponível em: `target/site/jacoco/index.html`

## 🚀 Deploy

### Docker (Opcional)

```dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/alerta360-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Variáveis de Ambiente

```bash
export DB_URL=jdbc:oracle:thin:@prod-server:1521/PRODPDB1
export DB_USERNAME=alerta360_prod
export DB_PASSWORD=senha_segura_prod
export SERVER_PORT=8080
```

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código

- Use **camelCase** para variáveis e métodos
- Use **PascalCase** para classes
- Documente métodos públicos com JavaDoc
- Siga as convenções do Spring Boot
- Mantenha cobertura de testes > 80%

## 📈 Roadmap

### Versão 1.1.0
- [ ] Interface web completa
- [ ] Notificações por email/SMS
- [ ] Relatórios em PDF
- [ ] Autenticação JWT

### Versão 1.2.0
- [ ] Análise preditiva com IA
- [ ] Dashboard em tempo real (WebSocket)
- [ ] API para dispositivos IoT
- [ ] Backup automático

### Versão 2.0.0
- [ ] Microserviços
- [ ] Suporte a múltiplos bancos
- [ ] Cache distribuído (Redis)
- [ ] Métricas avançadas (Prometheus)

## 📞 Suporte

Para suporte técnico ou dúvidas:

- 📧 **Email**: suporte@alerta360.com
- 📋 **Issues**: [GitHub Issues](https://github.com/seu-usuario/alerta360/issues)
- 📖 **Wiki**: [Documentação Técnica](https://github.com/seu-usuario/alerta360/wiki)

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<div align="center">

**Alerta360** - Sistema de Monitoramento de Sensores  
Desenvolvido com ❤️ usando Spring Boot

[⬆ Voltar ao topo](#-alerta360---sistema-de-monitoramento-de-sensores)

</div> 