package com.virtu_stock.User;

import java.util.List;
import java.util.UUID;

import com.virtu_stock.Enum.Role;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    @Column(nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Transient
    @NotBlank(message = "First name is required")
    private String firstName;

    @Transient
    private String lastName;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    @Column(name = "role")
    private List<Role> roles;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateFullName();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        updateFullName();
    }

    @PrePersist
    @PreUpdate
    private void updateFullName() {
        if (firstName != null && lastName != null)
            this.fullName = firstName + "|" + lastName;
        else if (firstName != null)
            this.fullName = firstName;
        else
            this.fullName = null;
    }

    @PostLoad
    private void splitFullName() {
        if (fullName != null) {
            String[] parts = fullName.split("\\|", 2);
            this.firstName = parts[0];
            this.lastName = (parts.length > 1) ? parts[1] : "";
        }
    }

}
