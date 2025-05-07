document.addEventListener("DOMContentLoaded", function() {
    const tipoSelect = document.getElementById("tipo");
    const documentoInput = document.getElementById("documento");
    const labelDocumento = document.getElementById("labelDocumento");

    function aplicarMascara(valor, tipo) {
        valor = valor.replace(/\D/g, '');
        
        if (tipo === "FISICA") {
            return valor.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
        } else {
            return valor.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/, "$1.$2.$3/$4-$5");
        }
    }

    tipoSelect?.addEventListener("change", function() {
        if (this.value === "FISICA") {
            labelDocumento.textContent = "CPF*";
            documentoInput.placeholder = "000.000.000-00";
        } else {
            labelDocumento.textContent = "CNPJ*";
            documentoInput.placeholder = "00.000.000/0000-00";
        }
        documentoInput.value = aplicarMascara(documentoInput.value, this.value);
    });

    documentoInput?.addEventListener("input", function() {
        this.value = aplicarMascara(this.value, tipoSelect.value);
    });
});