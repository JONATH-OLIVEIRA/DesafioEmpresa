package br.com.empresa.gerenciamento_usuarios.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.empresa.gerenciamento_usuarios.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	boolean existsByEmail(String email);
	List<Usuario> findByNomeContainingIgnoreCase(String nome);

	boolean existsByNomeUsuario(String nomeUsuario);

	Usuario findByEmail(String email);
		
	List<Usuario> findAllByOrderByNomeAsc();
	
	Page<Usuario> findAllByOrderByNomeAsc(Pageable pageable);
	
	Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
