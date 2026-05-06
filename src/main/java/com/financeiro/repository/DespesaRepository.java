package com.financeiro.repository;

import com.financeiro.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    
    @Query("SELECT d.filial as filial, SUM(d.valor) as total FROM Despesa d WHERE d.data BETWEEN :inicio AND :fim GROUP BY d.filial")
    List<Object[]> findTotalByFilialInRange(java.time.LocalDate inicio, java.time.LocalDate fim);

    @Query("SELECT d.categoria.nome as categoria, SUM(d.valor) as total FROM Despesa d WHERE d.data BETWEEN :inicio AND :fim GROUP BY d.categoria.nome")
    List<Object[]> findTotalByCategoriaInRange(java.time.LocalDate inicio, java.time.LocalDate fim);

    @Query("SELECT SUM(d.valor) FROM Despesa d WHERE d.data BETWEEN :inicio AND :fim")
    java.math.BigDecimal sumValorInRange(java.time.LocalDate inicio, java.time.LocalDate fim);

    List<Despesa> findByDataBetween(java.time.LocalDate inicio, java.time.LocalDate fim);
    List<Despesa> findByFilialAndDataBetween(String filial, java.time.LocalDate inicio, java.time.LocalDate fim);
}
