package gr.hua.dit.ds.springbootdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
public class MainController {

    @GetMapping("/")
    public String home(Model model) {
        return "home"; // Home page
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminDashboard() {
        return "admin_dashrdboa"; // Admin only page
    }

    @GetMapping("/creator")
    @PreAuthorize("hasRole('ROLE_CREATOR')")
    public String creatorDashboard() {
        return "creator_dashboard"; // Creator only page        
    }

    @GetMapping("/supporter")
    @PreAuthorize("hasRole('ROLE_SUPPORTER')")
    public String supporterDashboard() {
        return "supporter_dashboard"; // Supporter only page    
    }
} 