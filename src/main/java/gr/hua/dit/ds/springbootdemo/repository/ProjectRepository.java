package gr.hua.dit.ds.springbootdemo.repository;

import gr.hua.dit.ds.springbootdemo.entity.Project;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "project")
@Hidden
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    //Optional<Project> findByTitle(String title);
    List<Project> findByIsApproved(Boolean isApproved);
    List<Project> findByUserId(Long userId);
} 