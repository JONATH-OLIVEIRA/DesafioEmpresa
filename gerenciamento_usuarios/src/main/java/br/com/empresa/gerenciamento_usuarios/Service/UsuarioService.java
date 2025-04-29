package br.com.empresa.gerenciamento_usuarios.Service;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.empresa.gerenciamento_usuarios.dto.UsuarioDTO;
import br.com.empresa.gerenciamento_usuarios.enums.Role;
import br.com.empresa.gerenciamento_usuarios.enums.TipoUsuario;
import br.com.empresa.gerenciamento_usuarios.model.Usuario;
import br.com.empresa.gerenciamento_usuarios.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	@Autowired
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public Usuario converterDTOParaEntidade(UsuarioDTO dto) {
		Usuario usuario = new Usuario();
		usuario.setNome(dto.getNome());
		usuario.setNomeUsuario(dto.getNomeUsuario());
		usuario.setSenha(dto.getSenha()); // A senha será criptografada depois
		usuario.setEmail(dto.getEmail());
		usuario.setDataNascimento(dto.getDataNascimento());
		usuario.setSexo(dto.getSexo());
		usuario.setTipo(dto.getTipo());
		usuario.setDocumento(dto.getDocumento());
		if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
			usuario.setRoles(Set.of(Role.USUARIO)); // Padrão: USUARIO
		} else {
			usuario.setRoles(dto.getRoles());
		}

		return usuario;
	}

	public Usuario salvarUsuario(Usuario usuario) {
		// Validação 1: Nome de usuário único
		if (usuarioRepository.existsByNomeUsuario(usuario.getNomeUsuario())) {
			throw new IllegalArgumentException("Nome de usuário já está em uso.");
		}

		// Validação 2: Verificar se o usuário tem mais de 18 anos
		if (usuario.getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
			throw new IllegalArgumentException("O usuário deve ter pelo menos 18 anos.");
		}

		// Validação 3: Validar a senha
		if (!usuario.getSenha().matches("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
			throw new IllegalArgumentException(
					"A senha deve conter pelo menos 8 caracteres, uma letra maiúscula e um número.");
		}
		if (usuario.getTipo() == TipoUsuario.FISICA && !usuario.getDocumento().matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
		    throw new IllegalArgumentException("O CPF está inválido para um usuário do tipo FISICA.");
		}

		if (usuario.getTipo() == TipoUsuario.JURIDICA && !usuario.getDocumento().matches("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}")) {
		    throw new IllegalArgumentException("O CNPJ está inválido para um usuário do tipo JURIDICA.");
		}

		// Validação 4: Criptografar a senha
		usuario.criptografarSenha();

		// Salvar o usuário no banco de dados
		return usuarioRepository.save(usuario);
	}
}
