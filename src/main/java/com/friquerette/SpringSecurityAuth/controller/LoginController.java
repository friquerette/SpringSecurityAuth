package com.friquerette.SpringSecurityAuth.controller;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
public class LoginController {
    private final OAuth2AuthorizedClientService authorizedClientService;

    public LoginController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @RequestMapping("/user")
    @RolesAllowed("USER")
    public String getUser() {
        return "Welcome User";
    }

    @RequestMapping("/admin")
    @RolesAllowed("ADMIN")
    public String getAdmin() {
        return "Welcome Admin";
    }


    @RequestMapping("/user2")
    public String getGithub(Principal user) {
        System.out.println(user.toString());
        ((OAuth2AuthenticationToken) user).getPrincipal();
        return "Welcome root" + user.toString();
    }

    @RequestMapping("/*")
    public String getGetUserInfo(Principal user) {
        StringBuffer userInfo = new StringBuffer();
        if (user instanceof UsernamePasswordAuthenticationToken) {
            userInfo.append("GET UsernamePasswordAuthenticationToken<BR/>");
            userInfo.append(getUsernamePasswordLoginInfo(user));
        } else if (user instanceof OAuth2AuthenticationToken) {
            userInfo.append("GET OAuth2AuthenticationToken<BR/>");
            userInfo.append(getOauth2LoginInfo(user));
        } else {
            userInfo.append("Not Authenticate");
        }
        return "<a href='./login'>login</a><BR/>" + userInfo.toString();
    }

    private StringBuffer getOauth2LoginInfo(Principal user) {
        StringBuffer protectedInfo = new StringBuffer();
        OAuth2AuthenticationToken authToken = ((OAuth2AuthenticationToken) user);
        OAuth2AuthorizedClient authClient = this.authorizedClientService.loadAuthorizedClient(authToken.getAuthorizedClientRegistrationId(), authToken.getName());
        if (authToken.isAuthenticated()) {
            Map<String, Object> userAttributes = authToken.getPrincipal().getAttributes();
            String userToken = authClient.getAccessToken().getTokenValue();
            protectedInfo.append("Welcome, " + userAttributes.get("name") + "<br><br>");
            protectedInfo.append("e-mail: " + userAttributes.get("email") + "<br><br>");
            protectedInfo.append("Access Token: " + userToken + "<br><br>");
            OAuth2User principal = ((OAuth2AuthenticationToken) user).getPrincipal();
            OidcIdToken oidcIdToken = getIdToken(principal);
            if (oidcIdToken != null) {
                protectedInfo.append("idToken value: " + oidcIdToken.getTokenValue()+"<br><br>");
                protectedInfo.append("Token mapped values <br><br>");
                Map<String, Object> claims = oidcIdToken.getClaims();
                for (String key : claims.keySet()) {
                    protectedInfo.append("  " + key + ": " + claims.get(key)+"<br>");
                }
            } else {
                protectedInfo.append("NA");
            }
        } else {
            protectedInfo.append("NA");
        }
        return protectedInfo;
    }

    private StringBuffer getUsernamePasswordLoginInfo(Principal principal) {
        StringBuffer usernameInfo = new StringBuffer();
        UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) principal);
        if (token.isAuthenticated()) {
            User user = (User) token.getPrincipal();
            usernameInfo.append("Welcome, " + user.getUsername());
        } else {
            usernameInfo.append("NA");
        }
        return usernameInfo;
    }

    private OidcIdToken getIdToken(OAuth2User principal) {
        if (principal instanceof DefaultOidcUser) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
            return oidcUser.getIdToken();
        }
        return null;
    }

}
