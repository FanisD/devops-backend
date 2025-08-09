package gr.hua.dit.ds.springbootdemo.repository;

import gr.hua.dit.ds.springbootdemo.entity.ProjectUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource(path = "project-updates")
@Hidden
public interface ProjectUpdateRepository extends JpaRepository<ProjectUpdate, Integer> {
    
    List<ProjectUpdate> findByProjectIdOrderByCreatedAtDesc(Integer projectId);

} 