package gr.hua.dit.ds.springbootdemo.service;

import gr.hua.dit.ds.springbootdemo.entity.*;
import gr.hua.dit.ds.springbootdemo.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class InitialDataService {
    private static final int CREATORS_COUNT = 5;
    private static final int SUPPORTERS_COUNT = 10;
    private static final int PROJECTS_PER_CREATOR = 2;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialDataService(UserRepository userRepository,
                            RoleRepository roleRepository,
                            ProjectRepository projectRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    private void createUsersAndRoles() {
        // First ensure all roles exist and store them
        final List<String> rolesToCreate = List.of(
            "ROLE_ADMIN", 
            "ROLE_CREATOR", 
            "ROLE_SUPPORTER"
        );
        
        // Create all roles first
        for (final String roleName : rolesToCreate) {
            if (!roleRepository.findByName(roleName).isPresent()) {
                Role role = new Role(roleName);
                roleRepository.save(role);
            }
        }
        
        // Fetch the ADMIN role after ensuring it exists
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new RuntimeException("Admin role not found after creation"));
        
        // Create admin user if it doesn't exist
        if (!userRepository.findByUsername("admin").isPresent()) {
            User adminUser = new User("admin", "admin@crowdfund.com", 
                                   passwordEncoder.encode("1234"),
                                   "Admin",
                                   "Admin");
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);
            userRepository.save(adminUser);
        }
    }

    @Transactional
    private void createCreatorUsers() {
        Role creatorRole = roleRepository.findByName("ROLE_CREATOR")
                .orElseThrow(() -> new RuntimeException("Creator role not found"));

        // Create multiple creators
        for (int i = 1; i <= CREATORS_COUNT; i++) {
            String username = "creator" + i;
            // Skip if creator already exists
            if (userRepository.findByUsername(username).isPresent()) {
                continue;
            }

            // Create user with creator role
            User user = new User(
                username,
                "creator" + i + "@crowdfund.com",
                passwordEncoder.encode("1234"),
                "Creator" + i,
                "Last" + i
            );
            Set<Role> roles = new HashSet<>();
            roles.add(creatorRole);
            user.setRoles(roles);
            userRepository.save(user);
        }
    }

    @Transactional
    private void createSupporterUsers() {
        try {
            System.out.println("Looking for ROLE_SUPPORTER...");
            Role supporterRole = roleRepository.findByName("ROLE_SUPPORTER")
                    .orElseThrow(() -> new RuntimeException("Supporter role not found"));
            System.out.println("Found ROLE_SUPPORTER, proceeding with supporter creation");

            // Create multiple supporters
            for (int i = 1; i <= SUPPORTERS_COUNT; i++) {
                String username = "supporter" + i;
                System.out.println("Attempting to create supporter: " + username);
                
                // Skip if supporter already exists
                if (userRepository.findByUsername(username).isPresent()) {
                    System.out.println("Supporter " + username + " already exists, skipping...");
                    continue;
                }

                try {
                    // Create user with supporter role
                    User user = new User(
                        username,
                        "supporter" + i + "@crowdfund.com",
                        passwordEncoder.encode("1234"),
                        "Supporter" + i,
                        "Last" + i
                    );
                    Set<Role> roles = new HashSet<>();
                    roles.add(supporterRole);
                    user.setRoles(roles);
                    System.out.println("Saving user for " + username);
                    userRepository.save(user);
                    System.out.println("Successfully created supporter: " + username);
                } catch (Exception e) {
                    System.err.println("Error creating individual supporter " + username + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in createSupporters method: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    private void createProjects() {
        try {
            List<User> creators = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_CREATOR")))
                .toList();
            System.out.println("Found " + creators.size() + " creators for project creation");
            
            for (User user : creators) {
                System.out.println("Creating projects for user: " + user.getUsername());
                for (int i = 1; i <= PROJECTS_PER_CREATOR; i++) {
                    try {
                        Project project = new Project();
                        project.setTitle("Project " + i + " by " + user.getUsername());
                        project.setDescription("Description for project " + i + " by " + user.getUsername());
                        project.setFundingGoal(10000.0);
                        project.setCurrentFunding(0.0);
                        project.setDeadline(LocalDateTime.now().plusMonths(3));
                        project.setIsApproved(false);
                        project.setUser(user);
                        Project savedProject = projectRepository.save(project);
                        System.out.println("Created project: " + savedProject.getTitle());
                    } catch (Exception e) {
                        System.err.println("Error creating project " + i + " for user " + user.getUsername() + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in createProjects: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void setup() {
        try {
            System.out.println("=== Starting Initial Data Setup ===");
            
            // Create roles and admin first
            System.out.println("Creating roles and admin...");
            this.createUsersAndRoles();
            
            // Create creators if we don't have enough
            long creatorCount = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_CREATOR")))
                .count();
            System.out.println("Current creator count: " + creatorCount + ", target: " + CREATORS_COUNT);
            if (creatorCount < CREATORS_COUNT) {
                System.out.println("Creating creators...");
                this.createCreatorUsers();
            }
            
            // Create supporters if we don't have enough
            long supporterCount = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_SUPPORTER")))
                .count();
            System.out.println("Current supporter count: " + supporterCount + ", target: " + SUPPORTERS_COUNT);
            if (supporterCount < SUPPORTERS_COUNT) {
                System.out.println("Creating supporters...");
                this.createSupporterUsers();
            }
            
            // Create projects
            long projectCount = projectRepository.count();
            long expectedProjectCount = CREATORS_COUNT * PROJECTS_PER_CREATOR;
            System.out.println("Current project count: " + projectCount + ", expected: " + expectedProjectCount);
            if (projectCount < expectedProjectCount) {
                System.out.println("Creating projects...");
                this.createProjects();
            }
            
            System.out.println("=== Initial Data Setup Complete ===");
        } catch (Exception e) {
            System.err.println("Error during setup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
