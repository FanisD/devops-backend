package gr.hua.dit.ds.springbootdemo.service;

import gr.hua.dit.ds.springbootdemo.entity.Project;
import gr.hua.dit.ds.springbootdemo.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional
    public List<Project> getApprovedProjects() {
        return projectRepository.findByIsApproved(true);
    }

    @Transactional
    public List<Project> getPendingProjects() {
        return projectRepository.findByIsApproved(false);
    }

    @Transactional
    public List<Project> getUserProjects(Long userId) {
        return projectRepository.findByUserId(userId);
    }

    @Transactional
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Transactional
    public Project approveProject(Integer projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setIsApproved(true);
        return projectRepository.save(project);
    }

    @Transactional
    public Project rejectProject(Integer projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
        return project;
    }

    @Transactional
    public void deleteProject(Integer projectId) {
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public Project getProject(Integer projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found"));
    }
} 