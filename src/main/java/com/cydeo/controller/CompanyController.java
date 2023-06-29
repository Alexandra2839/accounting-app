package com.cydeo.controller;

import com.cydeo.dto.CompanyDto;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/list")
    public String listCompanies(Model model) {

        model.addAttribute("companies", companyService.listAllCompanies());

        return "/company/company-list";

    }

    @GetMapping("/update/{id}")
    public String editCompany(@PathVariable("id") Long id, Model model) {

        model.addAttribute("company", companyService.findById(id));

        return "/company/company-update";

    }

    @PostMapping("/update/{id}")
    public String saveUpdatedCompany(@ModelAttribute("company") CompanyDto companyDto,
                                     @PathVariable("id") Long id) {

        companyService.updateById(id, companyDto);

        return "redirect:/companies/list";

    }


    @GetMapping("/create")
    public String createCompany(Model model) {

        model.addAttribute("newCompany", new CompanyDto());

        return "/company/company-create";

    }

    @PostMapping("/create")
    public String saveCompany(@ModelAttribute("newCompany") CompanyDto companyDto) {


        companyService.save(companyDto);

        return "redirect:/companies/list";

    }


    @GetMapping("/activate/{id}")
    public String activateCompany(@PathVariable("id") Long id, Model model) {

        companyService.activateCompanyById(id);

        return "redirect:/companies/list";

    }

    @GetMapping("/deactivate/{id}")
    public String deactivateCompany(@PathVariable("id") Long id, Model model) {


        companyService.deactivateCompanyById(id);

        return "redirect:/companies/list";

    }


}
