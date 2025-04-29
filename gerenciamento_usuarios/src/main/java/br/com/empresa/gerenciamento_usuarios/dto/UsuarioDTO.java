package br.com.empresa.gerenciamento_usuarios.dto;

import java.time.LocalDate;
import java.util.Set;

import br.com.empresa.gerenciamento_usuarios.enums.Role;
import br.com.empresa.gerenciamento_usuarios.enums.Sexo;
import br.com.empresa.gerenciamento_usuarios.enums.TipoUsuario;

public class UsuarioDTO {

    private Long id;
    private String nome;
    private String nomeUsuario;
    private String foto;
    private String cep;
    private String endereco;
    private String email;
    private LocalDate dataNascimento;
    private Sexo sexo;
    private TipoUsuario tipo;
    private String documento;
    private Set<Role> roles;

    public UsuarioDTO() {
    	
    }
    
    public UsuarioDTO(Long id, String nome, String nomeUsuario, String foto, String cep, String endereco, String email,
			LocalDate dataNascimento, Sexo sexo, TipoUsuario tipo, String documento, Set<Role> roles) {
		super();
		this.id = id;
		this.nome = nome;
		this.nomeUsuario = nomeUsuario;
		this.foto = foto;
		this.cep = cep;
		this.endereco = endereco;
		this.email = email;
		this.dataNascimento = dataNascimento;
		this.sexo = sexo;
		this.tipo = tipo;
		this.documento = documento;
		this.roles = roles;
	}

	// Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
