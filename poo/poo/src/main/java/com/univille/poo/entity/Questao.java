package com.univille.poo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    
    @Lob 
    @Column(length = 5000) 
    private String pergunta;

    @Lob
    @Column(length = 10000)
    private String jsonRespostas; 
}