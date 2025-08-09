package gr.hua.dit.ds.springbootdemo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.LocalDateTime;

@Entity
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id"
)
public class Pledge {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    // Amount
    @Column
    private Double amount;

    // Pledge date
    @Column
    private LocalDateTime pledgeDate;

    // Project
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    // User who made the pledge
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructor
    public Pledge() {
    }

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPledgeDate() {
        return pledgeDate;
    }

    public void setPledgeDate(LocalDateTime pledgeDate) {
        this.pledgeDate = pledgeDate;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
} 