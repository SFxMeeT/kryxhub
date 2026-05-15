package com.kryxhub.kryxhub.dto;

import com.kryxhub.kryxhub.enums.Role;
import java.util.UUID;

public class AdminUserDto {
    private UUID id;
    private String username;
    private String email;
    private Role role;
    private boolean hasLinkedBank;

    public AdminUserDto(UUID id, String username, String email, Role role, String stripeAccountId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.hasLinkedBank = (stripeAccountId != null && !stripeAccountId.isEmpty());
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isHasLinkedBank() { return hasLinkedBank; }
    public void setHasLinkedBank(boolean hasLinkedBank) { this.hasLinkedBank = hasLinkedBank; }
}