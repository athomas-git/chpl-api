package gov.healthit.chpl.scheduler;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.healthit.chpl.auth.user.AuthenticationSystem;
import gov.healthit.chpl.auth.user.ChplSystemUsers;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.domain.auth.User;
import gov.healthit.chpl.dto.auth.UserDTO;

public class SecurityContextCapableJob {
    public void setSecurityContext(UserDTO user) {
        SecurityContextHolder.getContext().setAuthentication(JWTAuthenticatedUser.builder()
                .authenticated(true)
                .authenticationSystem(AuthenticationSystem.CHPL)
                .fullName(user.getFullName())
                .id(user.getId())
                .friendlyName(user.getFriendlyName())
                .subjectName(user.getUsername())
                .authorities(List.of(new SimpleGrantedAuthority(user.getPermission().getGrantedPermission().toString())))
                .build());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    public void setSecurityContext(JWTAuthenticatedUser user) {
        SecurityContextHolder.getContext().setAuthentication(user);
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    public void setSecurityContext(String authority) {
        SecurityContextHolder.getContext().setAuthentication(JWTAuthenticatedUser.builder()
                .authenticated(true)
                .authenticationSystem(AuthenticationSystem.CHPL)
                .fullName("Administrator")
                .id(ChplSystemUsers.ADMIN_USER_ID)
                .friendlyName("Admin")
                .subjectName("admin")
                .authorities(List.of(new SimpleGrantedAuthority(authority)))
                .build());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    public void setSecurityContext(User user) {
        SecurityContextHolder.getContext().setAuthentication(JWTAuthenticatedUser.builder()
                .authenticated(true)
                .authenticationSystem(user.getCognitoId() != null ? AuthenticationSystem.COGNITO : AuthenticationSystem.CHPL)
                .fullName(user.getFullName())
                .id(user.getUserId())
                .cognitoId(user.getCognitoId())
                .friendlyName(user.getFriendlyName())
                .subjectName(user.getEmail())
                .authorities(List.of(new SimpleGrantedAuthority(user.getRole())))
                .build());
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}
