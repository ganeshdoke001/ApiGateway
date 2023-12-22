package com.lcwd.gateway.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lcwd.gateway.model.AuthResponse;



@RestController
@RequestMapping("/auth")
public class AuthController {

	private Logger looger = org.slf4j.LoggerFactory.getLogger(AuthController.class);

	@GetMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
			@AuthenticationPrincipal OidcUser user, Model model) {
		this.looger.info("user email id : {}", user.getEmail());
		
		//creating AuthResponse object
		AuthResponse authResponse=new AuthResponse();
		
		// seting email to auth response
		authResponse.setUserId(user.getEmail());
		
		// seting   accessToken to Auth response
		authResponse.setAccessToken(client.getAccessToken().getTokenValue());
		
		authResponse.setRefreshToken(client.getRefreshToken().getTokenValue());
		
		authResponse.setExpireAt(client.getAccessToken().getExpiresAt().getEpochSecond());
		
		List<String> authoritiesList=user.getAuthorities().stream().map(grantedAuthority->{
			return grantedAuthority.getAuthority();
		}).collect(Collectors.toList());
		
		authResponse.setAuthorities(authoritiesList);
		
		return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);

	}
}
