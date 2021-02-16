package com.example.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping(path = "/{owner}")
public class UploadStatementController {

    @Autowired
    private Environment env;

    @Autowired
    private BankStatementService bankStatementService;

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
                List<AldaTransaction> aldaTransactions;
                aldaTransactions = bankStatementService.read(file, owner);

                // save users list on model
                model.addAttribute("transactions", aldaTransactions);
                model.addAttribute("status", true);
                model.addAttribute("amountRio", bankStatementService.categorizeRio(aldaTransactions));
                model.addAttribute("amountSaquarema", bankStatementService.categorizeSaquarema(aldaTransactions));
                model.addAttribute("amountSupermarket", bankStatementService.categorizeSupermarket(aldaTransactions));
                model.addAttribute("amountPersonal", bankStatementService.categorizePersonal(aldaTransactions));
                model.addAttribute("amountCreditCard", bankStatementService.categorizeCreditCard(aldaTransactions));

                // TODO: save users in DB?

            } catch (Exception ex) {
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }

        }

        return "file-upload-status";
    }
}

