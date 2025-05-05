package br.com.empresa.gerenciamento_usuarios.Controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.empresa.gerenciamento_usuarios.Service.UsuarioService;
import br.com.empresa.gerenciamento_usuarios.dto.UsuarioComEnderecoDTO;
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

	@PutMapping("/{id}")
	public ResponseEntity<?> atualizarUsuario(@PathVariable Long id,
	    @Validated @RequestBody UsuarioDTO usuarioDTO) {
	    
	    try {
	        logger.info("Recebendo requisição para atualizar usuário ID: {}", id);
	        
	        // 1. Busca o usuário existente
	        Usuario usuarioExistente = usuarioService.buscarPorId(id);
	        
	        // 2. Converte para DTO combinado (para manter compatibilidade com o service)
	        UsuarioComEnderecoDTO usuarioComEnderecoDTO = new UsuarioComEnderecoDTO();
	        usuarioComEnderecoDTO.setUsuario(usuarioDTO);
	        
	        // 3. Atualiza sem foto (null) usando o service existente
	        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(
	            id, 
	            usuarioComEnderecoDTO, 
	            null // Sem arquivo de foto
	        );
	        
	        logger.info("Usuário ID {} atualizado com sucesso", id);
	        return ResponseEntity.ok(usuarioService.converterParaDTO(usuarioAtualizado));
	        
	    } catch (UsernameNotFoundException e) {
	        logger.error("Usuário não encontrado ID: {}", id);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	            .body(Map.of(
	                "error", "Usuário não encontrado",
	                "message", e.getMessage(),
	                "usuarioId", id
	            ));
	            
	    } catch (IllegalArgumentException e) {
	        logger.error("Erro de validação ao atualizar usuário ID {}: {}", id, e.getMessage());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	            .body(Map.of(
	                "error", "Dados inválidos",
	                "message", e.getMessage(),
	                "usuarioId", id
	            ));
	            
	    } catch (Exception e) {
	        logger.error("Erro interno ao atualizar usuário ID {}: {}", id, e.getMessage(), e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Map.of(
	                "error", "Erro interno no servidor",
	                "message", "Não foi possível atualizar o usuário",
	                "usuarioId", id
	            ));
	    }
	}

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

	@GetMapping("/buscar/{nome}")
	public ResponseEntity<?> buscarPorNome(@PathVariable String nome) {
	    try {
	        logger.info("Buscando usuários por nome: {}", nome);
	        
	        // Usando o service existente que já tem cache e paginação
	        Page<UsuarioDTO> usuariosPage = usuarioService.buscarUsuariosPorNomePaginado(
	            nome, 
	            PageRequest.of(0, 20) // Página 0 com 20 resultados
	        );
	        
	        if (usuariosPage.isEmpty()) {
	            logger.warn("Nenhum usuário encontrado para o nome: {}", nome);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of(
	                    "message", "Nenhum usuário encontrado",
	                    "searchTerm", nome
	                ));
	        }
	        
	        return ResponseEntity.ok(Map.of(
	            "usuarios", usuariosPage.getContent(),
	            "total", usuariosPage.getTotalElements()
	        ));
	        
	    } catch (Exception e) {
	        logger.error("Erro ao buscar usuários por nome: " + nome, e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Map.of(
	                "error", "Erro ao processar a busca",
	                "message", e.getMessage()
	            ));
	    }
	}
}