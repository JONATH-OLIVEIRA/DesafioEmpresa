package br.com.empresa.gerenciamento_usuarios.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, 
                          Model model) {
        if (error != null) {
            model.addAttribute("error", "Email ou senha inválidos");
        }
        return "login";
    }
}