package com.example.statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Controller
@RequestMapping//(path = "/{owner}")
public class UploadStatementController {

    @Autowired(required = false)
    private OwnerRepository ownerRepository;

    @Autowired(required = false)
    private BankRepository bankRepository;

    @Autowired(required = false)
    private StatementRepository statementRepository;

    @Autowired
    private StatementService statementService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload-csv-file")
    public String uploadCSVFile(@RequestParam("ownerId") String ownerId, @RequestParam("bankId") String bankId, @RequestParam("file") MultipartFile file, Model model) {
        Owner owner = null;
        Bank bank = null;

        // validate OwnerID
        if (ownerId.isEmpty()) {
            model.addAttribute("message", "Please enter an ID.");
            model.addAttribute("status", false);
            return "file-upload-status";
        } else {
            var ownerOptional = ownerRepository.findById(ownerId);
            owner = ownerOptional.get();
            if (owner == null) {
                model.addAttribute("message", "The ID is not valid.");
                model.addAttribute("status", false);
                return "file-upload-status";
            }
        }
        model.addAttribute("owner", owner);

        // validate BankID
        if (bankId.isEmpty()) {
            model.addAttribute("message", "Select a Bank.");
            model.addAttribute("status", false);
            return "file-upload-status";
        } else {
            var bankOptional = bankRepository.findById(bankId);
            bank = bankOptional.get();
            if (bank == null) {
                model.addAttribute("message", "Bank is not valid.");
                model.addAttribute("status", false);
                return "file-upload-status";
            }
        }
        model.addAttribute("bank", bank);

        // validate file
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a statement (.csv) file to upload.");
            model.addAttribute("status", false);
            return "file-upload-status";
        } //TODO: Handle processing same file

        // parse csv file to create a list of `Extract` objects
        try {
            var transactions = statementService.read(bank, file);
            var sumCategories = statementService.categorize(owner, bank, transactions);
            statementService.save(owner, bank, file.getName(), transactions);

            model.addAttribute("transactions", transactions);
            model.addAttribute("status", true);
            model.addAttribute("categories", sumCategories);

        } catch (Exception ex) {
            model.addAttribute("message", "An error occurred while processing the statement file.\r\n"+ex.getMessage());
            model.addAttribute("status", false);
        }

        return "file-upload-status";
    }
}

