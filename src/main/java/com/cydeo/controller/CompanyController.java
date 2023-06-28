package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public String listCompanies(Model model){

        model.addAttribute("companies", companyService.listAllCompanies());

        return "/company/company-list";

    }

    @GetMapping("/update/{id}")
    public String editCompany(@PathVariable("id") Long id, Model model){

        model.addAttribute("companies", companyService.listAllCompanies());
        model.addAttribute("company", companyService.findById(id));

        return "/company/company-update";
    }

    @GetMapping("/create")
    public String createCompany(Model model){

        model.addAttribute("company", new CompanyDto());
        model.addAttribute("companies", companyService.listAllCompanies());

        return "/company/company-create";

    }

    @PostMapping("/activate/{id}")
    public String activateCompany(@PathVariable("id") Long id, Model model){

        model.addAttribute("companies", companyService.listAllCompanies());
        model.addAttribute("company", companyService.findById(id));

        companyService.activateCompanyById(id);

        return "redirect:/companies";

    }

    @PostMapping("/deactivate/{id}")
    public String deactivateCompany(@PathVariable("id") Long id, Model model){

        model.addAttribute("companies", companyService.listAllCompanies());
        model.addAttribute("company", companyService.findById(id));

        companyService.deactivateCompanyById(id);

        return "redirect:/companies";

    }



}
