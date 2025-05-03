package br.com.empresa.gerenciamento_usuarios.front;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.empresa.gerenciamento_usuarios.Service.UsuarioService;
import br.com.empresa.gerenciamento_usuarios.dto.UsuarioComEnderecoDTO;
import br.com.empresa.gerenciamento_usuarios.enums.Role;
import br.com.empresa.gerenciamento_usuarios.enums.Sexo;
import br.com.empresa.gerenciamento_usuarios.enums.TipoUsuario;
import br.com.empresa.gerenciamento_usuarios.model.Endereco;
import br.com.empresa.gerenciamento_usuarios.model.Usuario;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioViewController.class);

    @Autowired
    private UsuarioService usuarioService;
    
 // 🔹 Exibir lista de usuários
    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios"; // Renderiza lista_usuarios.html
    }

    // 🔹 Exibir formulário de cadastro
    @GetMapping("/cadastro")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("usuarioComEnderecoDTO", new UsuarioComEnderecoDTO());
        model.addAttribute("sexos", List.of(Sexo.MASCULINO, Sexo.FEMININO, Sexo.OUTRO));
        model.addAttribute("tipos", List.of(TipoUsuario.FISICA, TipoUsuario.JURIDICA));
        model.addAttribute("roles", List.of(Role.ADMINISTRADOR, Role.USUARIO));
        return "cadastro";
    }


    // 🔹 Processar cadastro de usuário e endereço
    @PostMapping("/salvar")
    public String salvarUsuarioComEndereco(@ModelAttribute UsuarioComEnderecoDTO usuarioComEnderecoDTO) {
        logger.info("Tentando salvar usuário: {}", usuarioComEnderecoDTO.getUsuario().getNome());

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioComEnderecoDTO.getUsuario().getNome());
        usuario.setNomeUsuario(usuarioComEnderecoDTO.getUsuario().getNomeUsuario());
        usuario.setSenha(new BCryptPasswordEncoder().encode(usuarioComEnderecoDTO.getUsuario().getSenha())); // 🔐 Criptografa senha
        usuario.setEmail(usuarioComEnderecoDTO.getUsuario().getEmail());
        usuario.setDataNascimento(usuarioComEnderecoDTO.getUsuario().getDataNascimento());
        usuario.setSexo(usuarioComEnderecoDTO.getUsuario().getSexo());
        usuario.setTipo(usuarioComEnderecoDTO.getUsuario().getTipo());
        usuario.setDocumento(usuarioComEnderecoDTO.getUsuario().getDocumento());
        usuario.setRoles(usuarioComEnderecoDTO.getUsuario().getRoles());

        // Criar e vincular endereço ao usuário
        Endereco endereco = new Endereco();
        endereco.setCep(usuarioComEnderecoDTO.getEndereco().getCep());
        endereco.setLogradouro(usuarioComEnderecoDTO.getEndereco().getLogradouro());
        endereco.setComplemento(usuarioComEnderecoDTO.getEndereco().getComplemento());
        endereco.setNumeroCasa(usuarioComEnderecoDTO.getEndereco().getNumeroCasa());
        endereco.setBairro(usuarioComEnderecoDTO.getEndereco().getBairro());
        endereco.setLocalidade(usuarioComEnderecoDTO.getEndereco().getLocalidade());
        endereco.setUf(usuarioComEnderecoDTO.getEndereco().getUf());

        usuario.setEndereco(endereco);

        try {
            usuarioService.salvarUsuario(usuario);
            logger.info("Usuário cadastrado com sucesso!");
            return "redirect:/usuarios"; // Redireciona para a listagem após o cadastro
        } catch (Exception e) {
            logger.error("Erro ao salvar usuário: {}", e.getMessage());
            return "erro"; // Redireciona para uma página de erro
        }
    }
}