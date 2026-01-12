[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/AR7CADm8)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=20867420)

# Proposta de Aplicativo

## Equipe
* **Nome do Aluno(a) 1:** Guilherme Barros Vieira de Araujo – 509873  
* **Nome do Aluno(a) 2:** Francisco Edinaldo dos Santos Silva – 586043  
* **Nome do Aluno(a) 3:** José Adrian Nascimento Silva – 475594  
* **Nome do Aluno(a) 4:** Petrucio de Carvalho Neves Filho – 469854  

---

## Título do Projeto
**Pegaí**

---

## Descrição do Projeto
O **Pegaí** é um aplicativo mobile *Cliente–Servidor* voltado para o aluguel de materiais acadêmicos entre estudantes, com o objetivo de reduzir custos e promover o reaproveitamento de recursos educacionais. Diferente de plataformas de classificados tradicionais, o Pegaí gerencia **todo o ciclo de vida da transação**, desde a descoberta do item até a devolução, garantindo maior segurança e confiabilidade entre os usuários.

A proposta do aplicativo é oferecer um ambiente seguro por meio de:
- Validação visual dos itens alugados;
- Sistema de reputação bilateral;
- Comunicação interna protegida;
- Controle de estados da transação.

A arquitetura do sistema foi projetada para atender aos requisitos de escalabilidade, organização e manutenção do código, utilizando o padrão **MVVM (Model–View–ViewModel)** aliado ao **Jetpack Compose** para a construção das interfaces gráficas de forma declarativa e reativa.

---

## Arquitetura e Estrutura
O aplicativo segue a arquitetura **MVVM**, promovendo separação de responsabilidades, desacoplamento entre camadas e maior testabilidade:

- **Model:** Camada responsável pelas entidades de domínio e regras de negócio.
- **View:** Interfaces gráficas desenvolvidas com **Jetpack Compose**, responsáveis pela apresentação e interação com o usuário.
- **ViewModel:** Intermediário entre View e Model, responsável por gerenciar estados, lógica de apresentação e comunicação com os repositórios, utilizando fluxo de dados reativos.
- **data/repositories** Responsável pela abstração dos métodos do firebase para acesso a dados.
- **data/utils** Responsável por expor funções e métodos gerais utilizados por qualquer componente da aplicação, seja Model, View, ViewModel e etc.

A persistência de dados é híbrida, combinando armazenamento remoto e local para melhor desempenho e experiência do usuário.

---

## Funcionalidades Principais

### Busca e Navegação
- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **Busca Georreferenciada**  
  Lista os produtos disponíveis de acordo com o filtro de localização do usuário, permitindo a visualização de itens próximos e facilitando a logística de empréstimo.

- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **Tendências**  
  Lista os produtos mais bem avaliados dentro da plataforma, destacando itens em evidência com base em métricas de avaliação e popularidade.

---

### Persistência e Preferências
- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **Persistência Local de Dados**  
  Salva localmente as preferências do usuário, como filtros de busca e configurações da aplicação, garantindo melhor desempenho e experiência de uso.

- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **Lista de Favoritos**  
  Permite que os usuários gerenciem itens favoritados, facilitando o acesso rápido a produtos de interesse.

---

### Comunicação e Fluxo de Empréstimo
- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **Chat e Gerenciamento do Empréstimo**  
  Responsável por gerenciar todo o ciclo de vida do empréstimo e devolução de produtos, permitindo a comunicação direta entre os usuários e o controle dos estados da transação.

- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **Notificações Internas (In-App)**  
  Notifica os usuários dentro do próprio aplicativo sobre novas mensagens no chat e mudanças de estado do empréstimo.

- ![Not Implemented](https://img.shields.io/badge/STATUS-NÃO%20IMPLEMENTADO-red?style=for-the-badge)

  **Notificações Push**  
  Responsável por notificar os usuários via push sobre mudanças de estado da aplicação sempre que necessário.

---

### Segurança e Confiabilidade
- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **Sistema de Reputação e Score**  
  Gerencia avaliações de usuários e produtos, oferecendo maior segurança tanto para quem empresta quanto para quem solicita empréstimos.

- ![Not Implemented](https://img.shields.io/badge/STATUS-NÃO%20IMPLEMENTADO-red?style=for-the-badge)

  **Vistoria Digital (Check-in com Câmera)**  
  Responsável por registrar imagens dos produtos no momento da entrega e da devolução, servindo como prova imutável do estado do item ao final de cada empréstimo.

---

### Pagamentos
- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **Simulação de Pagamento (PIX)**  
  Gerencia chaves e geração de QR Code para pagamentos simulados via PIX, reproduzindo o fluxo real de uma transação financeira.

---

### Gerenciamento de Dados (CRUD)
- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **CRUD de Usuários**  
  Gerencia cadastro, autenticação e atualização dos dados dos usuários da plataforma.

- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **CRUD de Produtos**  
  Gerencia os produtos cadastrados pelos usuários, permitindo criar, editar, listar e remover itens disponíveis para empréstimo.

- ![Implemented](https://img.shields.io/badge/STATUS-IMPLEMENTADO-brightgreen?style=for-the-badge)

  **CRUD de Avaliações**  
  Gerencia todas as avaliações da plataforma, incluindo avaliações de usuários como locadores, locatários e avaliações de produtos.


---

## Tecnologias Utilizadas

### Plataforma e Arquitetura

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=black)  
Plataforma alvo do projeto. O aplicativo foi desenvolvido para dispositivos Android, explorando recursos nativos como câmera, geolocalização e notificações.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=black)  
Linguagem de programação principal utilizada no desenvolvimento do aplicativo, escolhida por sua segurança, concisão e integração nativa com o ecossistema Android.

![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=black)  
Framework moderno para construção das interfaces gráficas de forma declarativa e reativa, permitindo maior produtividade, melhor gerenciamento de estado e integração direta com o padrão MVVM.

![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge)  
Padrão arquitetural adotado para separar responsabilidades entre interface, lógica de apresentação e regras de negócio, promovendo organização, testabilidade e manutenção do código.

---

### Backend e Serviços em Nuvem (Firebase)

![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)  
Plataforma utilizada como backend do aplicativo, oferecendo serviços gerenciados para autenticação, persistência de dados, armazenamento de mídia e comunicação em tempo real.

![Firebase Auth](https://img.shields.io/badge/Firebase%20Auth-FF6F00?style=for-the-badge&logo=firebase&logoColor=black)  
Responsável pela autenticação dos usuários da plataforma, permitindo cadastro, login e gerenciamento de sessões de forma segura.

![Firestore](https://img.shields.io/badge/Firestore-FFA000?style=for-the-badge&logo=firebase&logoColor=black)  
Banco de dados NoSQL utilizado para a persistência remota dos dados da aplicação, como usuários, produtos, empréstimos, avaliações e mensagens do chat.

![Firebase Storage](https://img.shields.io/badge/Firebase%20Storage-FF8F00?style=for-the-badge&logo=firebase&logoColor=black)  
Utilizado para o armazenamento remoto de arquivos de mídia, como imagens de produtos e registros de vistoria digital.

![Firebase Cloud Messaging](https://img.shields.io/badge/FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)  
Serviço destinado ao envio de notificações push para os usuários, informando eventos importantes da aplicação, como mudanças de estado e novas interações. *(Funcionalidade prevista, porém ainda não implementada).*

---

### Geolocalização

![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=for-the-badge&logo=googlemaps&logoColor=black)  
Utilizado para acesso à localização do usuário e exibição georreferenciada dos produtos disponíveis, permitindo filtros por proximidade e melhor experiência de navegação.

---

### Persistência Local

![DataStore](https://img.shields.io/badge/DataStore%20Preferences-3DDC84?style=for-the-badge&logo=android&logoColor=black)  
Utilizado para persistência local de dados simples, como preferências do usuário, configurações da aplicação e informações de cache, garantindo maior desempenho e usabilidade.

---

### 🛠️ Ferramentas de Desenvolvimento

![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=black)  
IDE oficial utilizada para o desenvolvimento do aplicativo Android, oferecendo suporte completo ao Kotlin, Jetpack Compose, emulação de dispositivos, depuração e integração com o Firebase.



## Instruções para Execução

> As instruções abaixo assumem que o Android Studio esta instalado e que esta sendo utilizado um sistema Linux passeado no Debian(Ubunto, Zorin, Pop-OS, etc).

```bash
# Clone o repositório
git clone https://github.com/profBruno-UFC-Qx/classroom-mobile-final-pegai.git

# Navegue até o diretório do projeto
cd classroom-mobile-final-pegai

# abra o projeto no Android Studio
android-studio mobile/

