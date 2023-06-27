package com.contact.main.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.main.dao.UserRepo;
import com.contact.main.entities.User;
import com.contact.main.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepo userRepo;
	
	@GetMapping("/")
	public String home(Model model) {
		System.out.println("home controller...");
		model.addAttribute("title", "Home - Contact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String about(Model model) {
		System.out.println("about controller...");
		model.addAttribute("title", "About - Contact Manager");
		return "about";
	}
	
	@GetMapping("/error")
	public String try1(Model model) {
		System.out.println("error controller...");
		model.addAttribute("title", "Error - Contact Manager");
		return "error";
	}
	
	@GetMapping("/signup")
	public String signup(Model model) {
		System.out.println("signup controller...");
		model.addAttribute("title", "SignUp - Contact Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
//	registration handler
	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("user") User user,BindingResult bresult ,@RequestParam(value = "condition",defaultValue = "false") boolean condition,
			Model model,HttpSession session ){
		
		try {
			if (!condition) {
				System.out.println("Please check the terms and conditions.");
				throw new Exception("Please check the terms and conditions");
			}
			
			if(bresult.hasErrors()) {
				System.out.println("error"+bresult.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("User");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("condition: "+condition);
			System.out.println("User: "+user);
			
			@SuppressWarnings("unused")
			User result = this.userRepo.save(user);

			model.addAttribute("user", new User());
			
			session.setAttribute("message", new Message("Successfully registered", "alert-success"));
			return "login";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	
//	custome login page configure in config it's a handler
	@GetMapping("/signin")
	public String Customelogin(Model model) {
		model.addAttribute("title", "Login-Contact Manager");
		return "login";
	}	
}