package com.cydeo.controller;

import com.cydeo.dto.UserDto;
import com.cydeo.service.RoleService;
import com.cydeo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping("/list")
    public String listUsers(Model model){
        model.addAttribute("users", userService.listAllUsers() );
        return "/user/user-list";
    }

    @GetMapping("/update/{id}")
    public String editUser(@PathVariable("id") Long id, Model model){

        model.addAttribute("user", userService.findById(id));
        model.addAttribute("userRoles", roleService.listAllRoles());
//        model.addAttribute("companies", companyService.listAllServices());
        return "/user/user-update";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute ("user") UserDto user, Model model){

        model.addAttribute("user", userService.findById(id));
        model.addAttribute("userRoles", roleService.listAllRoles());
//        model.addAttribute("companies", companyService.listAllServices());

        userService.update(user);
        return "redirect:/users/list";
    }

    @GetMapping("/create")
    public String createUser(Model model){

        model.addAttribute("newUser", new UserDto());
        model.addAttribute("userRoles", roleService.listAllRoles());
//        model.addAttribute("companies", companyService.listAllServices());

        return "user/user-create";
    }

    @PostMapping("/create")
    public String saveUser(@ModelAttribute ("newUser") UserDto user, Model model){

        model.addAttribute("userRoles", roleService.listAllRoles());
//        model.addAttribute("companies", companyService.listAllServices());

        userService.save(user);


        return "redirect:/users/list";
    }



}
