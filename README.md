# Documentação do Projeto

Este documento contém instruções para execução do projeto, testes automatizados e análise de cobertura de código.

## Execução do Projeto

O projeto é construído com Maven. Execute os seguintes comandos:

1. Compilação:
   ```bash
   mvn clean compile
   ```

2. Execução da aplicação:
   ```bash
   mvn spring-boot:run
   ```

## Execução dos Testes

Comandos disponíveis para execução dos testes automatizados:

1. Todos os testes:
   ```bash
   mvn test
   ```

2. Teste de uma classe específica:
   ```bash
   mvn test -Dtest=NomeDaClasse
   ```

3. Teste de um método específico:
   ```bash
   mvn test -Dtest=NomeDaClasse#nomeDoMetodo
   ```

## Análise de Cobertura de Código

O projeto utiliza JaCoCo para análise de cobertura de código. Para gerar o relatório:

1. Execute:
   ```bash
   mvn clean verify
   ```

2. Localize o relatório em:
   ```
   target/site/jacoco/index.html
   ```

3. O relatório HTML apresenta:
   - Análise por pacote
   - Análise por classe
   - Detalhamento da cobertura de código
   - Visualização do código fonte com indicadores de cobertura

O relatório indica através de cores:
- Verde: código coberto pelos testes
- Amarelo: branches parcialmente cobertos
- Vermelho: código não coberto

## Partições
1. Desconto por Múltiplos Itens de Mesmo Tipo

| ID | Intervalo de Quantidade           | Desconto Aplicado |
|----|----------------------------------|-------------------|
| P1 | Quantidade < 3                    | 0%               |
| P2 | 3 ≤ Quantidade ≤ 4               | 5%               |
| P3 | 5 ≤ Quantidade ≤ 7               | 10%              |
| P4 | Quantidade ≥ 8                    | 15%              |

2. Desconto sobre subtotal

| ID | Faixa de Subtotal   | Desconto |
|----|-------------------- |----------|
| P5 | Subtotal ≤ R$500,00 | 0%       |
| P6 | R$500,00 < Subtotal ≤ R$1000,00 | 10%      |
| P7 | Subtotal > R$1000,00 | 20%      |

3. Cálculo do Frete por Peso

| ID  | Intervalo de Peso (kg)  | Valor por kg  |
|-----|------------------------|---------------|
| P8  | 0 ≤ peso ≤ 5          | R$ 0,00      |
| P9  | 5 < peso ≤ 10         | R$ 2,00      |
| P10 | 10 < peso ≤ 50        | R$ 4,00      |
| P11 | peso > 50             | R$ 7,00      |

4. Taxa Mínima de Frete

| ID  | Condição de Frete         | Taxa Aplicada |
|-----|--------------------------|---------------|
| P12 | Peso ≤ 5 kg             | R$ 0,00      |
| P13 | Peso > 5 kg             | R$ 12,00     |

5. Taxa de Manuseio Especial

| ID  | Status de Fragilidade    | Taxa de Manuseio |
|-----|-------------------------|------------------|
| P14 | Item frágil            | R$ 5,00 por item |
| P15 | Item não frágil        | R$ 0,00         |

6. Multiplicador Regional de Frete

| ID  | Região         | Multiplicador |
|-----|---------------|--------------|
| P16 | Sudeste       | 1,00        |
| P17 | Sul           | 1,05        |
| P18 | Nordeste      | 1,10        |
| P19 | Centro-Oeste  | 1,20        |
| P20 | Norte         | 1,30        |

7. Cálculo do Peso para Frete

| ID  | Relação entre Pesos     | Peso Utilizado  |
|-----|------------------------|-----------------|
| P21 | Físico > Cúbico        | Peso Físico     |
| P22 | Cúbico > Físico        | Peso Cúbico     |
| P23 | Físico = Cúbico        | Qualquer        |

8. Desconto por Nível de Cliente

| ID  | Nível do Cliente | Desconto no Frete |
|-----|-----------------|------------------|
| P24 | Ouro            | 100%             |
| P25 | Prata           | 50%              |
| P26 | Bronze          | 0%               |
9. Validação de Dados de Entrada

| ID  | Parâmetro   | Critério de Validação          |
|-----|------------|-------------------------------|
| P27 | Preço      | Maior ou igual a zero         |
| P28 | Preço      | Não aceita valores negativos  |
| P29 | Quantidade | Maior que zero                |
| P30 | Quantidade | Não aceita zero ou negativo   |
| P31 | Cliente    | Objeto cliente requerido      |
| P32 | Cliente    | Não aceita valor nulo         |
| P33 | Carrinho   | Objeto carrinho requerido     |

### Casos de Teste

| ID do Teste | Partições   | Descrição                         | Entrada                                                                                                                       | Resultado Esperado           |
|-------------|-------------|-----------------------------------|-------------------------------------------------------------------------------------------------------------------------------|------------------------------|
| TC-P1       | P1          | Sem desconto (<3 itens)           | preço=100.00, qtd=1                                                                                                           | total=100.00                 |
| TC-P2       | P2          | Desconto 5% (3-4 itens)           | preço=100.00, qtd=3                                                                                                           | total=285.00                 |
| TC-P3       | P3          | Desconto 10% (5-7 itens)          | preço=50.00, qtd=5                                                                                                            | total=225.00                 |
| TC-P4       | P4          | Desconto 15% (≥8 itens)           | preço=25.00, qtd=8                                                                                                            | total=170.00                 |
| TC-P5       | P1,P2,P3,P4 | Múltiplos tipos com descontos     | Item1: preço=25.00, qtd=1, tipo=ELETRONICO; Item2: preço=100.00, qtd=2, tipo=ROUPA; Item3: preço=50.00, qtd=3, tipo=LIVRO     | total=367.50                 |
| TC-P6       | P1,P2,P3,P4 | Múltiplos tipos/preços            | Item1: preço=150.00, qtd=2, tipo=ELETRONICO; Item2: preço=75.00, qtd=3, tipo=MOVEL; Item3: preço=100.00, qtd=1, tipo=ALIMENTO | total=551.25                 |
| TC-P7       | P5          | Subtotal ≤500                     | preço=200.00, qtd=2                                                                                                           | total=400.00                 |
| TC-P8       | P6          | Subtotal >500                     | preço=100.00, qtd=6                                                                                                           | total=540.00                 |
| TC-P9       | P7          | Subtotal >1000                    | preço=525.00, qtd=2                                                                                                           | total=840.00                 |
| TC-P10      | P8,P12      | Frete isento (peso≤5kg)           | peso=1.00, qtd=2                                                                                                              | frete=0.00 + taxaFragil      |
| TC-P11      | P9,P13      | Frete faixa B (5-10kg)            | peso=4.00, qtd=2                                                                                                              | frete=(8×2)+12 + taxaFragil  |
| TC-P12      | P10,P13     | Frete faixa C (10-50kg)           | peso=15.00, qtd=2                                                                                                             | frete=(30×4)+12 + taxaFragil |
| TC-P13      | P14         | Taxa frágil aplicada              | peso=1.00, itens=3, frágil=true                                                                                               | taxaFragil=15.00             |
| TC-P14      | P15         | Sem taxa frágil                   | peso=4.00, itens=2, frágil=false                                                                                              | taxaFragil=0.00              |
| TC-P15      | P16         | Multiplicador SUDESTE             | região=SUDESTE, frete=50.00                                                                                                   | frete×1.00                   |
| TC-P16      | P17         | Multiplicador SUL                 | região=SUL, frete=50.00                                                                                                       | frete×1.05                   |
| TC-P17      | P18         | Multiplicador NORDESTE            | região=NORDESTE, frete=50.00                                                                                                  | frete×1.10                   |
| TC-P18      | P19         | Multiplicador CENTRO_OESTE        | região=CENTRO_OESTE, frete=50.00                                                                                              | frete×1.20                   |
| TC-P19      | P20         | Multiplicador NORTE               | região=NORTE, frete=50.00                                                                                                     | frete×1.30                   |
| TC-P20      | P24         | Cliente OURO                      | tipo=OURO, frete=100.00                                                                                                       | frete=0.00                   |
| TC-P21      | P25         | Cliente PRATA                     | tipo=PRATA, frete=100.00                                                                                                      | frete=50.00                  |
| TC-P22      | P26         | Cliente BRONZE                    | tipo=BRONZE, frete=100.00                                                                                                     | frete=100.00                 |
| TC-P23      | P21         | Peso físico maior que peso cúbico | peso=10.00, dim=1.0×1.0×1.0 (peso cúbico=0.00017)                                                                             | total=192.00                 |
| TC-P24      | P22         | Peso cúbico maior que peso físico | peso=1.00, dim=3.0×3.0×3.0 (peso cúbico=4.05)                                                                                 | total=100.00                 |
| TC-P25      | P23         | Peso físico igual ao peso cúbico  | peso=4.50, dim=30.0×30.0×30.0 (peso cúbico=4.50)                                                                              | total=130.00                 |
| TC-P26      | P28         | Preço negativo                    | preço=-1.00                                                                                                                   | IllegalArgumentException     |
| TC-P27      | P30         | Quantidade inválida               | qtd=0                                                                                                                         | IllegalArgumentException     |
| TC-P28      | P32         | Cliente nulo                      | cliente=null                                                                                                                  | IllegalArgumentException         |
| TC-P29      | P33         | Carrinho nulo                     | carrinho=null                                                                                                                 | IllegalArgumentException     |

## Valores Limites

| ID do Teste | Variável Focada | Valor Testado | Critério/Limite | Resultado Esperado Chave | Partição |
|-------------|-----------------|---------------|-----------------|--------------------------|----------|
| TC-VL1      | Qtd. Itens/Tipo | 2             | Limite 3 (L-1)  | 0% Desconto              | P1       |
| TC-VL2      | Qtd. Itens/Tipo | 3             | Limite 3 (L)    | 5% Desconto              | P2       |
| TC-VL3      | Qtd. Itens/Tipo | 4             | Limite 4 (L)    | 5% Desconto              | P2       |
| TC-VL4      | Qtd. Itens/Tipo | 5             | Limite 5 (L)    | 10% Desconto             | P3       |
| TC-VL5      | Qtd. Itens/Tipo | 7             | Limite 7 (L)    | 10% Desconto             | P3       |
| TC-VL6      | Qtd. Itens/Tipo | 8             | Limite 8 (L)    | 15% Desconto             | P4       |
| TC-VL7      | Qtd. Itens/Tipo | 9             | Limite 8 (L+1)  | 15% Desconto             | P4       |

| ID do Teste | Variável Focada | Valor Testado  | Critério/Limite  | Resultado Esperado Chave | Partição |
|-------------|-----------------|----------------|------------------|--------------------------|----------|
| TC-VL8      | Subtotal        | R$ 499,99      | R$ 500,00 (L-1)  | 0% Desconto              | P5       |
| TC-VL9      | Subtotal        | R$ 500,00      | R$ 500,00 (L)    | 0% Desconto              | P5       |
| TC-VL10     | Subtotal        | R$ 500,01      | R$ 500,00 (L+1)  | 10% Desconto             | P6       |
| TC-VL11     | Subtotal        | R$ 999,99      | R$ 1000,00 (L-1) | 10% Desconto             | P6       |
| TC-VL12     | Subtotal        | R$ 1000,00     | R$ 1000,00 (L)   | 10% Desconto             | P6       |
| TC-VL13     | Subtotal        | R$ 1000,01     | R$ 1000,00 (L+1) | 20% Desconto             | P7       |

| ID do Teste | Variável Focada | Valor Testado | Critério/Limite       | Resultado Esperado Chave    | Partição |
|-------------|-----------------|---------------|-----------------------|-----------------------------|----------|
| TC-VL14     | Peso Total      | 4,99 kg       | Limite 5,00 kg (L-1)  | Isento (R$ 0,00/kg)         | P8       |
| TC-VL15     | Peso Total      | 5,00 kg       | Limite 5,00 kg (L)    | Isento (R$ 0,00/kg)         | P8       |
| TC-VL16     | Peso Total      | 5,01 kg       | Limite 5,00 kg (L+1)  | R$ 2,00/kg                  | P9       |
| TC-VL17     | Peso Total      | 9,99 kg       | Limite 10,00 kg (L-1) | R$ 2,00/kg                  | P9       |
| TC-VL18     | Peso Total      | 10,00 kg      | Limite 10,00 kg (L)   | R$ 2,00/kg                  | P9       |
| TC-VL19     | Peso Total      | 10,01 kg      | Limite 10,00 kg (L+1) | R$ 4,00/kg                  | P10      |
| TC-VL20     | Peso Total      | 49,99 kg      | Limite 50,00 kg (L-1) | R$ 4,00/kg                  | P10      |
| TC-VL21     | Peso Total      | 50,00 kg      | Limite 50,00 kg (L)   | R$ 4,00/kg                  | P10      |
| TC-VL22     | Peso Total      | 50,01 kg      | Limite 50,00 kg (L+1) | R$ 7,00/kg                  | P11      |

| ID do Teste | Variável Focada | Valor Testado | Critério/Limite              | Resultado Esperado Chave | Partição |
|-------------|-----------------|---------------|------------------------------|--------------------------|----------|
| TC-VL23     | Quantidade      | -1            | Limite 0 (L-1 - Inválido)    | IllegalArgumentException | P30      |
| TC-VL24     | Quantidade      | 0             | Limite 0 (L - Inválido)      | IllegalArgumentException | P30      |
| TC-VL25     | Quantidade      | 1             | Limite 0 (L+1 - Válido)      | Cálculo normal           | P29      |
| TC-VL26     | Preço           | -0,01         | Limite 0,00 (L-1 - Inválido) | IllegalArgumentException | P28      |
| TC-VL27     | Preço           | 0,00          | Limite 0,00 (L - Válido)     | Cálculo normal           | P24      |
| TC-VL28     | Preço           | 0,01          | Limite 0,00 (L+1 - Válido)   | Cálculo normal           | P24      |

| ID do Teste | Variável Focada | Valor Testado | Critério/Limite | Resultado Esperado Chave | Partição |
|-------------|-----------------|---------------|-----------------|--------------------------|----------|
| TC-VL29     | Cliente         | null          | Cliente Nulo    | NullPointerException     | P32      |
| TC-VL30     | Região          | null          | Região Nula     | IllegalArgumentException | -        |

| ID do Teste | Variável Focada    | Valor Testado  | Critério/Limite | Resultado Esperado Chave | Partição |
|-------------|--------------------|----------------|-----------------|--------------------------|----------|
| TC-VL31     | Peso Físico/Cúbico | PF=10, PC=9.99 | Limite PF>PC    | Usar Peso Físico         | P21      |
| TC-VL32     | Peso Físico/Cúbico | PF=10, PC=10   | Limite PF=PC    | Usar qualquer um         | P23      |
| TC-VL33     | Peso Físico/Cúbico | PF=9.99, PC=10 | Limite PF<PC    | Usar Peso Cúbico         | P22      |

| ID do Teste | Variável Focada | Valor Testado | Critério/Limite | Resultado Esperado Chave | Partição |
|-------------|-----------------|---------------|-----------------|--------------------------|----------|
| TC-VL34     | Nível Cliente   | OURO          | Cliente OURO    | Frete Final = R$ 0,00    | P24      |
| TC-VL35     | Nível Cliente   | PRATA         | Cliente PRATA   | 50% Desconto no Frete    | P25      |
| TC-VL36     | Nível Cliente   | BRONZE        | Cliente BRONZE  | Frete Integral           | P26      |


## Análise por Tabela de Decisão

### Regras de Validação

| Condição             | R1  | R2  | R3  | R4  | R5  |
|----------------------|:---:|:---:|:---:|:---:|:---:|
| Preço < 0            | S   | N   | N   | N   | N   |
| Quantidade ≤ 0       | -   | S   | N   | N   | N   |
| Cliente nulo         | -   | -   | S   | N   | N   |
| Região nula          | -   | -   | -   | S   | N   |
| Produto indisponível | -   | -   | -   | -   | S   |

**Resultado**

| Ação              | R1  | R2  | R3  | R4  | R5  |
|-------------------|:---:|:---:|:---:|:---:|:---:|
| Lançar exceção    | X   | X   | X   | X   | X   |
| Continuar cálculo | -   | -   | -   | -   | -   |

---

### Regras de Negócio (Principais)


### Regras de Negócio

| Condição               | R6  | R7   | R8   | R9  | R10   | R11         | R12  |
|------------------------|-----|------|------|-----|-------|-------------|------|
| Quantidade itens tipo  | <3  | 3-4  | 5-7  | ≥8  | -     | -           | -    |
| Subtotal              | -   | -    | -    | -   | ≤500  | >500 e ≤1k  | >1k  |
| Peso total            | ≤5  | ≤10  | ≤50  | >50 | -     | -           | -    |

### Resultados

| Regra Aplicada          | R6  | R7  | R8  | R9  | R10 | R11 | R12 |
|------------------------|-----|-----|-----|-----|-----|-----|-----|
| Desconto tipo (%)      | 0   | 5   | 10  | 15  | -   | -   | -   |
| Taxa frete (R$/kg)     | 0   | 2   | 4   | 7   | -   | -   | -   |
| Taxa base frete (R$12) | N   | S   | S   | S   | -   | -   | -   |
| Desconto subtotal (%)  | -   | -   | -   | -   | 0   | 10  | 20  |

---

### Regras de Peso

| Condição de Peso     | R13   | R14   | R15   |
|---------------------|-------|-------|-------|
| Peso físico vs cúbico | PF>PC | PF=PC | PF<PC |

**Resultado**

| Decisão          | R13 | R14 | R15 |
|-----------------|-----|-----|-----|
| Usar peso físico | X   | X   | -   |
| Usar peso cúbico | -   | X   | X   |

---

### Regras de Região

| Condição | R16 | R17 | R18 | R19 | R20 |
|----------|-----|-----|-----|-----|-----|
| Região   | SE  | S   | NE  | CO  | N   |

**Resultado**

| Decisão            | R16  | R17  | R18  | R19  | R20  |
|-------------------|------|------|------|------|------|
| Multiplicador     | 1.00 | 1.05 | 1.10 | 1.20 | 1.30 |

---

### Regras de Benefício e Manuseio Especial
Regras independentes das faixas de peso e subtotal.

#### Condições

| Condição        | R21 | R22 | R23 | R24 | R25 |
|----------------|-----|-----|-----|-----|-----|
| Nível Cliente  | BR  | PR  | OU  | -   | -   |
| Item frágil    | -   | -   | -   | N   | S   |

#### Resultados

| Aplicação      | R21 | R22 | R23 | R24 | R25 |
|----------------|-----|-----|-----|-----|-----|
| Desconto frete | 0   | 50  | 100 | -   | -   |
| Taxa especial  | -   | -   | -   | N   | S   |

#### Legenda
- S: Sim, N: Não, -: Indiferente
- BR: Bronze, PR: Prata, OU: Ouro
- k: 1000 (exemplo: >1k = >1000)
- PF: Peso Físico, PC: Peso Cúbico
- SE: Sudeste, S: Sul, NE: Nordeste, CO: Centro-Oeste, N: Norte

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
 |------→ [Exceção] (se nulo)
 ↓
[3] Calcular subtotal
 |
 ↓
[4] Verificar quantidade de itens do mesmo tipo
 |---------------------|-------------------|
 ↓                     ↓                   ↓
[5] Qtd >= 8          [6] Qtd >= 5        [7] Qtd >= 3
 | (15% desconto)      | (10% desconto)    | (5% desconto)
 |                     |                   |
 |---------------------|-------------------|
 ↓
[8] Verificar valor subtotal
 |---------------------|
 ↓                     ↓
[9] Valor > 1000      [10] Valor > 500
 | (20% desconto)      | (10% desconto)
 |---------------------|
 ↓
[11] Calcular subtotal com descontos
 |
 ↓
[12] Verificar peso total
 |---------------------|-------------------|
 ↓                     ↓                   ↓
[13] Peso > 50        [14] Peso > 10      [15] Peso > 5
 | (R$7/kg)           | (R$4/kg)          | (R$2/kg)
 |---------------------|-------------------|
 ↓
[16] Verificar item frágil
 |---------------→ [17] Adicionar taxa especial (se frágil)
 ↓
[18] Verificar região
 |---------------------|-------------------|-------------------|
 ↓                     ↓                   ↓                   ↓
[19] SE (1.0x)        [20] S (1.05x)     [21] NE (1.1x)     [22] CO (1.2x)
 |                     |                   |                   |
 |---------------------|-------------------|-------------------|
 ↓
[23] Verificar tipo cliente
 |---------------------|-------------------|
 ↓                     ↓                   ↓
[24] OURO             [25] PRATA          [26] BRONZE
 | (100% desc)         | (50% desc)        | (0% desc)
 |---------------------|-------------------|
 ↓
[27] Calcular frete final
 |
 ↓
[28] Somar subtotal com frete
 |
 ↓
[29] Retornar total
```

### Cálculo da Complexidade Ciclomática (V(G))

A complexidade ciclomática pode ser calculada usando a fórmula:
V(G) = E - N + 2P, onde:
- E = número de arestas
- N = número de nós
- P = número de componentes conectados (normalmente 1 para um único método)

No grafo acima:
- Número de nós (N) = 29 (incluindo todos os pontos de decisão e caminhos alternativos)
- Número de arestas (E) = 38 (incluindo todas as conexões entre nós)
- Número de componentes conectados (P) = 1

Portanto:
V(G) = E - N + 2P
V(G) = 38 - 29 + 2(1)
V(G) = 11

Este valor mais alto de complexidade ciclomática (11) reflete melhor a realidade do código, considerando todos os pontos de decisão:
1. Validação de entrada (cliente/carrinho nulo)
2. Verificações de quantidade de itens (3 pontos de decisão: >=8, >=5, >=3)
3. Verificações de valor subtotal (2 pontos de decisão: >1000, >500)
4. Verificações de peso (3 pontos de decisão: >50, >10, >5)
5. Verificação de item frágil
6. Verificação de região (4 possíveis caminhos)
7. Verificação de tipo de cliente (3 tipos diferentes)

### Número Mínimo de Casos de Teste Independentes

Com base na complexidade ciclomática V(G) = 11, precisamos de no mínimo 11 casos de teste independentes para cobrir todos os caminhos básicos do código. Estes casos devem cobrir:

1. Caminho de erro: cliente ou carrinho inválido (nulo)
2. Quantidade de itens >= 8 (desconto 15%)
3. Quantidade de itens >= 5 e < 8 (desconto 10%)
4. Quantidade de itens >= 3 e < 5 (desconto 5%)
5. Subtotal > 1000 (desconto 20%)
6. Subtotal > 500 e <= 1000 (desconto 10%)
7. Peso > 50 (taxa R$7/kg)
8. Peso > 10 e <= 50 (taxa R$4/kg)
9. Peso > 5 e <= 10 (taxa R$2/kg)
10. Item frágil (taxa especial)
11. Combinações de tipo de cliente e região diferentes

Na prática, para garantir uma cobertura adequada, precisamos implementar ainda mais casos de teste para cobrir todas as combinações possíveis de condições, conforme demonstrado na análise MC/DC anterior. Isso é especialmente importante devido às múltiplas condições independentes que podem ocorrer simultaneamente.

