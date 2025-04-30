package br.com.empresa.gerenciamento_usuarios.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.faces.webapp.FacesServlet;

@Configuration
public class FacesConfig {
    @Bean
    public ServletRegistrationBean<FacesServlet> facesServlet() {
        ServletRegistrationBean<FacesServlet> servletBean = new ServletRegistrationBean<>(new FacesServlet(), "*.xhtml");
        servletBean.setLoadOnStartup(1);
        return servletBean;
    }
}

