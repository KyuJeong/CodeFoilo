package com.landvibe.codefolio.restcontroller;

import com.landvibe.codefolio.config.annotation.CurrentUser;
import com.landvibe.codefolio.model.User;
import com.landvibe.codefolio.model.dto.AuthenticationRequest;
import com.landvibe.codefolio.model.dto.UserInfoRequest;
import com.landvibe.codefolio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/api/login")
    public User login(@RequestBody AuthenticationRequest authenticationRequest, HttpSession session) {
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        User user = userService.loadUserByUsername(username);
        user.setToken(session.getId());
        return user;
    }

    @PostMapping(value = "/api/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public void signUp(@RequestBody UserInfoRequest userInfoRequest) {
        userService.update(userInfoRequest.getUsername(), userInfoRequest.getName(), userInfoRequest.getJob());
    }

    @GetMapping(value = "/api/auth")
    @ResponseStatus(value = HttpStatus.OK)
    public void checkAuth(@CurrentUser User user) {
        if (user == null) {
            throw new UsernameNotFoundException("No Auth");
        }
    }

}
