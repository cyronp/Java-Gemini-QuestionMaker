package com.univille.poo.dto;

import java.util.List;

public record QuestaoDTO(
    String titulo,
    String pergunta,
    List<AlternativaDTO> respostas
) {}

