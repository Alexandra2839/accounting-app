package com.cydeo.controller;

import com.cydeo.dto.CategoryDto;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    public String showCategoryList(Model model) {
        model.addAttribute("categories", categoryService.listAllCategoriesByCompany());
        return "/category/category-list";
    }

    @GetMapping("/create")
    public String showCreateCategoryPage(Model model) {
        model.addAttribute("newCategory", new CategoryDto());
        return "/category/category-create";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute("newCategory") CategoryDto categoryDto, Model model) {
        categoryService.save(categoryDto);
        return "redirect:/categories/list";
    }

    @GetMapping("/update/{id}")
    public String showPageWithEditCategory(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        return "/category/category-update";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute("category") CategoryDto categoryDto) {

        categoryService.update(categoryDto);
        return "redirect:/categories/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.delete(categoryService.findById(id));
        return "redirect:/categories/list";
    }
}
