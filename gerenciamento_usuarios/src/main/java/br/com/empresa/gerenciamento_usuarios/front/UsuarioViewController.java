package br.com.empresa.gerenciamento_usuarios.front;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.empresa.gerenciamento_usuarios.Service.UsuarioService;
import br.com.empresa.gerenciamento_usuarios.dto.EnderecoDTO;
import br.com.empresa.gerenciamento_usuarios.dto.UsuarioComEnderecoDTO;
import br.com.empresa.gerenciamento_usuarios.dto.UsuarioDTO;
import br.com.empresa.gerenciamento_usuarios.enums.Role;
import br.com.empresa.gerenciamento_usuarios.enums.Sexo;
import br.com.empresa.gerenciamento_usuarios.enums.TipoUsuario;
import br.com.empresa.gerenciamento_usuarios.model.Endereco;
import br.com.empresa.gerenciamento_usuarios.model.Usuario;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

	private static final Logger logger = LoggerFactory.getLogger(UsuarioViewController.class);
	

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/login")
	public String mostrarPaginaLogin(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model) {

		if (error != null) {
			model.addAttribute("erro", "Credenciais inválidas");
		}

		if (logout != null) {
			model.addAttribute("mensagem", "Logout realizado com sucesso");
		}

		return "/"; // Retorna para login.html
	}

	@GetMapping("/listar")
	public String listarUsuarios(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, Model model,
			@AuthenticationPrincipal UserDetails userDetails) {

		if (userDetails == null) {
			return "redirect:/login";
		}

		Page<UsuarioDTO> usuariosPage = usuarioService
				.listarTodosUsuariosDTO(PageRequest.of(page, size, Sort.by("nome").ascending()));
		model.addAttribute("usuarios", usuariosPage);
		return "listar";
	}

	@GetMapping("/cadastro")
	public String exibirFormularioCadastro(Model model) {
		carregarDadosCadastro(model);
		model.addAttribute("usuarioComEnderecoDTO", new UsuarioComEnderecoDTO());
		return "cadastro";
	}

	@PostMapping("/salvar")
	public String salvarUsuarioComEndereco(@ModelAttribute @Valid UsuarioComEnderecoDTO usuarioComEnderecoDTO,
			BindingResult result, @RequestParam(value = "file", required = false) MultipartFile file, Model model) {

		carregarDadosCadastro(model);

		if (result.hasErrors()) {
			return "cadastro";
		}

		// Validação manual da idade
		if (usuarioComEnderecoDTO.getUsuario().getDataNascimento() == null
				|| usuarioComEnderecoDTO.getUsuario().getDataNascimento().isAfter(LocalDate.now().minusYears(18))) {
			model.addAttribute("erro", "O usuário deve ter pelo menos 18 anos");
			return "cadastro";
		}

		try {
			Usuario usuario = converterParaEntidade(usuarioComEnderecoDTO);

			if (file != null && !file.isEmpty()) {
				// 1. Verifica se o arquivo não está vazio
				if (file.isEmpty()) {
					model.addAttribute("erro", "O arquivo enviado está vazio");
					return "cadastro";
				}

				// 2. Processa o upload com tratamento melhorado
				String nomeArquivo = processarUpload(file, usuario.getNomeUsuario());

				if (!nomeArquivo.startsWith("uploads/")) {
				    nomeArquivo = "uploads/" + nomeArquivo;
				}
				usuario.setFoto("/" + nomeArquivo);

				logger.info("Arquivo salvo com sucesso: {}", usuario.getFoto());
			}

			usuarioService.salvarUsuario(usuario);

			// Redireciona para login após cadastro (mais lógico para novo usuário)
			return "redirect:/login?cadastroSuccess";

		} catch (IllegalArgumentException e) {
			logger.error("Erro de validação: {}", e.getMessage());
			model.addAttribute("erro", e.getMessage());
			return "cadastro";
		} catch (IOException e) {
			logger.error("Falha no upload do arquivo", e);
			model.addAttribute("erro", "Falha ao salvar a imagem: " + e.getMessage());
			return "cadastro";
		} catch (Exception e) {
			logger.error("Erro ao salvar usuário", e);
			model.addAttribute("erro", "Erro ao salvar usuário: " + e.getMessage());
			return "cadastro";
		}
	}

	@GetMapping("/dashboard")
	public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		try {
			Usuario usuario = usuarioService.buscarPorEmail(userDetails.getUsername());

			if (usuario == null) {
				model.addAttribute("error", "Usuário não encontrado no sistema");
				return "dashboard"; // Ainda mostra a página, mas com erro
			}

			model.addAttribute("usuario", usuario);
			model.addAttribute("now", LocalDateTime.now());

		} catch (Exception e) {
			model.addAttribute("error", "Erro ao carregar dados do usuário");
			logger.error("Erro no dashboard", e);
		}

		return "dashboard";
	}

	@GetMapping("/detalhes/{id}")
	public String exibirDetalhes(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {

		if (userDetails == null) {
			return "redirect:/login";
		}

		Usuario usuario = usuarioService.buscarPorId(id);
		model.addAttribute("usuario", usuario);
		return "detalhes";
	}

	private void carregarDadosCadastro(Model model) {
		model.addAttribute("sexos", List.of(Sexo.MASCULINO, Sexo.FEMININO, Sexo.OUTRO));
		model.addAttribute("tipos", List.of(TipoUsuario.FISICA, TipoUsuario.JURIDICA));
		model.addAttribute("roles", List.of(Role.ADMINISTRADOR, Role.USUARIO));
	}

	private Usuario converterParaEntidade(UsuarioComEnderecoDTO dto) {
		if (dto == null || dto.getUsuario() == null) {
			throw new IllegalArgumentException("DTO de usuário não pode ser nulo");
		}

		Usuario usuario = new Usuario();
		UsuarioDTO usuarioDTO = dto.getUsuario();

		usuario.setNome(usuarioDTO.getNome());
		usuario.setNomeUsuario(usuarioDTO.getNomeUsuario());
		usuario.setEmail(usuarioDTO.getEmail());
		usuario.setSenha(usuarioDTO.getSenha());
		usuario.setDataNascimento(usuarioDTO.getDataNascimento());
		usuario.setSexo(usuarioDTO.getSexo());
		usuario.setTipo(usuarioDTO.getTipo());
		usuario.setDocumento(usuarioDTO.getDocumento());
		usuario.setRoles(usuarioDTO.getRoles());

		if (dto.getEndereco() != null) {
			Endereco endereco = new Endereco();
			endereco.setCep(dto.getEndereco().getCep());
			endereco.setLogradouro(dto.getEndereco().getLogradouro());
			endereco.setComplemento(dto.getEndereco().getComplemento());
			endereco.setNumeroCasa(dto.getEndereco().getNumeroCasa());
			endereco.setBairro(dto.getEndereco().getBairro());
			endereco.setLocalidade(dto.getEndereco().getLocalidade());
			endereco.setUf(dto.getEndereco().getUf());
			usuario.setEndereco(endereco);
		}

		return usuario;
	}

	private String processarUpload(MultipartFile file, String prefixoNome) throws IOException {
		// 1. Caminho absoluto dentro do projeto
		Path uploadPath = Paths.get("src/main/resources/static/uploads/").toAbsolutePath();

		// 2. Garante que o diretório existe
		Files.createDirectories(uploadPath);

		// 3. Gera nome único
		String extensao = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String nomeArquivo = prefixoNome + "_" + System.currentTimeMillis() + extensao;

		// 4. Salva o arquivo
		Path destino = uploadPath.resolve(nomeArquivo);
		Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

		// 5. Retorna o caminho relativo (IMPORTANTE!)
		return nomeArquivo;
	}

	@GetMapping("/buscar")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> buscarUsuarios(@RequestParam(required = false) String nome,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

		Page<UsuarioDTO> pagina = (nome == null || nome.trim().isEmpty())
				? usuarioService.listarTodosUsuariosPaginado(PageRequest.of(page, size))
				: usuarioService.buscarUsuariosPorNomePaginado(nome, PageRequest.of(page, size));

		Map<String, Object> response = new HashMap<>();
		response.put("content", pagina.getContent());
		response.put("totalPages", pagina.getTotalPages());
		response.put("currentPage", pagina.getNumber());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/detalhes/editar/{id}")
	public String mostrarFormularioEdicao(@PathVariable Long id, Model model) {
		try {
			// 1. Busca o usuário completo com endereço
			Usuario usuario = usuarioService.buscarPorId(id);

			// 2. Converte para DTO
			UsuarioDTO usuarioDTO = usuarioService.converterParaDTO(usuario);

			// 3. Cria o DTO combinado
			UsuarioComEnderecoDTO usuarioComEnderecoDTO = new UsuarioComEnderecoDTO();
			usuarioComEnderecoDTO.setUsuario(usuarioDTO);

			// 4. Mapeia o endereço corretamente
			if (usuario.getEndereco() != null) {
				EnderecoDTO enderecoDTO = new EnderecoDTO();
				enderecoDTO.setCep(usuario.getEndereco().getCep());
				enderecoDTO.setLogradouro(usuario.getEndereco().getLogradouro());
				enderecoDTO.setComplemento(usuario.getEndereco().getComplemento());
				enderecoDTO.setNumeroCasa(usuario.getEndereco().getNumeroCasa());
				enderecoDTO.setBairro(usuario.getEndereco().getBairro());
				enderecoDTO.setLocalidade(usuario.getEndereco().getLocalidade());
				enderecoDTO.setUf(usuario.getEndereco().getUf());
				usuarioComEnderecoDTO.setEndereco(enderecoDTO);
			} else {
				usuarioComEnderecoDTO.setEndereco(new EnderecoDTO());
			}

			// 5. Adiciona enums necessários para os selects
			model.addAttribute("sexos", List.of(Sexo.MASCULINO, Sexo.FEMININO, Sexo.OUTRO));
			model.addAttribute("tipos", List.of(TipoUsuario.FISICA, TipoUsuario.JURIDICA));
			model.addAttribute("roles", List.of(Role.ADMINISTRADOR, Role.USUARIO));

			// 6. Adiciona o DTO combinado ao modelo
			model.addAttribute("usuarioComEnderecoDTO", usuarioComEnderecoDTO);

			// 7. Adiciona a URL da foto se existir
			if (usuario.getFoto() != null && !usuario.getFoto().isEmpty()) {
				model.addAttribute("fotoUsuario", usuario.getFoto());
			}

			return "editar";

		} catch (UsernameNotFoundException e) {
			model.addAttribute("erro", "Usuário não encontrado: " + e.getMessage());
			return "redirect:/usuarios/listar";
		} catch (Exception e) {
			logger.error("Erro ao carregar formulário de edição", e);
			model.addAttribute("erro", "Erro ao carregar formulário: " + e.getMessage());
			return "redirect:/usuarios/listar";
		}
	}

	@PostMapping("/detalhes/editar/{id}")
	public String processarEdicao(@PathVariable Long id,
	        @ModelAttribute("usuarioComEnderecoDTO") @Valid UsuarioComEnderecoDTO usuarioComEnderecoDTO,
	        BindingResult result, @RequestParam(value = "file", required = false) MultipartFile file, 
	        Model model, RedirectAttributes redirectAttributes) {

	    carregarDadosCadastro(model);

	    if (result.hasErrors()) {
	        return "editar"; // Volta para a view de edição com erros de validação
	    }

	    try {
	        usuarioService.atualizarUsuario(id, usuarioComEnderecoDTO, file);
	        redirectAttributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso!");
	        return "redirect:/usuarios/dashboard";

	    } catch (Exception e) {
	        logger.error("Erro ao atualizar usuário ID: " + id, e);
	        
	        // Adiciona os dados necessários para voltar ao formulário
	        model.addAttribute("erro", "Erro ao atualizar usuário: " + e.getMessage());
	        model.addAttribute("usuarioId", id);
	        
	        // Mantém os dados do formulário para não perder o que foi preenchido
	        model.addAttribute("usuarioComEnderecoDTO", usuarioComEnderecoDTO);
	        
	        if (file != null && !file.isEmpty()) {
	            model.addAttribute("arquivoCarregado", true);
	        }
	        
	        return "erro-edicao"; // Nova página de erro específica para edição
	    }
	}
	
	@PostMapping("/deletar/{id}")
	public String deletarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes,
	                           @AuthenticationPrincipal UserDetails userDetails) {
	    try {
	        // Verifica se o usuário logado está tentando se deletar
	        Usuario usuarioLogado = usuarioService.buscarPorEmail(userDetails.getUsername());
	        if (usuarioLogado.getId().equals(id)) {
	            redirectAttributes.addFlashAttribute("erro", "Você não pode deletar sua própria conta!");
	            return "redirect:/usuarios/listar";
	        }

	        usuarioService.deletarUsuario(id);
	        redirectAttributes.addFlashAttribute("sucesso", "Usuário deletado com sucesso!");
	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("erro", e.getMessage());
	    } catch (Exception e) {
	        logger.error("Erro ao deletar usuário", e);
	        redirectAttributes.addFlashAttribute("erro", "Erro ao deletar usuário: " + e.getMessage());
	    }
	    return "redirect:/usuarios/listar";
	}

}