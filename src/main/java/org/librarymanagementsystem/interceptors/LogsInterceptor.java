package org.librarymanagementsystem.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.services.AuditLogService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogsInterceptor implements HandlerInterceptor {
    private final AuditLogService loggerService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader("Authorization");
        String method = request.getMethod();
        String url = request.getRequestURI();
        String principal = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "Anonymous";
        // Log the request
        log.info("REQUEST: User={}, Method={}, URI={}", principal, method, url);

        if (authorizationHeader != null && (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) && !url.endsWith("error")) {
            String[] pathParts = url.split("/");
            if (pathParts.length > 2) {
                String tableName = pathParts[1];
                String action = pathParts[2];

                log.info("ACTION LOG: User={}, Method={}, Table={}, Action={}", principal, method, tableName, action);

                // Log asynchronously to DB
               //loggerService.createLogAsync(method, principal, tableName, action);
            }
        }else{
            String[] pathParts = url.split("/");
            if (pathParts.length > 2) {
                String tableName = pathParts[1];
                String action = pathParts[2];
                log.info(" Else ACTION LOG: User={}, Method={}, Table={}, Action={}", principal, method, tableName, action);

                // Log asynchronously to DB
               // loggerService.createLogAsync(method, principal, tableName, action);
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) {
        String method = request.getMethod();
        String url = request.getRequestURI();
        int status = response.getStatus();
        String principal = (request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : "Anonymous";

        // Log the response
        log.info("RESPONSE: User={}, Method={}, URL={}, Status={}", principal, method, url, status);
        // Log asynchronously to DB
        //loggerService.createLogAsync(method, principal, url, status);
    }
}

