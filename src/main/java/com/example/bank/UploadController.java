package com.example.bank;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class UploadController {

    @Autowired
    private ExtractService extractService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload-csv-file")
    public String uploadCSVFile(@RequestParam("file") MultipartFile file, Model model) {

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {
            // parse CSV file to create a list of `Extract` objects
            try {
                List<Extract> extracts;
                extracts = extractService.read(file);

                // save users list on model
                model.addAttribute("extracts", extracts);
                model.addAttribute("status", true);
                model.addAttribute("somaRio", extractService.categorizeRio(extracts));
                model.addAttribute("somaSaquarema", extractService.categorizeSaquarema(extracts));
                model.addAttribute("somaMercado", extractService.categorizeMercado(extracts));
                model.addAttribute("somaPessoal", extractService.categorizePessoal(extracts));
                model.addAttribute("somaCartao", extractService.categorizeCartao(extracts));

                // TODO: save users in DB?

            } catch (Exception ex) {
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }

        }

        return "file-upload-status";
    }
}

