## Partições
1. Desconto por Múltiplos Itens de Mesmo Tipo

| ID | Partição                                  | Resultado Esperado |
|----|-------------------------------------------|--------------------|
| P1 | Quantidade de itens do mesmo tipo < 3     | Sem Desconto (0%)  |
| P2 | Quantidade de itens do mesmo tipo ≥3 e ≤4 | 5% de Desconto     |
| P3 | Quantidade de itens do mesmo tipo ≥5 e ≤7 | 10% de Desconto    |
| P4 | Quantidade de itens do mesmo tipo ≥8      | 15% de Desconto    |

2. Desconto sobre subtotal

| ID | Partição (Subtotal)  | Resultado Esperado (Desconto Aplicado) |
|----|----------------------|----------------------------------------|
| P5 | Subtotal ≤ R$500,00  | Sem Desconto (0%)                      |
| P6 | Subtotal > R$500,00  | 10% de Desconto                        |
| P7 | Subtotal > R$1000,00 | 20% de Desconto                        |

3. Valor frete

| ID  | Partição (Faixa de Peso Total em kg) | Resultado Esperado (Valor por kg) |
|-----|--------------------------------------|-----------------------------------|
| P8  | 0,00≤peso≤5,00 (Faixa A)             | Isento (R$ 0,00/kg)               |
| P9  | 5,00<peso≤10,00 (Faixa B)            | R$ 2,00/kg                        |
| P10 | 10,00<peso≤50,00 (Faixa C)           | R$ 4,00/kg                        |
| P11 | peso>50,00 (Faixa D)                 | R$ 7,00/kg                        |

4. Taxa mínima frete

| ID  | Partição (Frete Base)                             | Resultado Esperado (Taxa Mínima)  |
|-----|---------------------------------------------------|-----------------------------------|
| P12 | Frete base isento (Peso na Faixa A)               | Sem taxa mínima de R$ 12,00       |
| P13 | Frete base não isento (Peso nas Faixas B, C ou D) | Adicionar taxa mínima de R$ 12,00 |

5. Taxa de manuseio (Fragilidade)

| ID  | Partição (Status de Fragilidade dos Itens)             | Resultado Esperado (Taxa de Manuseio)             |
|-----|--------------------------------------------------------|---------------------------------------------------|
| P14 | Carrinho contém pelo menos um item marcado como frágil | Aplicação de R$ 5,00 × quantidade por item frágil |
| P15 | Carrinho não contém itens marcados como frágeis        | Sem taxa de manuseio especial                     |

6. Frete

| ID  | Partição (Região de Entrega) | Resultado Esperado (Multiplicador) |
|-----|------------------------------|------------------------------------|
| P16 | Região Sudeste               | 1,00                               |
| P17 | Região Sul                   | 1,05                               |
| P18 | Região Nordeste              | 1,10                               |
| P19 | Região Centro-Oeste          | 1,20                               |
| P20 | Região Norte                 | 1,30                               |

7. Tipo de Peso Considerado

| ID  | Partição (Tipo de Peso)   | Resultado Esperado                    |
|-----|---------------------------|---------------------------------------|
| P21 | Peso Físico > Peso Cúbico | Usar Peso Físico como peso tributável |
| P22 | Peso Cúbico > Peso Físico | Usar Peso Cúbico como peso tributável |
| P23 | Peso Físico = Peso Cúbico | Usar qualquer um (são iguais)         |

8. Beneficio

| ID  | Partição (Nível de Fidelidade) | Resultado Esperado (Desconto no Frete)   |
|-----|--------------------------------|------------------------------------------|
| P24 | Cliente Ouro                   | 100% de desconto (Frete final = R$ 0,00) |
| P25 | Cliente Prata                  | 50% de desconto sobre o frete calculado  |
| P26 | Cliente Bronze                 | Paga o frete integral (0% de desconto)   |
9. Valores de entrada

| ID  | Partição (Tipo de Entrada) | Domínio                                              |
|-----|----------------------------|------------------------------------------------------|
| P24 | Preços Válidos             | Preço unitário ≥0 (implícito)                        |
| P28 | Preços Inválidos           | Preços negativos                                     |
| P29 | Quantidade Válida          | Quantidade >0 (implícito)                            |
| P30 | Quantidade Inválida        | Quantidade ≤0                                        |
| P31 | Cliente Válido             | Cliente identificado (Não nulo/presente) (implícito) |
| P32 | Cliente Inválido           | Cliente nulo (ou inexistente)                        |

## Casos de Teste (Alinhado com ParticoesEquivalenciaTest.java)

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
| TC-P28      | P32         | Cliente nulo                      | cliente=null                                                                                                                  | NullPointerException         |