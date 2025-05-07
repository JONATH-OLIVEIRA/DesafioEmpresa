document.addEventListener("DOMContentLoaded", function() {
    // Configura validação do formulário
    const form = document.getElementById("cadastroForm");
    
    form?.addEventListener("submit", function(event) {
        if (!form.checkValidity() || !validarIdade()) {
            event.preventDefault();
            event.stopPropagation();
        }
        form.classList.add("was-validated");
    });

    // Configura data máxima (18 anos)
    const dataNascimentoInput = document.getElementById('dataNascimento');
    if (dataNascimentoInput) {
        const hoje = new Date();
        const maxDate = new Date(hoje.getFullYear() - 18, hoje.getMonth(), hoje.getDate());
        dataNascimentoInput.max = maxDate.toISOString().split('T')[0];
    }
});