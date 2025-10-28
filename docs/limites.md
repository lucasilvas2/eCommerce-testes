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
