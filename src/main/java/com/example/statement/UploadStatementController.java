package com.example.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping(path = "/{owner}")
public class UploadStatementController {

    @Autowired
    private StatementService statementService;

    @GetMapping("/")
    public String index(@PathVariable String owner) {
        return "index";
    }

    @PostMapping("/upload-csv-file")
    public String uploadCSVFile(@PathVariable String owner, @RequestParam("file") MultipartFile file, Model model) {
        model.addAttribute("owner", owner);

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {
            // parse CSV file to create a list of `Extract` objects
            try {

                var transactions = statementService.read(file, owner);

                // save users list on model
                model.addAttribute("transactions", transactions);
                model.addAttribute("status", true);

                Map<String, Double> sumCategories = statementService.categorize(owner, transactions);

                model.addAttribute("categories", sumCategories);

                // TODO: save users in DB?

            } catch (Exception ex) {
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }

        }

        return "file-upload-status";
    }
}

