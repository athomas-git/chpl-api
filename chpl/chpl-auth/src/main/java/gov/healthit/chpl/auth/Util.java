package gov.healthit.chpl.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import gov.healthit.chpl.auth.domain.Authority;
import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.auth.user.User;

public class Util {

    public static String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            return auth.getPrincipal().toString();
        }
    }

    public static boolean isUserRoleAdmin() {
        return doesUserHaveRole(Authority.ROLE_ADMIN);
    }

    public static boolean isUserRoleOnc() {
        return doesUserHaveRole(Authority.ROLE_ONC);
    }

    public static boolean isUserRoleCmsStaff() {
        return doesUserHaveRole(Authority.ROLE_CMS_STAFF);
    }

    public static boolean isUserRoleAcbAdmin() {
        return doesUserHaveRole(Authority.ROLE_ACB);
    }

    public static boolean isUserRoleAtlAdmin() {
        return doesUserHaveRole(Authority.ROLE_ATL);
    }

    public static User getCurrentUser() {

        User user = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof User) {
            user = (User) auth;
        }
        return user;
    }

    /**
     * Get the ID of the active user. If the active user is being impersonated, get the id of the impersonating user instead.
     * @return the user's audit-ready id
     */
    public static long getAuditId() {
        JWTAuthenticatedUser user = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JWTAuthenticatedUser) {
            user = (JWTAuthenticatedUser) auth;
        }
        if (user.getImpersonatingUser() != null) {
            return user.getImpersonatingUser().getId();
        } else {
            return user.getId();
        }
    }

    public static String fromInt(final Integer toStr) {
        return toStr.toString();
    }

    private static boolean doesUserHaveRole(final String authority) {
        User user = getCurrentUser();
        if (user == null) {
            return false;
        }
        for (GrantedPermission perm : user.getPermissions()) {
            if (perm.getAuthority().equals(authority)) {
                return true;
            }
        }
        return false;
    }

}
