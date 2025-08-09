package gr.hua.dit.ds.springbootdemo.repository;

import gr.hua.dit.ds.springbootdemo.entity.Pledge;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "pledge")
@Hidden
public interface PledgeRepository extends JpaRepository<Pledge, Integer> {
    List<Pledge> findByProjectId(Integer projectId);
    List<Pledge> findByUserId(Long userId);
    //List<Pledge> findByProjectIdAndSupporterId(Integer projectId, Integer supporterId);
} 