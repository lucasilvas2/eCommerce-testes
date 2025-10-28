# Tabela de Decisão

## Condições (nível de item)
- Sinal do preço do item
    - preço >= 0
    - preço < 0 (inválido)
- Sinal da quantidade do item
    - quantidade > 0
    - quantidade <= 0 (inválido)
- Peso físico vs peso cúbico (por item)
    - peso_físico > peso_cúbico
    - peso_físico = peso_cúbico
    - peso_físico < peso_cúbico
- Fragilidade do item (por item / agregado)
    - sem itens frágeis
    - ≥ 1 item frágil (usar TAXA_ITEM_FRAGIL × qtd)

## Condições (nível de carrinho / subtotal)
- Faixas de subtotal (antes de descontos)
    - subtotal ≤ 500
    - 500 < subtotal ≤ 1000
    - subtotal > 1000
- Quantidade de itens por tipo (agregação por TipoProduto)
    - < 3
    - 3..4
    - 5..7
    - ≥ 8

## Condições de peso / frete
- Faixas de peso total (peso tributável do carrinho)
    - pesoTotal ≤ 5.00 kg (Faixa A — isento)
    - 5.00 < pesoTotal ≤ 10.00 kg (Faixa B)
    - 10.00 < pesoTotal ≤ 50.00 kg (Faixa C)
    - pesoTotal > 50.00 kg (Faixa D)
- Aplicar taxa mínima de frete
    - isento (Faixa A) → sem `TAXA_FIXA_FRETE`
    - não isento (Faixa B,C,D) → adicionar `TAXA_FIXA_FRETE` (R$12,00)
- Multiplicador por região (CEP / região)
    - SUDESTE → 1.00
    - SUL → 1.05
    - NORDESTE → 1.10
    - CENTRO_OESTE → 1.20
    - NORTE → 1.30
    - regiao = null (inválido)

## Condições do cliente / benefícios
- Tipo de cliente (desconto sobre frete)
    - OURO → 100% desconto no frete (frete final = R$0,00)
    - PRATA → 50% desconto no frete
    - BRONZE → 0% desconto no frete

## Validações de fluxo / integrações
- Presença do cliente
    - cliente != null
    - cliente == null (inválido)
- Disponibilidade de produtos / estoque
    - disponível
    - não disponível (checkout bloqueado)

## Predicados derivados / compostos (usados nas regras)
- Aplicar desconto por tipo de item (por TipoProduto)
    - sem desconto (qtd < 3)
    - 5% (3–4)
    - 10% (5–7)
    - 15% (>= 8)
- Aplicar desconto por subtotal
    - nenhum (subtotal ≤ 500)
    - 10% (500 < subtotal ≤ 1000)
    - 20% (subtotal > 1000)
- Frete final após desconto do cliente (derivado)
    - frete_calculado × (1 - desconto_cliente)
    - para OURO, frete final forçado a 0.00 (regra de negócio)

## Ações (resultados / operações)
1. Calcular Subtotal = soma(preço_unitário × quantidade)
2. Aplicar desconto por múltiplos do mesmo tipo (por grupo de TipoProduto)
3. Aplicar desconto por valor de subtotal (após desconto por tipo)
4. Calcular frete:
    - soma(peso_tributável × taxa_por_kg) + taxa mínima (se aplicável) + taxa fragil
    - multiplicar pelo multiplicador de região
5. Aplicar desconto de frete por tipo de cliente
6. Somar subtotal com desconto + frete com desconto
7. Arredondar total final para 2 casas decimais (half-up)

## Observações para montagem das regras
- Regras = combinações relevantes das condições acima que disparam as ações.
- Calcular peso tributável por item = max(peso_físico, peso_cúbico); peso_cúbico = (C × L × A) / 6000.
- Descontos por tipo de item são aplicados por grupo e **antes** do desconto por subtotal.
- Mesmo para cliente OURO, o frete é calculado e somente depois zerado.

## Número de Combinações Possíveis

### Condições independentes e alternativas
- Sinal do preço: 2 opções  
- Sinal da quantidade: 2 opções  
- Peso físico vs peso cúbico: 3 opções  
- Fragilidade do item: 2 opções  
- Faixas de subtotal: 3 opções  
- Quantidade por tipo: 4 opções  
- Faixas de peso total: 4 opções  
- Região: 6 opções  
- Tipo de cliente: 3 opções  
- Presença do cliente: 2 opções  
- Disponibilidade: 2 opções

### Espaço Total (incluindo combinações inválidas)
Multiplicação de todas as alternativas:  
`2 × 2 × 3 × 2 × 3 × 4 × 4 × 6 × 3 × 2 × 2 = 82.944 combinações`

### Combinações Válidas
Excluindo opções inválidas:
- preço ≥ 0 (1 opção)  
- quantidade > 0 (1 opção)  
- região ≠ null (5 opções)  
- cliente presente (1 opção)  
- produto disponível (1 opção)

Multiplicação considerando exclusões:  
`1 × 1 × 3 × 2 × 3 × 4 × 4 × 5 × 3 × 1 × 1 = 4.320 combinações`

### Resultado Final
- Total de combinações (com inválidas): `82.944`  
- Total de combinações válidas: `4.320`

# Tabela de Decisão Simplificada

## Regras de Validação (Primárias)

| Condição             | R1  | R2  | R3  | R4  | R5  |
|----------------------|:---:|:---:|:---:|:---:|:---:|
| Preço < 0            | `S` | `N` | `N` | `N` | `N` |
| Quantidade ≤ 0       | `-` | `S` | `N` | `N` | `N` |
| Cliente nulo         | `-` | `-` | `S` | `N` | `N` |
| Região nula          | `-` | `-` | `-` | `S` | `N` |
| Produto indisponível | `-` | `-` | `-` | `-` | `S` |

**Ação**

| Ação              | R1  | R2  | R3  | R4  | R5  |
|-------------------|:---:|:---:|:---:|:---:|:---:|
| Lançar exceção    | `X` | `X` | `X` | `X` | `X` |
| Continuar cálculo | `-` | `-` | `-` | `-` | `-` |

---

## Regras de Negócio (Principais)


## Regras de Negócio 

| Condição / Regra     |  R6  |  R7   |  R8   |  R9   |  R10   |     R11      |  R12  |
|----------------------|:----:|:-----:|:-----:|:-----:|:------:|:------------:|:-----:|
| Qtd itens mesmo tipo | `<3` | `3-4` | `5-7` | `≥8`  |  `-`   |     `-`      |  `-`  |
| Subtotal             | `-`  |  `-`  |  `-`  |  `-`  | `≤500` | `>500 e ≤1k` | `>1k` |
| Peso total (Faixa)   | `≤5` | `≤10` | `≤50` | `>50` |  `-`   |     `-`      |  `-`  |

### Ações / Resultados

| Ação / Regra             | R6  | R7  |  R8  |  R9  | R10 | R11  | R12  |
|--------------------------|:---:|:---:|:----:|:----:|:---:|:----:|:----:|
| Desconto tipo (%)        | `0` | `5` | `10` | `15` | `-` | `-`  | `-`  |
| Taxa frete (R\$/kg)      | `0` | `2` | `4`  | `7`  | `-` | `-`  | `-`  |
| Taxa fixa frete (R\$\12) | `N` | `S` | `S`  | `S`  | `-` | `-`  | `-`  |
| Desconto subtotal (%)    | `-` | `-` | `-`  | `-`  | `0` | `10` | `20` |

---

## Regras de Peso (Derivadas)

| Condição              |   R13   |   R14   |   R15   |
|-----------------------|:-------:|:-------:|:-------:|
| Peso físico vs cúbico | `PF>PC` | `PF=PC` | `PF<PC` |

**Ação**

| Ação             | R13 | R14 | R15 |
|------------------|:---:|:---:|:---:|
| Usar peso físico | `X` | `X` | `-` |
| Usar peso cúbico | `-` | `X` | `X` |

---

## Regras de Região (Multiplicador)

| Condição | R16  | R17 | R18  | R19  | R20 |
|----------|:----:|:---:|:----:|:----:|:---:|
| Região   | `SE` | `S` | `NE` | `CO` | `N` |

**Ação**

| Ação                |  R16   |  R17   |  R18   |  R19   |  R20   |
|---------------------|:------:|:------:|:------:|:------:|:------:|
| Multiplicador frete | `1.00` | `1.05` | `1.10` | `1.20` | `1.30` |

---

## Regras de Benefício de Cliente e Fragilidade
Estas regras são totalmente ortogonais às faixas de peso e subtotal, devendo ser mapeadas separadamente.

### Condições

|   Condição   | R21 | R22 | R23 | R24 | R25 |
|:------------:|:---:|:---:|:---:|:---:|:---:|
| Cliente tipo | BR  | PR  | OU  |  -  |  -  |
| Item frágil  |  -  |  -  |  -  |  N  |  S  |

### Ações / Resultados

|        Ação        | R21 | R22 | R23 | R24 | R25 |
|:------------------:|:---:|:---:|:---:|:---:|:---:|
| Desconto frete (%) |  0  | 50  | 100 |  -  |  -  |
|  Taxa item frágil  |  -  |  -  |  -  |  N  |  S  |


### Observações
- `S` = Sim, `N` = Não, `-` = Indiferente  
- `BR` = Bronze, `PR` = Prata, `OU` = Ouro  
- `k` = 1000 (ex: `>1k` = `>1000`)  
- `PF` = Peso Físico, `PC` = Peso Cúbico  
- `SE` = Sudeste, `S` = Sul, `NE` = Nordeste, `CO` = Centro-Oeste, `N` = Norte