package org.example.sor_mrp.shared.configuration;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component("roleRights")
public class RoleRightsConfig {

    private final Map<String, List<String>> rights = Map.of(
            "user", List.of("getUsers", "getItems"),
            "admin", List.of("getUsers", "manageUsers", "deleteUser", "getItems", "manageItems"),
            "systemAdmin", List.of("getUsers", "manageUsers", "deleteUser", "getItems", "manageItems")
    );

    public List<String> getRightsForRole(String role) {
        return rights.getOrDefault(role, List.of());
    }
}
