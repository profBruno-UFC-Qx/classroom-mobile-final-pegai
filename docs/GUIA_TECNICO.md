# Guia de Especificação Funcional - Pegaí

**Status:** Rascunho Inicial
**Objetivo:** Definir as regras de negócio, dados e telas para garantir que todos desenvolvam na mesma direção.

---

## 1. Visão Geral e Glossário
Para evitar confusão durante as conversas da equipe:
* **Locador:** O dono do objeto (quem empresta).
* **Locatário:** O aluno que precisa do objeto (quem aluga).
* **Objeto/Item:** O material acadêmico sendo negociado.
* **Vistoria:** O ato de tirar fotos para comprovar o estado do item.

---

## 2. Estrutura de Dados (Banco de Dados Conceitual)
Aqui definimos quais informações precisamos salvar.

### Coleção: Usuários (Users)
Informações do perfil do aluno.
* **Nome Completo:** Texto.
* **Matrícula/Curso:** Texto (para validação institucional).
* **Reputação:** Número decimal (0 a 5 estrelas).
* **Saldo/Carteira:** Valor numérico (para simulação de pagamentos).
* **Histórico:** Lista de IDs dos aluguéis passados.

### Coleção: Produtos (Products)
Os itens disponíveis no feed.
* **Título:** Texto (ex: "Calculadora HP 12c").
* **Descrição:** Texto detalhado sobre o estado.
* **Preço Diária:** Valor monetário.
* **Fotos:** Lista de imagens (URLs).
* **Localização:** Latitude/Longitude (para o mapa).
* **Dono:** ID do Usuário que postou.
* **Status do Item:** "Disponível", "Alugado" ou "Inativo".

### Coleção: Pedidos de Aluguel (Rentals)
A tabela que liga quem alugou o quê.
* **Datas:** Data de Início e Data de Fim combinadas.
* **Status do Pedido:** (Ver Máquina de Estados abaixo).
* **Check-in (Entrega):** Fotos tiradas pelo Locador na hora da entrega.
* **Check-out (Devolução):** Fotos tiradas e Avaliação final.

---

## 3. Fluxo de Telas (Sitemap)

### A. Autenticação (Entrada)
1.  **Tela de Login:**
    * Se errar senha: Exibir erro "Credenciais inválidas".
    * Botão "Criar Conta": Leva para o Cadastro.
2.  **Tela de Cadastro:**
    * Campos obrigatórios: Nome, Email Acadêmico, Senha.
    * Ao salvar: Já loga o usuário e leva para a Home.

### B. Navegação Principal (Bottom Bar)
O app terá 3 abas fixas na parte inferior:

**Aba 1: Explorar (Home)**
* **Visualização:** Alternância entre "Lista" e "Mapa".
* **Busca:** Campo de texto no topo para filtrar por nome do item.
* **Ação:** Clicar num card abre os "Detalhes do Produto".

**Aba 2: Meus Pedidos**
* Deve ser dividida em duas sub-abas no topo:
    1.  **"Quero Pagar" (Locatário):** Pedidos que eu fiz.
    2.  **"Quero Receber" (Locador):** Itens meus que estão alugados.
* Cada item da lista mostra o status atual (ex: "Aguardando Vistoria").

**Aba 3: Perfil**
* Mostra a foto e a nota média do usuário.
* Opção de "Editar Perfil" e "Sair (Logout)".

---

## 4. Funcionalidades Complexas (Regras de Negócio)

### Funcionalidade: Vistoria Digital
Esta é a parte central da segurança do app.
1.  O **Locador** deve abrir o pedido no app na frente do Locatário.
2.  Clicar em "Realizar Vistoria de Entrega".
3.  A câmera abre (não pode usar foto da galeria, para evitar fraude).
4.  As fotos são enviadas.
5.  O status do pedido muda automaticamente para "Em Andamento".

### Funcionalidade: Busca por Localização
1.  O app pede permissão de GPS.
2.  O Feed filtra apenas itens dentro de um raio de **X km** (ex: 5km) da posição atual do aluno.
3.  Se não houver itens próximos, mostrar mensagem "Nenhum item na sua região".

### Funcionalidade: Sistema de Reputação
A nota não é inserida manualmente pelo admin. Ela é calculada:
* Após a devolução, o Locador dá nota de 1 a 5 para o Locatário.
* O Locatário dá nota de 1 a 5 para o Item.
* **Média:** Soma de todas as notas recebidas / Número de avaliações.

---

## 5. Simulação de Pagamento (Mock)
Como não teremos transação bancária real:
1.  Na tela de "Confirmar Aluguel", o usuário insere qualquer dado de cartão.
2.  **Regra de Teste:**
    * Final `0000`: O app deve simular ERRO ("Cartão Recusado").
    * Qualquer outro número: O app simula SUCESSO ("Pagamento Aprovado").
3.  Isso serve para o professor ver que tratamos os dois fluxos (sucesso e falha).
