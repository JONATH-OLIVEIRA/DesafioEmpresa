package br.com.empresa.gerenciamento_usuarios.dto;

public class UsuarioComEnderecoDTO {
	private UsuarioDTO usuario;
	private EnderecoDTO endereco;

	public UsuarioComEnderecoDTO() {
	}

	public UsuarioComEnderecoDTO(UsuarioDTO usuario, EnderecoDTO endereco) {
		this.usuario = usuario;
		this.endereco = endereco;
	}

	public UsuarioDTO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioDTO usuario) {
		this.usuario = usuario;
	}

	public EnderecoDTO getEndereco() {
		return endereco;
	}

	public void setEndereco(EnderecoDTO endereco) {
		this.endereco = endereco;
	}
}