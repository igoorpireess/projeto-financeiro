package com.financeiro.controller;

import com.financeiro.model.Categoria;
import com.financeiro.repository.CategoriaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("novaCategoria", new Categoria());
        return "categorias";
    }

    @PostMapping
    public String salvar(Categoria categoria, RedirectAttributes redirectAttributes) {
        categoriaRepository.save(categoria);
        redirectAttributes.addFlashAttribute("success", "Categoria salva com sucesso!");
        return "redirect:/categorias";
    }
}
