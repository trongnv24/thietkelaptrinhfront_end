package com.demo.controllers;

import com.demo.DTO.CategoryDTO;
import com.demo.DTO.ProductDTO;
import com.demo.DTO.UserDTO;
import com.demo.models.ERole;
import com.demo.models.Role;
import com.demo.models.User;
import com.demo.service.CategoryService;
import com.demo.service.ProductService;
import com.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("")
public class AuthController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;
    @GetMapping("/login")
    public String login(Principal principal){
        if(principal!=null){
            Optional<User> existed= Optional.ofNullable(userService.getUserByUsername(principal.getName()));
            if(existed.isPresent()){
                if(existed.get().getRole().getName().equals(ERole.ROLE_ADMIN)){
                    return "redirect:/admin/categories";
                }
                else{
                    return "redirect:/";
                }
            }
        }
        return "client/login";
    }

    @GetMapping("/register")
    public String registerGet (Model model) {

        UserDTO userDTO = new UserDTO();

        List<CategoryDTO> categoryDTOs = categoryService.getAllCategory()
                .stream().map(e -> e.toDTO()).collect(Collectors.toList());

        List<ProductDTO> productDTOS = productService.getAllProduct().stream()
                .map(e -> e.toDTO()).collect(Collectors.toList());

        model.addAttribute("userDTO", userDTO);
        model.addAttribute("categoryDTOs", categoryDTOs);
        model.addAttribute("productDTOs", productDTOS);

        return "/client/register";
    }

    @PostMapping("/register")
    public String registerPost (Model model, @ModelAttribute(name = "userDTO") UserDTO userDTO) {

        userDTO.setRoleDTO((new Role(ERole.ROLE_USER)).toDTO());

        User user0 = userService.getUserByEmail(userDTO.getEmail());
        User user1 = userService.getUserByUsername(userDTO.getUsername());

        if (user0 != null || user1 != null) {
            model.addAttribute("error", "Email or username is existed in system");
            return "/client/register";
        }

        userService.createUser(userDTO.toModel());

        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){

        session.removeAttribute("username");

        return "client/login";
    }
}
