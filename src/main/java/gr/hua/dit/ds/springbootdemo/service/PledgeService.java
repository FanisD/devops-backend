package gr.hua.dit.ds.springbootdemo.service;

import gr.hua.dit.ds.springbootdemo.entity.Pledge;
import gr.hua.dit.ds.springbootdemo.entity.Project;
import gr.hua.dit.ds.springbootdemo.entity.User;
import gr.hua.dit.ds.springbootdemo.repository.PledgeRepository;
import gr.hua.dit.ds.springbootdemo.repository.ProjectRepository;
import gr.hua.dit.ds.springbootdemo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PledgeService {
    @Autowired
    private PledgeRepository pledgeRepository;
    
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Pledge createPledge(Pledge pledge, Long userId) {
        // Get the user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        // Get and validate the project
        Project project = projectRepository.findById(pledge.getProject().getId())
            .orElseThrow(() -> new RuntimeException("Project not found"));
            
        if (!project.getIsApproved()) {
            throw new RuntimeException("Cannot pledge to unapproved project");
        }
        
        if (project.getDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Project deadline has passed");
        }

        // Set up the pledge
        pledge.setUser(user);
        pledge.setPledgeDate(LocalDateTime.now());
        
        // Update project and save
        project.addPledge(pledge);
        projectRepository.save(project);
        return pledgeRepository.save(pledge);
    }

    @Transactional
    public List<Pledge> getProjectPledges(Integer projectId) {
        return pledgeRepository.findByProjectId(projectId);
    }

    @Transactional
    public List<Pledge> getUserPledges(Long userId) {
        return pledgeRepository.findByUserId(userId);
    }

    @Transactional
    public void deletePledge(Integer pledgeId) {
        pledgeRepository.deleteById(pledgeId);
    }
} 