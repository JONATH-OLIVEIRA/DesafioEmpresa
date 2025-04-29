package br.com.empresa.gerenciamento_usuarios.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.empresa.gerenciamento_usuarios.model.Endereco;

@Service
public class EnderecoService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    public Endereco buscarEnderecoPorCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        
        // Consome a API VIACEP
        Endereco endereco = restTemplate.getForObject(VIACEP_URL, Endereco.class, cep);

        if (endereco == null || endereco.getCep() == null) {
            throw new IllegalArgumentException("CEP inválido ou não encontrado!");
        }

        return endereco; // Retorna o objeto preenchido
    }
}
