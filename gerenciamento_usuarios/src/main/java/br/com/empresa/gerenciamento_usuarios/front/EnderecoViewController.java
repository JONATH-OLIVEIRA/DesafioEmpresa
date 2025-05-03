package br.com.empresa.gerenciamento_usuarios.front;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.empresa.gerenciamento_usuarios.Service.EnderecoService;
import br.com.empresa.gerenciamento_usuarios.model.Endereco;

@Controller
@RequestMapping("/enderecos")
public class EnderecoViewController {

	@Autowired
	private EnderecoService enderecoService;

	@GetMapping("/buscar")
	public String exibirFormularioBusca(Model model) {
		model.addAttribute("endereco", new Endereco()); // Garante que a página tenha um objeto Endereco
		return "endereco"; // Renderiza "endereco.html"
	}

	@PostMapping("/buscar")
	public String buscarEndereco(@RequestParam String cep, Model model) {
		if (!cep.matches("\\d{5}-\\d{3}")) {
			model.addAttribute("erro", "Formato de CEP inválido! Use o formato 99999-999.");
			return "endereco";
		}

		try {
			Endereco endereco = enderecoService.buscarEnderecoPorCep(cep);
			model.addAttribute("endereco", endereco);
		} catch (Exception e) {
			model.addAttribute("erro", e.getMessage());
		}
		return "endereco";
	}

}