package com.univille.poo.service;

import com.univille.poo.config.GeminiAPI;
import com.univille.poo.dto.*;
import com.univille.poo.dto.AlternativaDTO;
import com.univille.poo.dto.QuestaoDTO;
import com.univille.poo.entity.Questao;
import com.univille.poo.repository.QuestaoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionMakerService {

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final GeminiAPI geminiAPI;
    private final QuestaoRepository repository;

    public QuestionMakerService(ObjectMapper objectMapper, GeminiAPI geminiAPI, QuestaoRepository repository) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
        this.geminiAPI = geminiAPI;
        this.repository = repository;
    }

    // Lista o histórico do banco
    public List<Questao> listarHistorico() {
        return repository.findAll();
    }

    // Busca uma específica e reconstrói o objeto DTO
    public QuestaoDTO buscarPorId(Long id) {
        Optional<Questao> entityOpt = repository.findById(id);
        if (entityOpt.isPresent()) {
            Questao entity = entityOpt.get();
            try {
                List<AlternativaDTO> respostas = objectMapper.readValue(
                        entity.getJsonRespostas(),
                        new TypeReference<>() {}
                );
                return new QuestaoDTO(entity.getTitulo(), entity.getPergunta(), respostas);
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }
        return null;
    }

    // Lógica principal unificada
    public QuestaoDTO gerarQuestao(String tema) {
        // 1. Montar a requisição para o Gemini
        String prompt = montarPrompt(tema);
        Part part = new Part(prompt);
        Content content = new Content(List.of(part));
        GeminiRequest request = new GeminiRequest(List.of(content));

        try {
            // 2. Chamar a API
            GeminiResponse response = restClient.post()
                    .uri(API_URL + "?key=" + geminiAPI.getKey())
                    .header("Content-Type", "application/json")
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            // 3. Processar a resposta
            if (response != null &&
                    !response.candidates().isEmpty() &&
                    !response.candidates().get(0).content().parts().isEmpty()) {

                String jsonTexto = response.candidates().get(0)
                        .content().parts().get(0)
                        .text();

                // Converte JSON -> Objeto Java (DTO)
                QuestaoDTO questaoDTO = converterParaQuiz(jsonTexto);

                // 4. Salvar no Banco de Dados (Histórico)
                if (questaoDTO != null) {
                    salvarNoBanco(questaoDTO);
                }

                return questaoDTO;
            }
        } catch (Exception e) {
            System.err.println("Erro ao chamar ou processar a API Gemini: " + e.getMessage());
        }
        return null;
    }

    // Método auxiliar para salvar no banco (para limpar o código principal)
    private void salvarNoBanco(QuestaoDTO questaoDTO) {
        try {
            Questao entidade = new Questao();
            entidade.setTitulo(questaoDTO.titulo());
            entidade.setPergunta(questaoDTO.pergunta());
            // Converte a lista de respostas para String JSON para guardar no banco
            entidade.setJsonRespostas(objectMapper.writeValueAsString(questaoDTO.respostas()));

            repository.save(entidade);
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao salvar histórico: " + e.getMessage());
        }
    }

    private QuestaoDTO converterParaQuiz(String jsonRaw) {
        try {
            String limpo = jsonRaw.replace("```json", "").replace("```", "").trim();
            // CORREÇÃO AQUI: Ler para QuestaoDTO.class, não Questao.class
            return objectMapper.readValue(limpo, QuestaoDTO.class);
        } catch (Exception e) {
            System.err.println("Erro ao converter JSON do Gemini para QuestaoDTO: " + e.getMessage());
            return null;
        }
    }

    private String montarPrompt(String tema) {
        return """
            Você é um professor universitário e deve gerar UMA questão de múltipla escolha.
            Gere uma questão de nível médio/difícil sobre o tema: "%s".
            
            SAÍDA DEVE SER ESTRITAMENTE UM OBJETO JSON, SEM CRASES DE MARKDOWN (`).
            O formato exato é:
            {
              "titulo": "Titulo Curto do Tópico",
              "pergunta": "Enunciado da questão?",
              "respostas": [
                {
                  "id": 1,
                  "resposta": "Texto da alternativa",
                  "descricao": "Explicação curta do porquê está certa ou errada",
                  "certo_ou_errado": false
                }
              ]
            }
            Gere 4 alternativas, sendo apenas 1 com "certo_ou_errado": true.
            Responda APENAS o JSON cru.
            """.formatted(tema);
    }
}