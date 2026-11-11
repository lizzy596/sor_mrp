package org.example.sor_mrp.shared.configuration.security;
import org.example.sor_mrp.shared.configuration.RoleRightsConfig;

import org.example.sor_mrp.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("securityEvaluator")
public class SecurityEvaluator {

    private final RoleRightsConfig roleRightsConfig;

    public SecurityEvaluator(RoleRightsConfig roleRightsConfig) {
        this.roleRightsConfig = roleRightsConfig;
    }

    public boolean hasAccess(Authentication authentication, String targetUserIdStr, String... requiredRights) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            return false;
        }

        // 1. Check if user is accessing their own resource (equivalent to req.params.userId === user.id)
        if (targetUserIdStr != null) {
            try {
                Long targetUserId = Long.valueOf(targetUserIdStr);
                if (targetUserId.equals(user.getId())) {
                    return true;
                }
            } catch (NumberFormatException ignored) {}
        }

        // 2. Check if user has all required rights based on their role
        List<String> userRights = roleRightsConfig.getRightsForRole(user.getRole());
        for (String right : requiredRights) {
            if (!userRights.contains(right)) {
                return false;
            }
        }

        return true;
    }
}