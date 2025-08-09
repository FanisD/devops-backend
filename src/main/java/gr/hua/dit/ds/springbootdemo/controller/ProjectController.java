package gr.hua.dit.ds.springbootdemo.controller;

import gr.hua.dit.ds.springbootdemo.entity.Project;
import gr.hua.dit.ds.springbootdemo.entity.User;
import gr.hua.dit.ds.springbootdemo.entity.Pledge;
import gr.hua.dit.ds.springbootdemo.service.ProjectService;
import gr.hua.dit.ds.springbootdemo.service.UserService;
import gr.hua.dit.ds.springbootdemo.service.PledgeService;
import gr.hua.dit.ds.springbootdemo.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/projects", "/projects"})
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private PledgeService pledgeService;

    @GetMapping // List all approved projects (PUBLIC)
    public List<Project> getAllProjects() {
        return projectService.getApprovedProjects();
    }

    @GetMapping("/pending") // List all pending projects (ADMIN only)   
    @PreAuthorize("hasRole('ADMIN')")
    public List<Project> getPendingProjects() {
        return projectService.getPendingProjects();
    }

    @GetMapping("/{id}") // Get project by id (PUBLIC)  
    public Project getProject(@PathVariable Integer id) {
        return projectService.getProject(id);
    }

    @PostMapping // Create project (CREATOR only)   
    @PreAuthorize("hasRole('CREATOR')")
    public Project createProject(@RequestBody Project project) {
        // Get the authenticated user's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Get the user
        User user = userService.getUserByUsername(username);
        
        // Set the user for the project
        project.setUser(user);
        
        // Set initial values
        project.setCurrentFunding(0.0);
        project.setIsApproved(false);
        
        return projectService.saveProject(project);
    }

    @GetMapping("/my") // Get current user's projects (CREATOR only)
    @PreAuthorize("hasRole('CREATOR')")
    public List<Project> getMyProjects() {
        // Get the authenticated user's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Get the user
        User user = userService.getUserByUsername(username);
        
        return projectService.getUserProjects(user.getId());
    }

    @PutMapping("/{id}/approve") // Approve project (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    public Project approveProject(@PathVariable Integer id) {
        return projectService.approveProject(id);
    }

    @PutMapping("/{id}/reject") // Reject project (ADMIN only)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectProject(@PathVariable Integer id) {
        Project rejectedProject = projectService.rejectProject(id);
        return ResponseEntity.ok().body(Map.of(
            "message", "Project '" + rejectedProject.getTitle() + "' has been rejected and deleted",
            "projectId", rejectedProject.getId()
        ));
    }

    @DeleteMapping("/{id}") // Delete project (ADMIN or project owner)
    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    public ResponseEntity<?> deleteProject(@PathVariable Integer id) {
        // Get the authenticated user
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        
        // Get the project
        Project project = projectService.getProject(id);
        
        // Check if user is admin or project owner
        if (!user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN")) 
            && !project.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You can only delete your own projects");
        }
        
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/featured") // List featured (approved) projects (PUBLIC)
    public List<Project> getFeaturedProjects() {
        return projectService.getApprovedProjects();
    }

    @PostMapping("/{id}/pledge") // Create pledge for project (SUPPORTER only)
    @PreAuthorize("hasRole('SUPPORTER')")
    public ResponseEntity<?> pledgeToProject(@PathVariable Integer id, @RequestBody Map<String, Double> payload) {
        try {
            System.out.println("Received pledge request for project " + id + " with payload: " + payload);
            
            // Get the authenticated user's username
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("Authenticated username: " + username);
            
            User user = userService.getUserByUsername(username);
            System.out.println("Found user: " + user.getUsername() + " with ID: " + user.getId());
            
            // Create and save the pledge
            Pledge pledge = new Pledge();
            Double amount = payload.get("amount");
            if (amount == null) {
                throw new IllegalArgumentException("Amount is required");
            }
            pledge.setAmount(amount);
            System.out.println("Set pledge amount: " + amount);
            
            // Get and set the project
            Project project = projectService.getProject(id);
            System.out.println("Found project: " + project.getTitle() + " with ID: " + project.getId());
            pledge.setProject(project);
            
            // Set the user directly
            pledge.setUser(user);
            
            // Save the pledge
            Pledge savedPledge = pledgeService.createPledge(pledge, user.getId());
            System.out.println("Successfully created pledge with ID: " + savedPledge.getId());
            
            return ResponseEntity.ok(savedPledge);
        } catch (Exception e) {
            System.err.println("Error creating pledge: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 