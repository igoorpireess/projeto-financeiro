package com.financeiro.controller;

import com.financeiro.model.Despesa;
import com.financeiro.repository.DespesaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    private final DespesaRepository despesaRepository;

    public DashboardController(DespesaRepository despesaRepository) {
        this.despesaRepository = despesaRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(
            @RequestParam(value = "inicio", required = false) String inicioStr,
            @RequestParam(value = "fim", required = false) String fimStr,
            Model model) {
        
        java.time.LocalDate inicio = (inicioStr != null && !inicioStr.isEmpty()) ? java.time.LocalDate.parse(inicioStr) : java.time.LocalDate.now().withDayOfMonth(1);
        java.time.LocalDate fim = (fimStr != null && !fimStr.isEmpty()) ? java.time.LocalDate.parse(fimStr) : java.time.LocalDate.now().withDayOfMonth(java.time.LocalDate.now().lengthOfMonth());

        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("totalRegistros", despesaRepository.count());
        model.addAttribute("somaTotal", despesaRepository.sumValorInRange(inicio, fim));
        
        List<Object[]> porFilial = despesaRepository.findTotalByFilialInRange(inicio, fim);
        model.addAttribute("dadosFilial", porFilial);

        List<Object[]> porCategoria = despesaRepository.findTotalByCategoriaInRange(inicio, fim);
        model.addAttribute("dadosCategoria", porCategoria);

        // Transações Recentes
        List<Despesa> recentes = despesaRepository.findByDataBetween(inicio, fim);
        model.addAttribute("recentes", recentes.stream().limit(8).toList());

        return "dashboard";
    }
}
