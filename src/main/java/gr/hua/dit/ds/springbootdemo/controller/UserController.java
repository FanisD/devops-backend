package gr.hua.dit.ds.springbootdemo.controller;

import gr.hua.dit.ds.springbootdemo.entity.User;
import gr.hua.dit.ds.springbootdemo.entity.Role;
import gr.hua.dit.ds.springbootdemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
            .map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("firstName", user.getFirstName());
                userMap.put("lastName", user.getLastName());
                userMap.put("email", user.getEmail());
                // Extract role names from the Role objects and ensure we're fetching them eagerly
                userMap.put("roles", user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
                return userMap;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
            .map(existingUser -> {
                // Only update roles if they are provided
                if (updatedUser.getRoles() != null && !updatedUser.getRoles().isEmpty()) {
                    existingUser.setRoles(updatedUser.getRoles());
                    return ResponseEntity.ok(userRepository.save(existingUser));
                }
                return ResponseEntity.badRequest().body("No roles provided in the update request");
            })
            .orElse(ResponseEntity.notFound().build());
    }
} 