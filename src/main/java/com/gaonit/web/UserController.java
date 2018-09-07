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
		
		if (!user.matchPassword(password)) {
			return "redirect:/users/loginForm";
		}
		
		session.setAttribute(HttpSessionUtils.USER_SESSION_KEY, user);
		System.out.println(user);
		return "redirect:/";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute(HttpSessionUtils.USER_SESSION_KEY);
		
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
	public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
		System.out.println("시작: " + id);
		
		if (!HttpSessionUtils.isLoginUser(session)) {
			return "redirect:/users/loginForm";
		}
		
		User sessionedUser = HttpSessionUtils.getUserFormSession(session);
		
		System.out.println("이름: " + sessionedUser.getName());
		if (!id.equals(sessionedUser.getId())) {
			throw new IllegalStateException("자신의 정보만 수정할 수 있습니다.");
		}
		
		 
		System.out.println("수정: " + id);
		//Optional<User> user = userRepository.findById(id);
		User user = userRepository.getOne(sessionedUser.getId()); 
		System.out.println(user);
		
		model.addAttribute("user", user);
		return "/user/updateForm";
	}
	
	@PutMapping("/{id}")
	public String update(@PathVariable Long id, User updatedUser, HttpSession session) {
		if (!HttpSessionUtils.isLoginUser(session)) {
			return "redirect:/users/loginForm";
		}
		
		User sessionedUser = HttpSessionUtils.getUserFormSession(session);
		
		if (!sessionedUser.matchId(id)) {
			throw new IllegalStateException("자신의 정보만 수정할 수 있습니다.");
		}		
		
		User user = userRepository.getOne(id);
		
		user.update(updatedUser);
		
		userRepository.save(user);
		
		return "redirect:/users";
	}

}
