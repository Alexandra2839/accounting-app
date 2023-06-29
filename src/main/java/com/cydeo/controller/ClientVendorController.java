package com.cydeo.controller;

import com.cydeo.dto.ClientVendorDto;
import com.cydeo.enums.ClientVendorType;
import com.cydeo.service.ClientVendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@Controller
@RequestMapping("/clientVendors")
public class ClientVendorController {
    private final ClientVendorService clientVendorService;

    public ClientVendorController(ClientVendorService clientVendorService) {
        this.clientVendorService = clientVendorService;
    }

    @GetMapping("/list")
    public String listOfClientVendors(Model model){
        model.addAttribute("clientVendors", clientVendorService.findAll());
        return "/clientVendor/clientVendor-list";
    }
    @GetMapping("/create")
    public String showCreateClientVendorForm(Model model){

        ClientVendorType[] enumValues = ClientVendorType.values();
        ArrayList<ClientVendorType> list = new ArrayList<>(Arrays.asList(enumValues));


        model.addAttribute("newClientVendor", new ClientVendorDto());
        model.addAttribute("clientVendorTypes", list);
        return "/clientVendor/clientVendor-create";

    }

    @PostMapping("/create")
    public String createClientVendor(@ModelAttribute("newClientVendor")ClientVendorDto clientVendorDto, Model model){

        clientVendorService.save(clientVendorDto);


        return "redirect:/clientVendors/list";
    }
    @GetMapping("/update/{id}")
    public String editClientVendor(@PathVariable Long id, Model model){
        model.addAttribute("clientVendor", clientVendorService.findById(id));
        model.addAttribute("clientVendorTypes", ClientVendorType.values());

        return "/clientVendor/clientVendor-update";
    }
    @PostMapping("/update/{id}")
    public String updateClientVendor(@PathVariable Long id, @ModelAttribute("clientVendor") ClientVendorDto clientVendorDto, Model model){
        model.addAttribute("clientVendor", clientVendorService.findById(id));


        clientVendorService.update(clientVendorDto);
        return "redirect:/clientVendors/list";
    }
    @GetMapping("/delete/{id}")
    public String deleteClientVender(@PathVariable Long id){
        clientVendorService.delete(clientVendorService.findById(id));
        return "redirect:/clientVendors/list";
    }
}
