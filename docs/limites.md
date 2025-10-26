| ID do Teste | Variável Focada | Valor Testado | Critério/Limite | Resultado Esperado Chave | Partição |
|-------------|-----------------|---------------|-----------------|--------------------------|----------|
| TC-VL1      | Qtd. Itens/Tipo | 2             | Limite 3 (L-1)  | 0% Desconto              | P1       |
| TC-VL2      | Qtd. Itens/Tipo | 3             | Limite 3 (L)    | 5% Desconto              | P2       |
| TC-VL3      | Qtd. Itens/Tipo | 4             | Limite 4 (L)    | 5% Desconto              | P2       |
| TC-VL4      | Qtd. Itens/Tipo | 5             | Limite 5 (L)    | 10% Desconto             | P3       |
| TC-VL5      | Qtd. Itens/Tipo | 7             | Limite 7 (L)    | 10% Desconto             | P3       |
| TC-VL6      | Qtd. Itens/Tipo | 8             | Limite 8 (L)    | 15% Desconto             | P4       |
| TC-VL7      | Qtd. Itens/Tipo | 9             | Limite 8 (L+1)  | 15% Desconto             | P4       |

| ID do Teste | Variável Focada | Valor Testado | Critério/Limite | Resultado Esperado Chave | Partição |
|-------------|-----------------|---------------|-----------------|--------------------------|----------|
| TC-VL8      | Subtotal        | R$ 499,99     | R$ 500,00 (L-1) | 0% Desconto              | P5       |
| TC-VL9      | Subtotal        | R$ 500,00     | R$ 500,00 (L)   | 0% Desconto              | P5       |
| TC-VL10     | Subtotal        | R$ 500,01     | R$ 500,00 (L+1) | 10% Desconto             | P6       |

| ID do Teste | Variável Focada | Valor Testado | Critério/Limite       | Resultado Esperado Chave    | Partição |
|-------------|-----------------|---------------|-----------------------|-----------------------------|----------|
| TC-VL11     | Peso Total      | 4,99 kg       | Limite 5,00 kg (L-1)  | Frete Isento (R$ 0,00)      | P7, P11  |
| TC-VL12     | Peso Total      | 5,00 kg       | Limite 5,00 kg (L)    | Frete Isento (R$ 0,00)      | P7, P11  |
| TC-VL13     | Peso Total      | 5,01 kg       | Limite 5,00 kg (L+1)  | Frete R2,00/kg+R 12,00 taxa | P8, P12  |
| TC-VL14     | Peso Total      | 9,99 kg       | Limite 10,00 kg (L-1) | Frete R2,00/kg+R 12,00 taxa | P8, P12  |
| TC-VL15     | Peso Total      | 10,00 kg      | Limite 10,00 kg (L)   | Frete R2,00/kg+R 12,00 taxa | P8, P12  |
| TC-VL16     | Peso Total      | 10,01 kg      | Limite 10,00 kg (L+1) | Frete R4,00/kg+R 12,00 taxa | P9, P12  |
| TC-VL17     | Peso Total      | 49,99 kg      | Limite 50,00 kg (L-1) | Frete R4,00/kg+R 12,00 taxa | P9, P12  |
| TC-VL18     | Peso Total      | 50,00 kg      | Limite 50,00 kg (L)   | Frete R4,00/kg+R 12,00 taxa | P9, P12  |
| TC-VL19     | Peso Total      | 50,01 kg      | Limite 50,00 kg (L+1) | Frete R7,00/kg+R 12,00 taxa | P10, P12 |

| ID do Teste | Variável Focada | Valor Testado | Critério/Limite              | Resultado Esperado Chave | Partição |
|-------------|-----------------|---------------|------------------------------|--------------------------|----------|
| TC-VL20     | Quantidade      | -1            | Limite 0 (L-1 - Inválido)    | Lançamento de Exceção    | P26      |
| TC-VL21     | Quantidade      | 0             | Limite 0 (L - Inválido)      | Lançamento de Exceção    | P26      |
| TC-VL22     | Quantidade      | 1             | Limite 0 (L+1 - Válido)      | Cálculo normal           | P25      |
| TC-VL23     | Preço           | -0,01         | Limite 0,00 (L-1 - Inválido) | Lançamento de Exceção    | P24      |
| TC-VL24     | Preço           | 0,00          | Limite 0,00 (L - Válido)     | Cálculo normal           | P23      |
| TC-VL25     | Preço           | 0,01          | Limite 0,00 (L+1 - Válido)   | Cálculo normal           | P23      |

