package com.todo.utility;

import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import com.todo.userservice.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * purpose: this class is designed to build and deploy jwt token
 * 
 * @author JAYANTA ROY
 * @version 1.0
 * @since 10/07/18
 */
public class JwtTokenBuilder {
	final static String KEY = "JAYANTA";

	/**
	 * Method to create token
	 * 
	 * @param emp
	 * @return JWT token
	 */
	public String createJWT(User emp) {
		// The JWT signature algorithm we will be using to sign the token
		String id = emp.get_id();
		String subject = emp.getEmail();
		String issuer = emp.getUserName();
		Date now = new Date();
		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(subject).setIssuer(issuer)
				.signWith(SignatureAlgorithm.HS256, KEY);

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	/**
	 * Method to access token
	 * 
	 * @param jwt
	 */
	public static Claims parseJWT(String jwt) {

		// This line will throw an exception if it is not a signed JWS (as expected)
		return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(KEY)).parseClaimsJws(jwt).getBody();

	}
}
