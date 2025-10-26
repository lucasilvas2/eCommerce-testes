package ecommerce.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    /**
     * Preço unitário em reais (R$).
     */
    private BigDecimal preco;

    /**
     * Peso físico em quilogramas (kg).
     */
    private BigDecimal pesoFisico;

    /**
     * Dimensões em centímetros (cm).
     */
    private BigDecimal comprimento;
    private BigDecimal largura;
    private BigDecimal altura;

    /**
     * Indica se o produto é frágil.
     */
    private Boolean fragil;

    @Enumerated(EnumType.STRING)
    private TipoProduto tipo;
    private static final BigDecimal DIVISOR = new BigDecimal("6000");
    private static final int DIVIDE_SCALE = 6;


    public Produto() {
    }

    public Produto(Long id, String nome, String descricao, BigDecimal preco, BigDecimal pesoFisico,
                   BigDecimal comprimento, BigDecimal largura, BigDecimal altura, Boolean fragil, TipoProduto tipo) {
        this.setId(id);
        this.setNome(nome);
        this.setDescricao(descricao);
        this.setPreco(preco);
        this.setPesoFisico(pesoFisico);
        this.setComprimento(comprimento);
        this.setLargura(largura);
        this.setAltura(altura);
        this.setFragil(fragil);
        this.setTipo(tipo);
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if(id == null || id < 0){
            throw new IllegalArgumentException("O ID do produto deve ser um valor positivo.");
        }
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if(nome == null || nome.trim().isEmpty()){
            throw new IllegalArgumentException("O nome do produto não pode ser nulo ou vazio.");
        }
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        if(preco == null || preco.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }
        this.preco = preco;
    }

    public BigDecimal getPesoFisico() {
        return pesoFisico;
    }

    public void setPesoFisico(BigDecimal pesoFisico) {
        if(pesoFisico == null || pesoFisico.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("O peso físico não pode ser negativo.");
        }
        this.pesoFisico = pesoFisico;
    }

    public BigDecimal getComprimento() {
        return comprimento;
    }

    public void setComprimento(BigDecimal comprimento) {
        if(comprimento == null || comprimento.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("O comprimento não pode ser negativo.");
        }
        this.comprimento = comprimento;
    }

    public BigDecimal getLargura() {
        return largura;
    }

    public void setLargura(BigDecimal largura) {
        if(largura == null || largura.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("A largura não pode ser negativa.");
        }
        this.largura = largura;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        if(altura == null || altura.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("A altura não pode ser negativa.");
        }
        this.altura = altura;
    }

    public Boolean isFragil() {
        return fragil;
    }

    public void setFragil(Boolean fragil) {
        this.fragil = fragil;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProduto tipo) {
        if(tipo == null){
            throw new IllegalArgumentException("O tipo do produto não pode ser nulo.");
        }
        this.tipo = tipo;
    }

    protected BigDecimal pesoCubico(){
        if (this.comprimento == null || this.largura == null || this.altura == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal volume = this.comprimento.multiply(this.largura).multiply(this.altura);
        return volume.divide(DIVISOR, DIVIDE_SCALE, RoundingMode.HALF_UP);
    }

    public BigDecimal pesoTributavel() {
        BigDecimal pesoCubico = this.pesoCubico();
        if (this.pesoFisico == null) {
            return pesoCubico;
        }
        return this.pesoFisico.compareTo(pesoCubico) > 0 ? this.pesoFisico : pesoCubico;
    }
}