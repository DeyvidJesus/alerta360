package com.alerta360.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String nome;

    // Aceita a senha na entrada, mas nunca devolve o hash nas respostas
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    private boolean ativo;
    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo;
}
