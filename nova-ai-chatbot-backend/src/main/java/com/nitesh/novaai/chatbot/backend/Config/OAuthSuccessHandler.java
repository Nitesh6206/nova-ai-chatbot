package com.nitesh.novaai.chatbot.backend.Config;

import com.nitesh.novaai.chatbot.backend.Entity.User;
import com.nitesh.novaai.chatbot.backend.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;     // 🔥 Added

    public OAuthSuccessHandler(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        // 🔥 User ko database mein save ya update kar do
        userService.getOrCreateUser(email, name, picture);

        // JWT Token generate karo
        String token = jwtService.generateToken(email);

        // Cookie Setup
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);           // Production mein true kar dena (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(86400);           // 24 hours
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);

        // Frontend pe redirect
        response.sendRedirect("http://localhost:3000?auth=success");
    }
}