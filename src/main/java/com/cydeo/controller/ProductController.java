package com.cydeo.controller;


import com.cydeo.dto.ProductDto;
import com.cydeo.enums.ProductUnit;
import com.cydeo.service.CategoryService;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {

        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.listAllProducts());
        return "product/product-list";
    }

    @GetMapping("/create")
    public String createProduct(Model model) {

        ProductUnit[] enumValues = ProductUnit.values();
        List<ProductUnit> list = new ArrayList<>(Arrays.asList(enumValues));

        model.addAttribute("newProduct", new ProductDto());
        model.addAttribute("productUnits", list);
        model.addAttribute("categories", categoryService.listOfCategories());
        return "product/product-create";
    }

    @PostMapping("/create")
    public String saveProduct(@Valid @ModelAttribute("newProduct") ProductDto dto, BindingResult bindingResult, Model model) {
        ProductUnit[] enumValues = ProductUnit.values();
        List<ProductUnit> list = new ArrayList<>(Arrays.asList(enumValues));

        if (productService.isNameExist(dto)) {
            bindingResult.rejectValue("name", " ", "This name already exists.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("productUnits", list);
            model.addAttribute("categories", categoryService.listOfCategories());
            return "product/product-create";
        }

        productService.createProduct(dto);
        return "redirect:/products/list";
    }

    @GetMapping("/update/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model) {
        model.addAttribute("product", productService.findProductById(id));
        model.addAttribute("productUnits", ProductUnit.values());
        model.addAttribute("categories", categoryService.listOfCategories());
        return "product/product-update";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@Valid @ModelAttribute("product") ProductDto dto, BindingResult bindingResult, Model model) {

        if (productService.isNameExist(dto)) {
            bindingResult.rejectValue("name", " ", "This name already exists.");

        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("productUnits", ProductUnit.values());
            model.addAttribute("categories", categoryService.listOfCategories());
            return "product/product-update";
        }

        productService.updateProduct(dto);
        return "redirect:/products/list";
    }

    @GetMapping("delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(productService.findProductById(id));
        return "redirect:/products/list";
    }


}
