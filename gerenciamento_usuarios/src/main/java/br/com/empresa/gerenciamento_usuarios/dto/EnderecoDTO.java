package br.com.empresa.gerenciamento_usuarios.dto;

public class EnderecoDTO {

	private Long id;
	private String cep;
	private String logradouro;
	private String complemento;
	private Integer numeroCasa;
	private String bairro;
	private String localidade; // Cidade
	private String uf; // Estado

	public EnderecoDTO() {
	}

	public EnderecoDTO(Long id, String cep, String logradouro, String complemento, Integer numeroCasa, String bairro,
			String localidade, String uf) {
		this.id = id;
		this.cep = cep;
		this.logradouro = logradouro;
		this.complemento = complemento;
		this.numeroCasa = numeroCasa;
		this.bairro = bairro;
		this.localidade = localidade;
		this.uf = uf;
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public Integer getNumeroCasa() {
		return numeroCasa;
	}

	public void setNumeroCasa(Integer numeroCasa) {
		this.numeroCasa = numeroCasa;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
}