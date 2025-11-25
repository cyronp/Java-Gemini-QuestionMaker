package com.univille.poo.controller;

import com.univille.poo.dto.QuestaoDTO;
import com.univille.poo.service.QuestionMakerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final QuestionMakerService service;

    public HomeController(QuestionMakerService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("historico", service.listarHistorico());
        return "index";
    }

    @PostMapping("/gerar")
    public String gerar(@RequestParam("prompt") String tema, Model model) {
        QuestaoDTO questao = service.gerarQuestao(tema);
        model.addAttribute("questao", questao);
        model.addAttribute("temaAnterior", tema);
        
        model.addAttribute("historico", service.listarHistorico());
        
        return "index";
    }

    @GetMapping("/historico/{id}")
    public String carregarHistorico(@PathVariable Long id, Model model) {
        QuestaoDTO questao = service.buscarPorId(id);
        model.addAttribute("questao", questao);
        
        model.addAttribute("historico", service.listarHistorico());
        
        return "index";
    }
}