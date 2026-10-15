package com.guilherme.augusto.integracao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    // Mapeia a rota /tarefas para o arquivo index.html
    @GetMapping("/tarefas")
    public String abrirPaginaDeTarefas() {
        // O Spring vai procurar por um arquivo chamado "index.html" 
        // dentro da pasta src/main/resources/static/ (ou templates/)
        return "index.html"; 
    }
    
    // Você também pode mapear a raiz "/" diretamente
    @GetMapping("/")
    public String index() {
        return "index.html"; 
    }
}