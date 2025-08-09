package gr.hua.dit.ds.springbootdemo.controller;

import gr.hua.dit.ds.springbootdemo.entity.ProjectUpdate;
import gr.hua.dit.ds.springbootdemo.entity.Project;
import gr.hua.dit.ds.springbootdemo.entity.User;
import gr.hua.dit.ds.springbootdemo.repository.ProjectUpdateRepository;
import gr.hua.dit.ds.springbootdemo.repository.ProjectRepository;
import gr.hua.dit.ds.springbootdemo.repository.UserRepository;
import gr.hua.dit.ds.springbootdemo.repository.PledgeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/projects", "/projects"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProjectUpdateController {

    @Autowired
    private ProjectUpdateRepository projectUpdateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PledgeRepository pledgeRepository;

    @GetMapping("/{projectId}/updates")
    public ResponseEntity<List<ProjectUpdate>> getProjectUpdates(@PathVariable Integer projectId) {
        return ResponseEntity.ok(projectUpdateRepository.findByProjectIdOrderByCreatedAtDesc(projectId));
    }

    @PostMapping("/{projectId}/updates")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> createUpdate(
            @PathVariable Integer projectId,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the authenticated user is the project creator
        if (!project.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Only the project creator can post updates");
        }

        ProjectUpdate update = new ProjectUpdate();
        update.setContent(payload.get("content"));
        update.setProject(project);
        update.setCreator(user);

        ProjectUpdate savedUpdate = projectUpdateRepository.save(update);
        return ResponseEntity.ok(savedUpdate);
    }

    @DeleteMapping("/projects/{projectId}/updates/{updateId}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<?> deleteUpdate(
            @PathVariable Integer projectId,
            @PathVariable Integer updateId,
            Authentication authentication) {

        ProjectUpdate update = projectUpdateRepository.findById(updateId)
                .orElseThrow(() -> new RuntimeException("Update not found"));

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the authenticated user is the update creator
        if (!update.getCreator().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Only the update creator can delete it");
        }

        projectUpdateRepository.delete(update);
        return ResponseEntity.ok().build();
    }
} 