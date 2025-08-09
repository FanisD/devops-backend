package gr.hua.dit.ds.springbootdemo.controller;

import gr.hua.dit.ds.springbootdemo.entity.Pledge;
import gr.hua.dit.ds.springbootdemo.service.PledgeService;
import gr.hua.dit.ds.springbootdemo.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping({"/api/pledges", "/pledges"})
public class PledgeController {
    private static final Logger logger = LoggerFactory.getLogger(PledgeController.class);

    @Autowired
    private PledgeService pledgeService;

    @PostMapping // Create pledge (SUPPORTER only)
    @PreAuthorize("hasRole('ROLE_SUPPORTER')")
    public ResponseEntity<?> createPledge(@RequestBody Pledge pledge) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
            Pledge savedPledge = pledgeService.createPledge(pledge, userDetails.getId());
            return ResponseEntity.ok(savedPledge);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/project/{projectId}") // Get project pledges  (PUBLIC)
    public List<Pledge> getProjectPledges(@PathVariable Integer projectId) {
        return pledgeService.getProjectPledges(projectId);
    }

    @GetMapping("/my") // Get current user's pledges (SUPPORTER only)
    @PreAuthorize("hasRole('ROLE_SUPPORTER')")
    public List<Pledge> getMyPledges() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        logger.info("Fetching pledges for user ID: {}", userDetails.getId());
        
        List<Pledge> pledges = pledgeService.getUserPledges(userDetails.getId());
        logger.info("Found {} pledges for user", pledges.size());
        
        return pledges;
    }

    @DeleteMapping("/{id}") // Delete pledge (ADMIN only)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deletePledge(@PathVariable Integer id) {
        pledgeService.deletePledge(id);
        return ResponseEntity.ok().build();
    }
} 