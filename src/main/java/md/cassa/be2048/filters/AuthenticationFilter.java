package md.cassa.be2048.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import md.cassa.be2048.constants.Security;
import md.cassa.be2048.services.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements Filter {

    @Value("${security.enable:false}")
    private boolean isSecurityEnabled;

    private final AuthService authService;

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws IOException, ServletException {

        if (isSecurityEnabled) {
            final HttpServletRequest request = (HttpServletRequest) servletRequest;
            final HttpServletResponse response = (HttpServletResponse) servletResponse;

            // Check if the request path should be excluded from authentication
            String requestPath = request.getRequestURI();
            if (isExcludedPath(requestPath)) {
                filterChain.doFilter(request, response);
                return;
            }

            final String session = authService.getSessionFromRequest(request);

            if (session == null || !authService.isAuthenticated(UUID.fromString(session))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isExcludedPath(String requestPath) {
        for (String excludedPath : Security.PUBLIC_ENDPOINTS) {
            // use prefix match for directory-style entries, exact match for everything else
            if (excludedPath.endsWith("/")) {
                if (requestPath.startsWith(excludedPath)) return true;
            } else {
                if (requestPath.equals(excludedPath)) return true;
            }
        }
        return false;
    }
}
