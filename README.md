# Documentação do Projeto

Este documento contém instruções para execução do projeto, testes automatizados e análise de cobertura de código.


# Autores
<ul>
   <li>Lucas Silva de Oliveira</li>
   <li>Vinicios David Martins Bezerra</li>
</ul>


# Localização dos arquivos 
Os arquivos de teste estão localizados no diretório:
```src/test/java/ecommerce/service/u3```

E a classe sob teste está em:
```src/main/java/ecommerce/service/CompraSimplificadaService.java```

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

## Análise de mutação 

O projeto utiliza o Pitest para análise de mutação. Para executar a análise:

1. Execute:
   ```mvn org.pitest:pitest-maven:mutationCoverage```

2. Localize o relatório em:
   ```target/pit-reports/index.html```
   
3. O relatório apresenta:
   - Line Coverage: porcentagem de linhas de código executadas pelos testes.
   - Mutation Coverage: porcentagem de mutantes gerados que os testes conseguiram matar.
   - Test Strength: quão eficazes são os testes sobre o código que eles realmente exercitam (mutantes cobertos que foram mortos).
   - Taxa de detecção de mutantes (mutantes mortos / total de mutantes)

4. Como verificar que não restaram mutantes sobreviventes:
   - Abra o relatório HTML gerado
   - Verifique se o item Mutation Coverage indica todos os mutantes como mortos, caso afirmativo, a barra ficará totalmente verde, caso contrário ficará vermelho para indicar mutantes sobreviventes.

5. Estrategia para eliminar mutantes sobreviventes

Os mutantes sobreviventes foram analisados no relatório e, para cada um, foi identificado o teste que precisava ser criado/ajustado para matá‑lo.