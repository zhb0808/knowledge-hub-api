package com.software.knowledgehub.security.authorization;

import com.software.knowledgehub.security.model.AuthenticatedUser;
import com.software.knowledgehub.system.entity.SysPermission;
import com.software.knowledgehub.system.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class PermissionAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final PermissionService permissionService;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authentication,
            RequestAuthorizationContext context) {
        Authentication currentAuthentication = authentication.get();
        if (currentAuthentication == null) {
            return new AuthorizationDecision(false);
        }

        Object principal = currentAuthentication.getPrincipal();
        if (!(principal instanceof AuthenticatedUser)) {
            return new AuthorizationDecision(false);
        }

        AuthenticatedUser user = (AuthenticatedUser) principal;
        String requestMethod = context.getRequest().getMethod();
        String requestPath = context.getRequest().getRequestURI();

        // 加载当前用户的业务权限。
        for (SysPermission permission : permissionService.listByUserId(user.getId())) {
            if (permission.getApiRules() == null) {
                continue;
            }

            for (String rule : permission.getApiRules().split(",")) {
                String item = rule.trim();
                if (item.isEmpty()) {
                    continue;
                }

                int separator = item.indexOf(' ');
                if (separator <= 0) {
                    continue;
                }

                String method = item.substring(0, separator);
                String pathPattern = item.substring(separator + 1).trim();
                if (requestMethod.equalsIgnoreCase(method)
                        && pathMatcher.match(pathPattern, requestPath)) {
                    return new AuthorizationDecision(true);
                }
            }
        }

        return new AuthorizationDecision(false);
    }
}
