package com.gaonit.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gaonit.domain.User;
import com.gaonit.domain.UserRepository;

@Controller
@RequestMapping("/users")
public class UserController {
	private List<User> users = new ArrayList<User>();
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/loginForm")
	public String loginForm() {
		return "/user/login";
	}
	
	@PostMapping("/login")
	public String login(String userId, String password, HttpSession session) {
		User user = userRepository.findByUserId(userId);
		
		if (user == null) {
			return "redirect:/users/loginForm";
		}
		if (!password.equals(user.getPassword())) {
			return "redirect:/users/loginForm";
		}
		
		session.setAttribute("user", user);
		System.out.println(user);
		return "redirect:/";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");
		
		return "redirect:/";
	}
	
	@GetMapping("/form")
	public String form() {
		System.out.println("회원가입");
		return "/user/form";
		//return new ModelAndView("/user/form");
	}
	
	@PostMapping("")
	public String create(User user) {		
		System.out.println("user : " + user);
		//users.add(user);
		userRepository.save(user);
		return "redirect:/users";
	}
	
	@GetMapping("")
	public String list(Model model) {
		model.addAttribute("users", userRepository.findAll());
		return "/user/list";
	}
	
	@GetMapping("/{id}/form")
	public String updateForm(@PathVariable Long id, Model model) {
		
		System.out.println("수정: " + id);
		//Optional<User> user = userRepository.findById(id);
		User user = userRepository.getOne(id); 
		System.out.println(user);
		
		model.addAttribute("user", user);
		return "/user/updateForm";
	}
	
	@PutMapping("/{id}")
	public String update(@PathVariable Long id, User newUser) {
		User user = userRepository.getOne(id);
		
		user.update(newUser);
		
		userRepository.save(user);
		
		return "redirect:/users";
	}

}
