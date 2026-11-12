package org.example.sor_mrp.shared.configuration;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("roleRights")
public class RoleRightsConfig {

  private final Map<String, List<String>> rights =
      Map.of(
          "user", List.of("getUsers", "getItems", "getBoms"),
          "admin", List.of("getUsers", "manageUsers", "deleteUser", "getItems", "manageItems", "manageBoms"),
          "systemAdmin",
              List.of("getUsers", "manageUsers", "deleteUser", "getItems", "manageItems", "manageBoms"));

  public List<String> getRightsForRole(String role) {
    return rights.getOrDefault(role, List.of());
  }
}
