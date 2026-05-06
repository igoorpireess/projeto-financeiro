package com.financeiro.controller;

import com.financeiro.model.Despesa;
import com.financeiro.repository.DespesaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RelatorioController {

    private final DespesaRepository despesaRepository;

    public RelatorioController(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    @GetMapping("/relatorios")
    public String relatorios(
            @RequestParam(value = "filial", required = false) String filial,
            @RequestParam(value = "inicio", required = false) String inicioStr,
            @RequestParam(value = "fim", required = false) String fimStr,
            Model model) {
        
        java.time.LocalDate inicio = (inicioStr != null && !inicioStr.isEmpty()) ? java.time.LocalDate.parse(inicioStr) : java.time.LocalDate.now().withDayOfMonth(1);
        java.time.LocalDate fim = (fimStr != null && !fimStr.isEmpty()) ? java.time.LocalDate.parse(fimStr) : java.time.LocalDate.now().withDayOfMonth(java.time.LocalDate.now().lengthOfMonth());

        List<Despesa> despesas;
        if (filial != null && !filial.isEmpty()) {
            despesas = despesaRepository.findByFilialAndDataBetween(filial, inicio, fim);
        } else {
            despesas = despesaRepository.findByDataBetween(inicio, fim);
        }
        
        model.addAttribute("despesas", despesas);
        model.addAttribute("filialSelecionada", filial);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        
        List<String> filiais = despesaRepository.findAll().stream()
                .map(Despesa::getFilial)
                .filter(f -> f != null && !f.isEmpty())
                .distinct()
                .toList();
        model.addAttribute("filiais", filiais);

        return "relatorios";
    }
}
