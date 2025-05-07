function validarIdade() {
    const dataNascimento = document.getElementById("dataNascimento");
    const nascimento = new Date(dataNascimento.value);
    const hoje = new Date();
    
    let idade = hoje.getFullYear() - nascimento.getFullYear();
    const mesDiff = hoje.getMonth() - nascimento.getMonth();
    
    if (mesDiff < 0 || (mesDiff === 0 && hoje.getDate() < nascimento.getDate())) {
        idade--;
    }

    if (idade < 18) {
        dataNascimento.setCustomValidity("Você precisa ter pelo menos 18 anos.");
        return false;
    }
    
    dataNascimento.setCustomValidity("");
    return true;
}