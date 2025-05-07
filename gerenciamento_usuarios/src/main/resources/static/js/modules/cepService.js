function buscarCep() {
    const cepInput = document.getElementById("cep");
    const cep = cepInput.value.replace(/\D/g, '');
    const btn = document.getElementById("buscarCepBtn");
    
    if (cep.length !== 8) {
        cepInput.setCustomValidity("CEP deve conter 8 dígitos");
        return;
    }

    btn.innerHTML = '<i class="bi bi-hourglass"></i>';
    btn.disabled = true;

    fetch(`https://viacep.com.br/ws/${cep}/json/`)
        .then(response => response.json())
        .then(data => {
            if (data.erro) {
                throw new Error("CEP não encontrado");
            }
            
            document.getElementById("logradouro").value = data.logradouro || "";
            document.getElementById("bairro").value = data.bairro || "";
            document.getElementById("localidade").value = data.localidade || "";
            document.getElementById("uf").value = data.uf || "";
        })
        .catch(error => {
            console.error("Erro ao buscar CEP:", error);
            alert(error.message);
        })
        .finally(() => {
            btn.innerHTML = '<i class="bi bi-search"></i>';
            btn.disabled = false;
        });
}

// Configura eventos
document.getElementById("buscarCepBtn")?.addEventListener("click", buscarCep);
document.getElementById("cep")?.addEventListener("blur", buscarCep);