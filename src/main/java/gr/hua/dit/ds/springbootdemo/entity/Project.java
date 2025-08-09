package gr.hua.dit.ds.springbootdemo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import gr.hua.dit.ds.springbootdemo.entity.ProjectUpdate;

@Entity
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id"
)
public class Project {

    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer id;

    // Title
    @Column
    private String title;

    // Description
    @Column(length = 1000)
    private String description;

    // Funding goal
    @Column
    private Double fundingGoal;

    // Current funding
    @Column
    private Double currentFunding = 0.0;

    // Deadline
    private LocalDateTime deadline;

    // Approved
    @Column
    private Boolean isApproved = false;

    // Creator
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Pledges
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Pledge> pledges;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectUpdate> updates = new ArrayList<>();

    public Project() {
    }

    public Project(String title, String description, Double fundingGoal, LocalDateTime deadline, User user) {
        this.title = title;
        this.description = description;
        this.fundingGoal = fundingGoal;
        this.deadline = deadline;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getFundingGoal() {
        return fundingGoal;
    }

    public void setFundingGoal(Double fundingGoal) {
        this.fundingGoal = fundingGoal;
    }

    public Double getCurrentFunding() {
        return currentFunding;
    }

    public void setCurrentFunding(Double currentFunding) {
        this.currentFunding = currentFunding;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Pledge> getPledges() {
        return pledges;
    }

    public void setPledges(List<Pledge> pledges) {
        this.pledges = pledges;
    }

    public void addPledge(Pledge pledge) {
        if (pledges == null) {
            pledges = new ArrayList<>();
        }
        pledges.add(pledge);
        currentFunding += pledge.getAmount(); // Automatically updates funding
    }

    public List<ProjectUpdate> getUpdates() {
        return updates;
    }

    public void setUpdates(List<ProjectUpdate> updates) {
        this.updates = updates;
    }

    public void addUpdate(ProjectUpdate update) {
        updates.add(update);
        update.setProject(this);
    }

    public void removeUpdate(ProjectUpdate update) {
        updates.remove(update);
        update.setProject(null);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", fundingGoal=" + fundingGoal +
                ", currentFunding=" + currentFunding +
                ", deadline=" + deadline +
                ", isApproved=" + isApproved +
                ", user=" + user +
                ", pledges=" + pledges +
                ", updates=" + updates +
                '}';
    }
}
