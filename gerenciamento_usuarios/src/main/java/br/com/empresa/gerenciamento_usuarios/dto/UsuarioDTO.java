package br.com.empresa.gerenciamento_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

import br.com.empresa.gerenciamento_usuarios.enums.Role;
import br.com.empresa.gerenciamento_usuarios.enums.Sexo;
import br.com.empresa.gerenciamento_usuarios.enums.TipoUsuario;

public class UsuarioDTO {

	@NotNull(message = "O nome não pode ser nulo.")
	@Size(min = 30, message = "O nome deve ter pelo menos 30 caracteres.")
	private String nome;

	@NotNull(message = "O nome de usuário não pode ser nulo.")
	private String nomeUsuario;

	private String foto;

	//@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=]{8,}$", message = "A senha deve conter pelo menos 8 caracteres, uma letra maiúscula, uma letra minúscula, um número e pode ter caracteres especiais.")
	private String senha;

	@NotNull(message = "O email não pode ser nulo.")
	@Email(message = "O email deve estar em um formato válido.")
	private String email;

	@NotNull(message = "A data de nascimento não pode ser nula.")
	private LocalDate dataNascimento;

	@NotNull(message = "O sexo não pode ser nulo.")
	private Sexo sexo;

	@NotNull(message = "O tipo de usuário não pode ser nulo.")
	private TipoUsuario tipo;

	@NotNull(message = "O documento não pode ser nulo.")
	@Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CPF ou CNPJ no formato inválido.")
	private String documento;

	private Set<Role> roles;

	public UsuarioDTO() {
	}

	public UsuarioDTO(String nome, String nomeUsuario, String foto, String email, LocalDate dataNascimento, Sexo sexo,
			TipoUsuario tipo, String documento, String senha, Set<Role> roles) {
		this.nome = nome;
		this.nomeUsuario = nomeUsuario;
		this.foto = foto;
		this.email = email;
		this.senha = senha;
		this.dataNascimento = dataNascimento;
		this.sexo = sexo;
		this.tipo = tipo;
		this.documento = documento;
		this.roles = roles;
	}

	// Getters e Setters
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

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
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