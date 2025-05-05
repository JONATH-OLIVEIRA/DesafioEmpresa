package br.com.empresa.gerenciamento_usuarios.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.empresa.gerenciamento_usuarios.Service.UsuarioService;
import br.com.empresa.gerenciamento_usuarios.dto.UsuarioDTO;
import br.com.empresa.gerenciamento_usuarios.model.Usuario;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

	private final UsuarioService usuarioService;

	@Autowired
	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@PostMapping
	public ResponseEntity<Usuario> salvarUsuario(@Validated @RequestBody UsuarioDTO usuarioDTO) {
		logger.info("Recebendo requisição para salvar usuário: {}", usuarioDTO);
		try {
			// Log antes de converter o DTO para a entidade
			logger.info("Senha recebida no DTO: {}", usuarioDTO.getSenha());

			// Converter o DTO para a entidade
			Usuario usuario = usuarioService.converterDTOParaEntidade(usuarioDTO);

			// Log após a conversão do DTO para a entidade
			logger.info("Usuário convertido para entidade com senha: {}", usuario.getSenha());

			// Salvar o usuário no banco
			Usuario novoUsuario = usuarioService.salvarUsuario(usuario);

			logger.info("Usuário salvo com sucesso: {}", novoUsuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
		} catch (IllegalArgumentException e) {
			logger.error("Erro ao salvar usuário: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

//	@PutMapping("/{id}")
//	public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id,
//			@Validated @RequestBody UsuarioDTO usuarioDTO) {
//		try {
	//		logger.info("Recebendo requisição para atualizar usuário ID: {}", id);
	//		Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioDTO);
	//		return ResponseEntity.ok(usuarioAtualizado);
	//	} catch (IllegalArgumentException e) {
	//		logger.error("Erro ao atualizar usuário: {}", e.getMessage());
	//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
	//	} catch (RuntimeException e) {
	//		logger.error("Erro interno ao atualizar usuário: {}", e.getMessage());
	//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	//	}
	//}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
		try {
			logger.info("Recebendo requisição para deletar usuário ID: {}", id);
			usuarioService.deletarUsuario(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			logger.error("Erro ao deletar usuário: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

//	@GetMapping("/buscar/{nome}")
//	public ResponseEntity<List<Usuario>> buscarPorNome(@PathVariable String nome) {
	//	List<Usuario> usuarios = usuarioService.buscarUsuariosPorNome(nome);
	// (usuarios.isEmpty()) {
	//		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	//	}
	//	return ResponseEntity.ok(usuarios);
	//}
}