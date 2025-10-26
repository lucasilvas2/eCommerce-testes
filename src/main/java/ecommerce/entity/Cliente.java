package ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cliente
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private Regiao regiao;

	@Enumerated(EnumType.STRING) // Armazenar o enum como String no banco
	private TipoCliente tipo;

	public Cliente()
	{
	}

	public Cliente(Long id, String nome, Regiao regiao, TipoCliente tipo)
	{
		this.setId(id);
        this.setNome(nome);
        this.setRegiao(regiao);
        this.setTipo(tipo);
	}

	// Getters e Setters
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
        if (id == null || id < 0) {
            throw new IllegalArgumentException("ID não pode ser negativo.");
        }
		this.id = id;
	}

	public String getNome()
	{
		return nome;
	}

	public void setNome(String nome)
	{
        if(nome == null || nome.trim().isEmpty()){
            throw new IllegalArgumentException("O nome não pode ser nulo ou vazio.");
        }
		this.nome = nome;
	}

	public Regiao getRegiao()
	{
		return regiao;
	}

	public void setRegiao(Regiao regiao)
	{
        if(regiao == null){
            throw new IllegalArgumentException("A região não pode ser nula.");
        }

        // validar se o valor está entre os valores definidos no enum Regiao
        boolean valido = false;
        for (Regiao r : Regiao.values()) {
            if (r == regiao) {
                valido = true;
                break;
            }
        }
        if (!valido) {
            throw new IllegalArgumentException("Região inválida.");
        }
		this.regiao = regiao;
	}

	public TipoCliente getTipo()
	{
		return tipo;
	}

	public void setTipo(TipoCliente tipo)
	{
        if(tipo == null){
            throw new IllegalArgumentException("O tipo de cliente não pode ser nulo.");
        }
		this.tipo = tipo;
	}
}
