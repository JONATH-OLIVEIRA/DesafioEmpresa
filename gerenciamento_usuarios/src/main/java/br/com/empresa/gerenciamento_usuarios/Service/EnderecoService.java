package br.com.empresa.gerenciamento_usuarios.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.empresa.gerenciamento_usuarios.model.Endereco;
import br.com.empresa.gerenciamento_usuarios.repository.EnderecoRepository;

@Service
public class EnderecoService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    @Autowired
    private EnderecoRepository enderecoRepository;

    public Endereco buscarEnderecoPorCep(String cep) {
        // 1. Validar o formato do CEP antes da requisição
        if (!cep.matches("\\d{5}-\\d{3}")) {
            throw new IllegalArgumentException("Formato de CEP inválido! O formato correto é 99999-999.");
        }

        RestTemplate restTemplate = new RestTemplate();

        try {
            // 2. Consome a API ViaCep
            Endereco endereco = restTemplate.getForObject(VIACEP_URL, Endereco.class, cep);

            // 3. Verificar se o endereço retornado é válido
            if (endereco == null || endereco.getCep() == null) {
                throw new IllegalArgumentException("CEP inválido ou não encontrado!");
            }

            return endereco;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar endereço na API ViaCep: " + e.getMessage());
        }
    }

    public Endereco salvarEndereco(Endereco endereco) {
        // 4. Validar se os campos essenciais estão preenchidos
        if (endereco.getCep() == null || endereco.getLogradouro() == null || endereco.getLocalidade() == null || endereco.getUf() == null) {
            throw new IllegalArgumentException("Endereço incompleto. Certifique-se de preencher todos os campos obrigatórios.");
        }

        return enderecoRepository.save(endereco);
    }

    public Endereco atualizarEndereco(Long id, Endereco enderecoAtualizado) {
        // 5. Verificar se o endereço existe
        return enderecoRepository.findById(id).map(endereco -> {
            endereco.setCep(enderecoAtualizado.getCep());
            endereco.setLogradouro(enderecoAtualizado.getLogradouro());
            endereco.setComplemento(enderecoAtualizado.getComplemento());
            endereco.setNumeroCasa(enderecoAtualizado.getNumeroCasa());
            endereco.setBairro(enderecoAtualizado.getBairro());
            endereco.setLocalidade(enderecoAtualizado.getLocalidade());
            endereco.setUf(enderecoAtualizado.getUf());
            return enderecoRepository.save(endereco);
        }).orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado!"));
    }

    public void deletarEndereco(Long id) {
        // 6. Remover um endereço
        if (!enderecoRepository.existsById(id)) {
            throw new IllegalArgumentException("Endereço não encontrado!");
        }
        enderecoRepository.deleteById(id);
    }
}