import { validarIdade, configurarMascaraDocumento } from '../modules/formValidation.js';
import { buscarCep } from '../modules/cepService.js';

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form.needs-validation");

    form?.addEventListener("submit", event => {
        if (!form.checkValidity() || !validarIdade()) {
            event.preventDefault();
            event.stopPropagation();
        }
        form.classList.add("was-validated");
    });

    configurarMascaraDocumento();

    document.getElementById("cep")?.addEventListener("blur", buscarCep);
    document.getElementById("buscarCepBtn")?.addEventListener("click", buscarCep);
});
