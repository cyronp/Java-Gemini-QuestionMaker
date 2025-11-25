package com.univille.poo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlternativaDTO(
        int id,
        String resposta,
        String descricao,
        @JsonProperty("certo_ou_errado") boolean certoOuErrado
) {}