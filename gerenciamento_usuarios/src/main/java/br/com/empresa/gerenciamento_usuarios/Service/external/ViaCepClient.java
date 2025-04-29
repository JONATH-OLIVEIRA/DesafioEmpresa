package br.com.empresa.gerenciamento_usuarios.Service.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.empresa.gerenciamento_usuarios.model.Endereco;

@FeignClient(name = "viacep", url = "https://viacep.com.br/ws")
public interface ViaCepClient {
    @GetMapping("/{cep}/json/")
    Endereco buscarEnderecoPorCep(@PathVariable("cep") String cep);
}
