package com.demo.controllers.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.demo.DTO.ProductDTO;
import com.demo.DTO.UserDTO;
import com.demo.models.ERole;
import com.demo.models.Role;
import com.demo.models.User;
import com.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.demo.DTO.CategoryDTO;
import com.demo.service.CategoryService;
import com.demo.service.ProductService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@GetMapping("")
	public String homeRedirect () {
		return "redirect:/page/1";
	}

	@GetMapping("/page/{id}")
	public String home (Model model, HttpSession session, 
				@PathVariable("id") Integer id) {
		
		List<CategoryDTO> categoryDTOs = categoryService.getAllCategory()
				.stream().map(e -> e.toDTO()).collect(Collectors.toList());

		List<ProductDTO> listAllProductDTO = productService.getAllProduct().stream()
				.map(e -> e.toDTO()).collect(Collectors.toList()); 
		
		int pageNumber = listAllProductDTO.size() / 8 + 1;
		
		List<List<ProductDTO>> listPage = new ArrayList<List<ProductDTO>>();
		int index = 0;
		
		for (int i = 0; i < pageNumber; i++) {
			List<ProductDTO> res;
			if (index + 8 > listAllProductDTO.size()) {
				res = listAllProductDTO.subList(index, listAllProductDTO.size());
			} else {
				res = listAllProductDTO.subList(index, index + 8);
			}
			listPage.add(res);
			index += 8;
		}
		if (!listPage.isEmpty()) {
			Collections.shuffle(listPage.get(id - 1));
			model.addAttribute("productDTOs", listPage.get(id - 1));
		}

		model.addAttribute("pageNumbers", pageNumber);
		model.addAttribute("categoryDTOs", categoryDTOs);


		return "/client/home";
	}
}
