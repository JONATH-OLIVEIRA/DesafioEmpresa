package br.com.empresa.gerenciamento_usuarios.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.empresa.gerenciamento_usuarios.enums.Role;
import br.com.empresa.gerenciamento_usuarios.enums.Sexo;
import br.com.empresa.gerenciamento_usuarios.enums.TipoUsuario;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 30, message = "O nome deve ter pelo menos 30 caracteres.")
	private String nome;

	@NotNull
	@Column(unique = true) // Garante unicidade no banco de dados
	private String nomeUsuario;

	@NotNull(message = "A senha não pode ser nula.")
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$", message = "A senha deve conter pelo menos 8 caracteres, uma letra maiúscula, uma letra minúscula e um número.")
	private String senha;

	private String foto; // URL ou caminho da foto

	@NotNull
	@Email(message = "O email deve estar em um formato válido.")
	private String email;

	@NotNull
	@Past(message = "A data de nascimento deve estar no passado.")
	private LocalDate dataNascimento;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Sexo sexo; // Enumeração para o campo "Sexo"

	@NotNull
	@Enumerated(EnumType.STRING)
	private TipoUsuario tipo; // Enumeração para Pessoa Física ou Jurídica

	@NotNull
	@Pattern(regexp = "(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})|(\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})", message = "CPF ou CNPJ no formato inválido.")
	private String documento;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"))
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Set<Role> roles; // Permissões de acesso do usuário

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "endereco_id") // Define a chave estrangeira na tabela de usuários
	private Endereco endereco; // Relacionamento com a classe Endereco

	public Usuario() {

	}

	public Usuario(Long id, String nome, String nomeUsuario, String senha, String foto, String email,
			LocalDate dataNascimento, Sexo sexo, TipoUsuario tipo, String documento, Set<Role> roles,
			Endereco endereco) {
		super();
		this.id = id;
		this.nome = nome;
		this.nomeUsuario = nomeUsuario;
		this.senha = senha;
		this.foto = foto;
		this.email = email;
		this.dataNascimento = dataNascimento;
		this.sexo = sexo;
		this.tipo = tipo;
		this.documento = documento;
		this.roles = roles;
		this.endereco = endereco;
	}

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

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
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

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public void criptografarSenha() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		this.senha = encoder.encode(this.senha);
	}

	public boolean validarSenha(String senha) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.matches(senha, this.senha);
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nome=" + nome + ", nomeUsuario=" + nomeUsuario + ", senha=" + senha + ", foto="
				+ foto + ", email=" + email + ", dataNascimento=" + dataNascimento + ", sexo=" + sexo + ", tipo=" + tipo
				+ ", documento=" + documento + ", roles=" + roles + ", endereco=" + endereco + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		return Objects.equals(id, other.id);
	}

}