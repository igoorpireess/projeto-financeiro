package com.financeiro.controller;

import com.financeiro.service.ImportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @GetMapping("/importar")
    public String importarPage() {
        return "importar";
    }

    @PostMapping("/importar")
    public String importarArquivo(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor, selecione um arquivo.");
            return "redirect:/importar";
        }

        try {
            int total = importService.importarArquivo(file);
            redirectAttributes.addFlashAttribute("success", "Sucesso! " + total + " registros importados.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao processar o arquivo: " + e.getMessage());
        }

        return "redirect:/importar";
    }
}
