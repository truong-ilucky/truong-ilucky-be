package burundi.ilucky.controller;

import burundi.ilucky.jwt.JwtTokenProvider;
import burundi.ilucky.payload.*;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@Log4j2
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/login")
	public ResponseEntity<?> auth(@Valid @RequestBody AuthRequest authRequest) {
		Response response = new Response();
	try {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
	;

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.generateToken((User) authentication.getPrincipal());

		AuthResponse authResponse = new AuthResponse(jwt);
		return ResponseEntity.ok().body(authResponse);}
	catch (BadCredentialsException e) {
		response.setStatus("404");
		response.setMessage("Bad credentials");
		return ResponseEntity.status(HttpStatus.OK).body(response);

	}
	}
}
