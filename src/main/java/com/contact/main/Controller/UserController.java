package com.contact.main.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.contact.main.dao.ContactRepo;
import com.contact.main.dao.UserRepo;
import com.contact.main.entities.Contact;
import com.contact.main.entities.User;
import com.contact.main.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping(value = "/user")
public class UserController {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ContactRepo contactRepo;

//adding common data 
	@ModelAttribute
	public void addCommonData(Model m,Principal p) {
		m.addAttribute("title", "Dashboard - Contact Manager");
		
		String userName = p.getName();
		System.out.println("user name: "+userName);
		
//getting the user name which is email
		User user = userRepo.getUserByUserName(userName);
		System.out.println("USER: "+user);
		
		m.addAttribute("user",user);
	}
	
//dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		System.out.println("user dashboard handler");
		
		return "normal/user_index";
	}
	
//add contact handler
	@GetMapping("/add_contact")
	public String addContactForm(Model model) {
		System.out.println("add contact form handler");
		model.addAttribute("title", "Add Contact - Contact Manager");
		model.addAttribute("contact",new Contact());
		return"normal/add_contact";
	}
	
//	processing add contact form data
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact ,BindingResult bindingResult, Principal principal
			,@RequestParam("image") MultipartFile file,HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepo.getUserByUserName(name);

		//image file processing....
			if (file.isEmpty()){
				String gender = contact.getGender();
				if (gender.contains("male")) {
					contact.setImage("boy.svg");
				}if(gender.contains("female")) {
					contact.setImage("girl.svg");
				}if(gender.contains("other")) {
					contact.setImage("other.svg");
				}
			}else {
		//upload process here....
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image uploaded successfully.....");
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			
			this.userRepo.save(user);
			System.out.println("contact added successfully....");
			
			//success message
			session.setAttribute("message", new Message("contact is added", "success"));
		} catch (Exception e) {
			System.out.println("Error:"+e.getMessage());
			e.printStackTrace();
			
			//error message
			session.setAttribute("message", new Message("something went wrong! try again...", "danger"));
		}
		return "normal/add_contact";
	}
	
//	showing contact details
									//bug here if not contact it still show pages....
	@GetMapping("/show_contact/{page}")//pagination showing per page 4=n contacts and current page=0
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal,Contact contact) {
		model.addAttribute("title", "Show Contact - Contact Manager");
		
		if (page==-1) {
			return"normal/showcontact";
		}
//get contact list and display it
		String userName = principal.getName(); //email id of user
		User user = this.userRepo.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 3);// have current and contact per page
		Page<Contact> contacts = this.contactRepo.findContactByUser(user.getId(),pageable);
		
		model.addAttribute("contacts",contacts);//list of contact
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/showcontact";
	}
	
//display particular contact details 
	@GetMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		System.out.println(cid);
		Optional<Contact> optionalContact = this.contactRepo.findById(cid);
		Contact contact = optionalContact.get();
		
		String username = principal.getName();
		User user = this.userRepo.getUserByUserName(username);
//check condition not see other contacts from other user
		if (user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName()+" "+contact.getSecondName());
		}
		
		return "normal/contactDetails";
	}
	
//delete contact handler
	@RequestMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model,HttpSession session) {
		
		Contact contact = this.contactRepo.findById(cid).get();
		
//check condition
		System.out.println("contact"+contact);
		if (contact.getCid()==cid) {
			this.contactRepo.deleteByIdCustom(cid);
		}
		session.setAttribute("message", new Message("Contact deleted successfully....", "success"));
		return "redirect:/user/show_contact/0";
	}
	
//update form
	@PostMapping("/updateContact/{cid}")
	public String updateForm(Model model,@PathVariable("cid") int cid) {
		model.addAttribute("title", "Update_contact-Contact Manager");
		Contact contact = this.contactRepo.findById(cid).get();
		model.addAttribute("contact",contact);
		return "normal/updateForm";
	}
	
//update form data handler
	@PostMapping("/process-update")
	public String updatedata(@ModelAttribute("contact") Contact contact, BindingResult bindingResult,@RequestParam("image") MultipartFile file
			,Model model, Principal principal,HttpSession session) {
		try {
		//old contact details
			Contact oldcontactDetails = this.contactRepo.findById(contact.getCid()).get();

			if (!file.isEmpty()) {//if not empty
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			}else {
				contact.setImage(oldcontactDetails.getImage());
			}
			User user =this.userRepo.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepo.save(contact);
		//setting message for 
			session.setAttribute("message", new Message("Updated Successfully...", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
}
