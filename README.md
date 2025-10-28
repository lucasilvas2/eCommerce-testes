# Instruções do Projeto

Este documento descreve como executar o projeto, seus testes e verificar a cobertura dos testes.

## Como executar o projeto

O projeto utiliza Maven como ferramenta de build. Para executar o projeto, você pode usar os seguintes comandos:

1. Primeiro, compile o projeto:
   ```bash
   mvn clean compile
   ```

2. Para executar a aplicação:
   ```bash
   mvn spring-boot:run
   ```

## Como executar os testes

Para executar os testes automatizados, você tem algumas opções:

1. Executar todos os testes:
   ```bash
   mvn test
   ```

2. Executar uma classe de teste específica:
   ```bash
   mvn test -Dtest=NomeDaClasse
   ```

3. Executar um método de teste específico:
   ```bash
   mvn test -Dtest=NomeDaClasse#nomeDoMetodo
   ```

## Como verificar a cobertura dos testes

O projeto utiliza JaCoCo para gerar relatórios de cobertura de testes. Para verificar a cobertura:

1. Execute os testes com o relatório de cobertura:
   ```bash
   mvn clean verify
   ```

2. O relatório será gerado em HTML e pode ser encontrado em:
   ```
   target/site/jacoco/index.html
   ```

3. Abra o arquivo index.html em seu navegador para ver:
   - Cobertura por pacote
   - Cobertura por classe
   - Detalhes de cobertura de código (linhas, branches, etc.)
   - Visualização do código fonte com highlights de cobertura

Os relatórios do JaCoCo mostrarão em verde as linhas cobertas pelos testes, em amarelo os branches parcialmente cobertos e em vermelho o código não coberto.

## Análise MC/DC (Modified Condition/Decision Coverage)

A análise MC/DC foi realizada para a decisão composta mais complexa do código: o cálculo de desconto que considera tanto o tipo de produto quanto o valor total do carrinho.

### Condições Analisadas

A decisão envolve as seguintes condições:
1. C1: Quantidade de itens do mesmo tipo >= 3
2. C2: Quantidade de itens do mesmo tipo >= 5
3. C3: Quantidade de itens do mesmo tipo >= 8
4. C4: Valor total do carrinho > R$ 1000,00
5. C5: Valor total do carrinho > R$ 500,00

### Tabela de Decisão MC/DC

| Caso de Teste | C1 | C2 | C3 | C4 | C5 | Resultado Esperado | Descrição do Caso |
|---------------|----|----|----|----|----|--------------------|-------------------|
| CT1 | F | F | F | F | F | Sem desconto | Carrinho com 2 itens do mesmo tipo, valor total R$ 200,00 |
| CT2 | T | F | F | F | F | 5% desconto tipo | Carrinho com 3 itens do mesmo tipo, valor total R$ 300,00 |
| CT3 | T | T | F | F | T | 10% tipo + 10% valor | Carrinho com 5 itens do mesmo tipo, valor total R$ 600,00 |
| CT4 | T | T | T | T | T | 15% tipo + 20% valor | Carrinho com 8 itens do mesmo tipo, valor total R$ 1200,00 |
| CT5 | F | F | F | T | T | 20% valor | Carrinho com 2 itens do mesmo tipo, valor total R$ 1100,00 |
| CT6 | F | F | F | F | T | 10% valor | Carrinho com 2 itens do mesmo tipo, valor total R$ 600,00 |
| CT7 | T | T | F | T | T | 10% tipo + 20% valor | Carrinho com 6 itens do mesmo tipo, valor total R$ 1100,00 |

### Explicação da Cobertura

1. Influência de C1 (>= 3 itens):
   - Compare CT1 e CT2: Apenas C1 muda, alterando o resultado
   
2. Influência de C2 (>= 5 itens):
   - Compare CT2 e CT3: C2 muda, alterando o percentual de desconto

3. Influência de C3 (>= 8 itens):
   - Compare CT3 e CT4: C3 muda, aumentando o desconto por tipo

4. Influência de C4 (> R$ 1000):
   - Compare CT6 e CT5: Apenas C4 muda, alterando o desconto por valor

5. Influência de C5 (> R$ 500):
   - Compare CT1 e CT6: Apenas C5 muda, aplicando desconto por valor

Esta análise demonstra que cada condição individualmente afeta o resultado final, e todos os casos estão cobertos por testes específicos.

## Análise de Complexidade e Caminhos

### Grafo de Fluxo de Controle (CFG)

O grafo de fluxo de controle do método `calcularCustoTotal` é apresentado abaixo:

```
[1] Início
 |
 ↓
[2] Validar entradas (cliente e carrinho)
 |
 ↓
[3] Calcular subtotal
 |
 ↓
[4] Calcular desconto por itens do mesmo tipo
 |
 ↓
[5] Verificar valor do subtotal → [6] Aplicar desconto por valor
 |                                 |
 |                                 ↓
 |                               [7] Subtrair desconto do subtotal
 |                                 |
 ↓                                |
[8] Calcular valor do frete        |
 |                                |
 ↓                                |
[9] Verificar tipo do cliente      |
 |                                |
 ↓                                |
[10] Aplicar desconto no frete     |
 |                                |
 ↓                                |
[11] Somar subtotal com frete ←────┘
 |
 ↓
[12] Retornar total
```

### Cálculo da Complexidade Ciclomática (V(G))

A complexidade ciclomática pode ser calculada usando a fórmula:
V(G) = E - N + 2, onde:
- E = número de arestas
- N = número de nós

No grafo acima:
- Número de nós (N) = 12
- Número de arestas (E) = 13

Portanto:
V(G) = 13 - 12 + 2 = 3

Alternativamente, podemos calcular usando o número de decisões:
- Decisão 1: Validação de entrada (cliente/carrinho nulo)
- Decisão 2: Verificação de valor para desconto por subtotal
- Decisão 3: Verificação do tipo de cliente para desconto no frete

O que confirma V(G) = 3

### Número Mínimo de Casos de Teste Independentes

Com base na complexidade ciclomática V(G) = 3, precisamos de no mínimo 3 casos de teste independentes para cobrir todos os caminhos básicos do código. Estes casos devem cobrir:

1. Caminho feliz: cliente válido, com desconto por subtotal e tipo cliente ouro
2. Caminho alternativo: cliente válido, sem desconto por subtotal e tipo cliente bronze
3. Caminho de erro: cliente ou carrinho inválido (nulo)

Na prática, implementamos mais casos de teste para garantir uma cobertura adequada de todas as combinações de condições, conforme demonstrado na análise MC/DC anterior.

