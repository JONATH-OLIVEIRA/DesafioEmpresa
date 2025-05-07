document.addEventListener("DOMContentLoaded", () => {
    const buttons = document.querySelectorAll('.btn');
    
    buttons.forEach(button => {
        // Efeito ao passar o mouse
        button.addEventListener('mouseenter', () => {
            button.style.transform = 'translateY(-3px) scale(1.05)';
        });
        
        // Efeito ao tirar o mouse
        button.addEventListener('mouseleave', () => {
            button.style.transform = 'translateY(0) scale(1)';
        });
        
        // Efeito ao clicar
        button.addEventListener('mousedown', () => {
            button.style.transform = 'translateY(1px)';
        });
        
        button.addEventListener('mouseup', () => {
            button.style.transform = 'translateY(-3px)';
        });
    });
});