package fhir.controller;

import fhir.models.User;
import fhir.repository.UserRepository;
import fhir.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class HomeController {
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public HomeController(MyUserDetailsService myUserDetailsService){
        this.myUserDetailsService = myUserDetailsService;
    }

    @Autowired
    UserRepository userRepo;
    
    /**
     * 
     * @return
     */
    @GetMapping("/")
    public String index(){
        return "home";
    }
    
    /**
     * 
     * @param model
     * @return
     */
    @GetMapping("/admin")
    public String admin(@ModelAttribute User users, Model model){
        model.addAttribute("userslist", userRepo.findAll());
        model.addAttribute("users", users);
        return "admin";
    }

    /**
     * 
     * @param
     * @return
     */
    @PostMapping("/admin")
    public String processRegister(@Valid User users, BindingResult bindingResult, RedirectAttributes ra, Model model) {
        //Check if user exist
        if(myUserDetailsService.userExists(users.getUsername())){
            bindingResult.addError(new FieldError("users", "username", "Username is already exist"));
        }

        //check if the passwords match

        if(bindingResult.hasErrors()){
            model.addAttribute("userslist", userRepo.findAll());
            return "admin";
        }
        ra.addFlashAttribute("success_message", "Success! Neuer User ist hinzugefügt");
        userRepo.save(users);
        return "redirect:/";
    }

    /**
     *
     * @return
     */
    @GetMapping("edit/{id}")
    public String editUser(@PathVariable(value = "id") int id, Model model){
        //get emp
        User users = userRepo.getById(id);

        model.addAttribute("users", users);
        return "edit";
    }

    /**
     *
     * @return
     */
    @PostMapping("/saveUser")
    public String saveUser(@Valid @ModelAttribute("users") User users, BindingResult bindingResult, Model model){
        if(myUserDetailsService.userExists(users.getUsername())){
            bindingResult.addError(new FieldError("users", "username", "Username is already exist"));
        }
        if(bindingResult.hasErrors()){
            return "edit";
        }
        userRepo.save(users);
        return "redirect:/admin";
    }
    
    /**
     * 
     * @return
     */
    @GetMapping("/login")
    public String login(){ return "login";}
    
    /**
     * 
     * @return
     */
    @GetMapping ("/logout")
    public String logout(){
        return "login";
    }
    
    /**
     * 
     * @return
     */
    @GetMapping("/newdatenhochladen")
    public String newdaten(){
        return "newdatenhochladen";
    }
/*
    @GetMapping ("/archivedfiles")
    public String archivedfiles(){
        return "archivedfiles";
    }*/
}
