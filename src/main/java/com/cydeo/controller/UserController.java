package com.cydeo.controller;

import com.cydeo.dto.UserDto;
import com.cydeo.service.CompanyService;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;

    public UserController(UserService userService, RoleService roleService, CompanyService companyService) {
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
    }


    @GetMapping("/list")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.listAllUsers());
        return "/user/user-list";
    }

    @GetMapping("/update/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {

        UserDto user = userService.findById(id);
        userService.setOnlyAdmin(user);

        model.addAttribute("user", user);
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies", companyService.listAllCompaniesByLoggedInUser());
        return "/user/user-update";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@Valid @ModelAttribute("user") UserDto user,
                             BindingResult bindingResult, @PathVariable("id") Long id, Model model) {

        if (userService.isEmailExist(user)) {
            bindingResult.rejectValue("username", " ", "A user with this email already exists. Please try different email.");
        }

        if (bindingResult.hasErrors()) {

            model.addAttribute("userRoles", roleService.listAllRoles());
            model.addAttribute("companies", companyService.listAllCompaniesByLoggedInUser());

            return "/user/user-update";
        }

        userService.update(user);
        return "redirect:/users/list";
    }

    @GetMapping("/create")
    public String createUser(Model model) {

        model.addAttribute("newUser", new UserDto());
        model.addAttribute("userRoles", roleService.listAllRoles());
        model.addAttribute("companies", companyService.listAllCompaniesByLoggedInUser());

        return "user/user-create";
    }

    @PostMapping("/create")
    public String saveUser(@Valid @ModelAttribute("newUser") UserDto user, BindingResult bindingResult, Model model) {

        if (userService.isEmailExist(user)) {
            bindingResult.rejectValue("username", " ", "A user with this email already exists. Please try different email.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("userRoles", roleService.listAllRoles());
            model.addAttribute("companies", companyService.listAllCompaniesByLoggedInUser());

            return "/user/user-create";
        }

        userService.save(user);


        return "redirect:/users/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {

        userService.delete(id);

        return "redirect:/users/list";
    }


}
