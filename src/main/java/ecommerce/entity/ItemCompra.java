package ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ItemCompra
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne // Vários itens podem se referir ao mesmo produto
	@JoinColumn(name = "produto_id")
	private Produto produto;

	private Long quantidade;

	public ItemCompra()
	{
	}

	public ItemCompra(Long id, Produto produto, Long quantidade)
	{
		this.setId(id);
        this.setProduto(produto);
        this.setQuantidade(quantidade);
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

	public Produto getProduto()
	{
		return produto;
	}

	public void setProduto(Produto produto)
	{
        if (produto == null || produto.getId() == null) {
            throw new IllegalArgumentException("Produto inválido.");
        }
		this.produto = produto;
	}

	public Long getQuantidade()
	{
		return quantidade;
	}

	public void setQuantidade(Long quantidade)
	{
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade do item deve ser maior que zero.");
        }
		this.quantidade = quantidade;
	}
}
