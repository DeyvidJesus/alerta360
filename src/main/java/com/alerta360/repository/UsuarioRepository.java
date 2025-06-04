package com.alerta360.repository;

import com.alerta360.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por email
    Optional<Usuario> findByEmail(String email);

    // Verificar se email já existe
    boolean existsByEmail(String email);

    // Listar usuários ativos
    List<Usuario> findByAtivoTrue();

    // Buscar por tipo de usuário
    List<Usuario> findByTipo(String tipo);

    // Buscar usuários ativos por tipo
    List<Usuario> findByAtivoTrueAndTipo(String tipo);

    // Buscar por nome (busca parcial)
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    // Buscar usuários cadastrados em um período
    List<Usuario> findByDataCadastroBetween(LocalDateTime inicio, LocalDateTime fim);

    // Contar usuários por tipo
    @Query("SELECT u.tipo, COUNT(u) FROM Usuario u GROUP BY u.tipo")
    List<Object[]> countUsuariosPorTipo();

    // Contar usuários ativos
    long countByAtivoTrue();

    // Buscar usuários inativos há mais de X dias
    @Query("SELECT u FROM Usuario u WHERE u.ativo = false " +
            "AND u.dataCadastro < :dataLimite")
    List<Usuario> findUsuariosInativosAntigos(@Param("dataLimite") LocalDateTime dataLimite);
}