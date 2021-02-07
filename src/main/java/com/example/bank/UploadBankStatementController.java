package com.example.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class UploadBankStatementController {

    @Autowired
    private BankStatementService bankStatementService;

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
                List<Transaction> transactions;
                transactions = bankStatementService.read(file);

                // save users list on model
                model.addAttribute("transactions", transactions);
                model.addAttribute("status", true);
                model.addAttribute("amountRio", bankStatementService.categorizeRio(transactions));
                model.addAttribute("amountSaquarema", bankStatementService.categorizeSaquarema(transactions));
                model.addAttribute("amountSupermarket", bankStatementService.categorizeSupermarket(transactions));
                model.addAttribute("amountPersonal", bankStatementService.categorizePersonal(transactions));
                model.addAttribute("amountCreditCard", bankStatementService.categorizeCreditCard(transactions));

                // TODO: save users in DB?

            } catch (Exception ex) {
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }

        }

        return "file-upload-status";
    }
}

