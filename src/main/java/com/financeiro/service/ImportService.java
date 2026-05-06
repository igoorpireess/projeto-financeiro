package com.financeiro.service;

import com.financeiro.model.Categoria;
import com.financeiro.model.Despesa;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.DespesaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ImportService {

    private final DespesaRepository despesaRepository;
    private final CategoriaRepository categoriaRepository;

    public ImportService(DespesaRepository despesaRepository, CategoriaRepository categoriaRepository) {
        this.despesaRepository = despesaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public int importarArquivo(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.toLowerCase().endsWith(".csv")) {
            return importarCSV(file);
        } else {
            return importarExcel(file);
        }
    }

    private int importarCSV(MultipartFile file) throws IOException {
        List<Despesa> despesas = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] columns = line.split(",");
                if (columns.length >= 5) {
                    try {
                        Despesa despesa = new Despesa();
                        despesa.setData(LocalDate.parse(columns[0].trim(), formatter));
                        despesa.setDescricao(columns[1].trim());
                        
                        String valorStr = columns[2].trim().replace("R$", "").replace(".", "").replace(",", ".");
                        despesa.setValor(new BigDecimal(valorStr));
                        
                        String nomeCat = columns[3].trim();
                        Categoria cat = findOrCreateCategoria(nomeCat);
                        despesa.setCategoria(cat);
                        
                        despesa.setFilial(columns[4].trim());
                        despesas.add(despesa);
                    } catch (Exception e) {
                        System.err.println("Erro na linha CSV: " + line);
                    }
                }
            }
        }
        if (!despesas.isEmpty()) despesaRepository.saveAll(despesas);
        return despesas.size();
    }

    private int importarExcel(MultipartFile file) throws IOException {
        List<Despesa> despesas = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) rows.next(); // Skip header

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                try {
                    Despesa despesa = new Despesa();
                    
                    Cell dateCell = currentRow.getCell(0);
                    if (dateCell != null && dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                        despesa.setData(dateCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    } else {
                        despesa.setData(LocalDate.now());
                    }

                    despesa.setDescricao(getCellValueAsString(currentRow.getCell(1)));
                    
                    Cell valCell = currentRow.getCell(2);
                    if (valCell != null) {
                        if (valCell.getCellType() == CellType.NUMERIC) {
                            despesa.setValor(BigDecimal.valueOf(valCell.getNumericCellValue()));
                        } else {
                            String val = valCell.getStringCellValue().replace("R$", "").replace(".", "").replace(",", ".");
                            despesa.setValor(new BigDecimal(val.trim()));
                        }
                    }

                    despesa.setCategoria(findOrCreateCategoria(getCellValueAsString(currentRow.getCell(3))));
                    despesa.setFilial(getCellValueAsString(currentRow.getCell(4)));

                    if (despesa.getValor() != null) {
                        despesas.add(despesa);
                    }
                } catch (Exception e) {
                    System.err.println("Erro linha Excel " + currentRow.getRowNum());
                }
            }
        }
        if (!despesas.isEmpty()) despesaRepository.saveAll(despesas);
        return despesas.size();
    }

    private Categoria findOrCreateCategoria(String nome) {
        if (nome == null || nome.isEmpty()) return null;
        Categoria cat = categoriaRepository.findByNome(nome);
        if (cat == null) {
            cat = new Categoria();
            cat.setNome(nome);
            cat.setMetaMensal(BigDecimal.ZERO);
            cat = categoriaRepository.save(cat);
        }
        return cat;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf(cell.getNumericCellValue());
        return "";
    }
}
