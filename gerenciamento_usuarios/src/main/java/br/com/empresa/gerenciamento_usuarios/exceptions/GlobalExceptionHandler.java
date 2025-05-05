package br.com.empresa.gerenciamento_usuarios.exceptions;



import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Tratamento para erros de entidade não encontrada
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException ex, Model model, 
                                             HttpServletRequest request) {
        logger.error("Entidade não encontrada: {}", ex.getMessage());
        
        model.addAttribute("error", "Recurso não encontrado");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("path", request.getRequestURI());
        
        return "error/404";
    }

    // Tratamento para erros de acesso negado
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model, 
                                           HttpServletRequest request) {
        logger.warn("Acesso negado: {}", ex.getMessage());
        
        model.addAttribute("error", "Acesso negado");
        model.addAttribute("message", "Você não tem permissão para acessar este recurso");
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        model.addAttribute("path", request.getRequestURI());
        
        return "error/403";
    }

    
    // Tratamento para IOExceptions (como problemas com upload de arquivos)
    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException ex, Model model, 
                                  HttpServletRequest request) {
        logger.error("Erro de I/O: {}", ex.getMessage());
        
        model.addAttribute("error", "Erro no processamento de arquivo");
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("path", request.getRequestURI());
        
        return "error/500";
    }

    // Tratamento genérico para todas as outras exceções
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception ex, Model model, 
                                    HttpServletRequest request) {
        logger.error("Erro interno: {}", ex.getMessage(), ex);
        
        model.addAttribute("error", "Erro interno no servidor");
        model.addAttribute("message", "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("path", request.getRequestURI());
        
        return "error/500";
    }

    // Tratamento para argumentos inválidos
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, 
                                               RedirectAttributes redirectAttributes) {
        logger.error("Argumento inválido: {}", ex.getMessage());
        
        redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        return "redirect:/usuarios/listar";
    }

    // Tratamento para usuário não encontrado
    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException ex, 
                                                RedirectAttributes redirectAttributes) {
        logger.error("Usuário não encontrado: {}", ex.getMessage());
        
        redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        return "redirect:/login";
    }
}