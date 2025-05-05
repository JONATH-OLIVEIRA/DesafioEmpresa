package br.com.empresa.gerenciamento_usuarios.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.empresa.gerenciamento_usuarios.dto.EnderecoDTO;
import br.com.empresa.gerenciamento_usuarios.dto.UsuarioComEnderecoDTO;
import br.com.empresa.gerenciamento_usuarios.dto.UsuarioDTO;
import br.com.empresa.gerenciamento_usuarios.enums.Role;
import br.com.empresa.gerenciamento_usuarios.enums.TipoUsuario;
import br.com.empresa.gerenciamento_usuarios.model.Endereco;
import br.com.empresa.gerenciamento_usuarios.model.Usuario;
import br.com.empresa.gerenciamento_usuarios.repository.EnderecoRepository;
import br.com.empresa.gerenciamento_usuarios.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final EnderecoRepository enderecoRepository;

	// Padrões de validação
	private static final Pattern SENHA_FORTE = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$");
	private static final Pattern CPF_PATTERN = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
	private static final Pattern CNPJ_PATTERN = Pattern.compile("\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
			EnderecoRepository enderecoRepository) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.enderecoRepository = enderecoRepository;
	}

	@CacheEvict(value = { "usuariosPorNome", "todosUsuariosPaginados" }, allEntries = true)
	public Usuario salvarUsuario(Usuario usuario) {
		validarUsuario(usuario);
		validarIdade(usuario.getDataNascimento());
		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		return usuarioRepository.save(usuario);
	}

	@CacheEvict(value = { "usuariosPorNome", "todosUsuariosPaginados" }, allEntries = true)
	public Usuario atualizarUsuario(Long id, UsuarioComEnderecoDTO usuarioComEnderecoDTO, MultipartFile fotoFile) {
		return usuarioRepository.findById(id).map(usuario -> {
			try {
				UsuarioDTO usuarioDTO = usuarioComEnderecoDTO.getUsuario();
				EnderecoDTO enderecoDTO = usuarioComEnderecoDTO.getEndereco();

				validarAtualizacaoUsuario(usuario, usuarioDTO);
				atualizarDadosUsuario(usuario, usuarioDTO);

				if (fotoFile != null && !fotoFile.isEmpty()) {
					String nomeArquivo = processarUpload(fotoFile, usuario.getNomeUsuario());
					usuario.setFoto("/uploads/" + nomeArquivo);
				}

				if (enderecoDTO != null) {
					Endereco endereco = usuario.getEndereco();
					if (endereco == null) {
						endereco = new Endereco();
					}

					endereco.setLogradouro(enderecoDTO.getLogradouro());
					endereco.setNumeroCasa(enderecoDTO.getNumeroCasa());
					endereco.setLocalidade(enderecoDTO.getLocalidade());
					endereco.setUf(enderecoDTO.getUf());
					endereco.setCep(enderecoDTO.getCep());
					endereco.setComplemento(enderecoDTO.getComplemento());

					usuario.setEndereco(endereco); // Com CascadeType.ALL, isso já cuida de salvar/atualizar o endereço
				}

				return usuarioRepository.save(usuario);

			} catch (IOException e) {
				throw new RuntimeException("Falha ao processar a foto: " + e.getMessage(), e);
			}
		}).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado!"));
	}

	@CacheEvict(value = { "usuariosPorNome", "todosUsuariosPaginados" }, allEntries = true)
	public void deletarUsuario(Long id) {
		if (!usuarioRepository.existsById(id)) {
			throw new IllegalArgumentException("Usuário não encontrado!");
		}
		usuarioRepository.deleteById(id);
	}

	@Cacheable("usuariosPorNome")
	public Page<UsuarioDTO> buscarUsuariosPorNome(String nome, Pageable pageable) {
		return usuarioRepository.findByNomeContainingIgnoreCase(nome, pageable).map(this::converterParaDTO);
	}

	@Cacheable("todosUsuariosPaginados")
	public Page<UsuarioDTO> listarTodosUsuariosDTO(Pageable pageable) {
		return usuarioRepository.findAllByOrderByNomeAsc(pageable).map(this::converterParaDTO);
	}

	public Usuario buscarPorId(Long usuarioId) {
		return usuarioRepository.findById(usuarioId)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
	}

	public Usuario buscarPorEmail(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email);
		if (usuario == null) {
			throw new UsernameNotFoundException("Usuário não encontrado com email: " + email);
		}
		return usuario;
	}

	public UsuarioDTO buscarUsuarioDTOPorId(Long usuarioId) {
		return converterParaDTO(buscarPorId(usuarioId));
	}

	public boolean existePorUsername(String nomeUsuario) {
		if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
			throw new IllegalArgumentException("Nome de usuário não pode ser vazio");
		}
		return usuarioRepository.existsByNomeUsuario(nomeUsuario.trim());
	}

	public Usuario converterDTOParaEntidade(UsuarioDTO dto) {
		Usuario usuario = new Usuario();
		if (dto.getId() != null) {
			usuario.setId(dto.getId());
		}
		usuario.setNome(dto.getNome());
		usuario.setNomeUsuario(dto.getNomeUsuario());
		usuario.setSenha(dto.getSenha());
		usuario.setEmail(dto.getEmail());
		usuario.setDataNascimento(dto.getDataNascimento());
		usuario.setSexo(dto.getSexo());
		usuario.setTipo(dto.getTipo());
		usuario.setDocumento(dto.getDocumento());
		usuario.setRoles(dto.getRoles() == null || dto.getRoles().isEmpty() ? Set.of(Role.USUARIO) : dto.getRoles());
		return usuario;
	}

	public UsuarioDTO converterParaDTO(Usuario usuario) {
		if (usuario == null) {
			return null;
		}
		UsuarioDTO dto = new UsuarioDTO();
		dto.setId(usuario.getId());
		dto.setNome(usuario.getNome());
		dto.setNomeUsuario(usuario.getNomeUsuario());
		dto.setEmail(usuario.getEmail());
		dto.setDataNascimento(usuario.getDataNascimento());
		dto.setSexo(usuario.getSexo());
		dto.setTipo(usuario.getTipo());
		dto.setDocumento(usuario.getDocumento());
		dto.setRoles(usuario.getRoles());
		return dto;
	}

	private void validarUsuario(Usuario usuario) {
		if (usuarioRepository.existsByNomeUsuario(usuario.getNomeUsuario())) {
			throw new IllegalArgumentException("Nome de usuário já está em uso.");
		}
		if (usuarioRepository.existsByEmail(usuario.getEmail())) {
			throw new IllegalArgumentException("Email já está em uso.");
		}
		validarIdade(usuario.getDataNascimento());
		validarSenha(usuario.getSenha());
		validarDocumento(usuario.getTipo(), usuario.getDocumento());
		validarEmail(usuario.getEmail());
	}

	private void validarAtualizacaoUsuario(Usuario usuarioExistente, UsuarioDTO usuarioDTO) {
		if (!usuarioExistente.getEmail().equals(usuarioDTO.getEmail())
				&& usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
			throw new IllegalArgumentException("Email já está em uso por outro usuário.");
		}

		if (!usuarioExistente.getNomeUsuario().equals(usuarioDTO.getNomeUsuario())
				&& usuarioRepository.existsByNomeUsuario(usuarioDTO.getNomeUsuario())) {
			throw new IllegalArgumentException("Nome de usuário já está em uso.");
		}

		validarIdade(usuarioDTO.getDataNascimento());
		validarDocumento(usuarioDTO.getTipo(), usuarioDTO.getDocumento());
		validarEmail(usuarioDTO.getEmail());

		if (usuarioDTO.getSenha() != null && !usuarioDTO.getSenha().isEmpty()) {
			validarSenha(usuarioDTO.getSenha());
		}
	}

	private void validarIdade(LocalDate dataNascimento) {
		if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now().minusYears(18))) {
			throw new IllegalArgumentException("O usuário deve ter pelo menos 18 anos.");
		}
	}

	private void validarSenha(String senha) {
		if (senha != null && !senha.isEmpty() && !SENHA_FORTE.matcher(senha).matches()) {
			throw new IllegalArgumentException(
					"A senha deve conter pelo menos 8 caracteres, uma letra maiúscula, uma letra minúscula e um número.");
		}
	}

	private void validarDocumento(TipoUsuario tipo, String documento) {
		if (tipo == TipoUsuario.FISICA && !CPF_PATTERN.matcher(documento).matches()) {
			throw new IllegalArgumentException("CPF inválido para pessoa física.");
		}
		if (tipo == TipoUsuario.JURIDICA && !CNPJ_PATTERN.matcher(documento).matches()) {
			throw new IllegalArgumentException("CNPJ inválido para pessoa jurídica.");
		}
	}

	private void validarEmail(String email) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new IllegalArgumentException("Formato de e-mail inválido.");
		}
	}

	private void atualizarDadosUsuario(Usuario usuario, UsuarioDTO dto) {
		usuario.setNome(dto.getNome());
		usuario.setNomeUsuario(dto.getNomeUsuario());
		usuario.setEmail(dto.getEmail());
		usuario.setDataNascimento(dto.getDataNascimento());
		usuario.setSexo(dto.getSexo());
		usuario.setTipo(dto.getTipo());
		usuario.setDocumento(dto.getDocumento());
		usuario.setRoles(dto.getRoles() == null || dto.getRoles().isEmpty() ? Set.of(Role.USUARIO) : dto.getRoles());

		if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
			usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
		}
	}

	private String processarUpload(MultipartFile file, String prefixoNome) throws IOException {
		Path uploadPath = Paths.get("src/main/resources/static/uploads/").toAbsolutePath();
		Files.createDirectories(uploadPath);

		String extensao = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String nomeArquivo = prefixoNome + "_" + System.currentTimeMillis() + extensao;

		Path destino = uploadPath.resolve(nomeArquivo);
		Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

		return nomeArquivo;
	}

	@Cacheable("todosUsuarios")
	public List<UsuarioDTO> listarTodosUsuarios() {
		return usuarioRepository.findAllByOrderByNomeAsc().stream().map(this::converterParaDTO)
				.collect(Collectors.toList());
	}

	public Page<UsuarioDTO> listarTodosUsuariosPaginado(PageRequest pageRequest) {
		return usuarioRepository.findAll(pageRequest).map(this::converterParaDTO);
	}

	public Page<UsuarioDTO> buscarUsuariosPorNomePaginado(String nome, PageRequest pageRequest) {
		return usuarioRepository.findByNomeContainingIgnoreCase(nome, pageRequest).map(this::converterParaDTO);
	}
}