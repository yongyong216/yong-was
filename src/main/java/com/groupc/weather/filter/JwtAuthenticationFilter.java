package com.groupc.weather.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.groupc.weather.common.model.AuthenticationObject;
import com.groupc.weather.provider.JwtProvider;
import com.groupc.weather.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userService;

   

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String jwt = parseToken(request);

            boolean hasJwt = jwt != null;
            if (!hasJwt) {
                filterChain.doFilter(request, response);
                return;
            }
        
            AuthenticationObject authenticationObject = jwtProvider.validate(jwt);
            String email = authenticationObject.getEmail();
            boolean compareResult = userService.validateToken(email, jwt);
           
            if(!compareResult){
                filterChain.doFilter(request, response);
                return;
            }
            
            AbstractAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authenticationObject, null, AuthorityUtils.NO_AUTHORITIES);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        filterChain.doFilter(request, response);

    }

    private String parseToken(HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        boolean hasToken = 
            token != null && 
            !token.equalsIgnoreCase("null");
        if (!hasToken) return null;

        boolean isBearer = token.startsWith("Bearer ");
        if (!isBearer) return null;

        String jwt = token.substring(7);
        return jwt;

    }

}