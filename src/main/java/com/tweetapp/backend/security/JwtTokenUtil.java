package com.tweetapp.backend.security;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NoArgsConstructor;

/**
 * @author <b>Subham Santra</b>
 *
 */
@Service
@NoArgsConstructor
public class JwtTokenUtil {
	/**
	 * this variable will store SECRET KEY of the Application
	 */
	private static final String SECRET_KEY = "SSBoYXRlIFBha2lzdGFuIGJ1dCBJIGxvdmUgSW5kaWE=";

	/**
	 * this variable will store jwtToken life time, by default it is 86400000
	 * millisecond (1 day)
	 */
	@Value("${jwt.lifetime:86400000}")
	private long jwtTokenLifeTime;

	/**
	 * @param jwtToken
	 * @return userName from jwtToken
	 */
	public String extractUserName(final String jwtToken) {
		return extractClaim(jwtToken, Claims::getSubject);
	}

	/**
	 * @param jwtToken
	 * @return expiration date from jwtToken
	 */
	public Date extractExpiration(final String jwtToken) {
		return extractClaim(jwtToken, Claims::getExpiration);
	}

	/**
	 * @param <T>
	 * @param jwtToken
	 * @param claimsResolver
	 * @return claim of time T
	 */
	public <T> T extractClaim(final String jwtToken, final Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(jwtToken);
		return claimsResolver.apply(claims);
	}

	/**
	 * @param userDetails has userName
	 * @return generated jwtToken with userName as subject
	 */
	public String generateToken(final UserDetails userDetails) {
		final Map<String, Object> claims = new ConcurrentHashMap<>();
		claims.put("ROLE", userDetails.getAuthorities());
		return createToken(claims, userDetails);
	}

	/**
	 * @param claims
	 * @param userDetails
	 * @return generated token from claims and userDetails
	 */
	private String createToken(final Map<String, Object> claims, final UserDetails userDetails) {
		final Date issueDate = new Date();
		final Date expirationDate = new Date(issueDate.getTime() + jwtTokenLifeTime);
		final String userName = userDetails.getUsername();
		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(issueDate)
				.setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
	}

	/**
	 * @param jwtToken
	 * @param userDetails
	 * @return true if jwtToken is valid, false otherwise
	 */
	public Boolean validateToken(final String jwtToken, final UserDetails userDetails) {
		final String actualUserName = extractUserName(jwtToken);
		final String expectedUserName = userDetails.getUsername();
		return actualUserName.equals(expectedUserName) && isTokenNotExpired(jwtToken);
	}

	/**
	 * @param jwtToken
	 * @return true if expiration < currentDate
	 */
	private boolean isTokenExpired(final String jwtToken) {
		final Date expiration = extractExpiration(jwtToken);
		final Date currentDate = new Date();
		return expiration.before(currentDate);
	}

	/**
	 * @param jwtToken
	 * @return Claims extracted from a jwtToken
	 */
	private Claims extractAllClaims(final String jwtToken) {
		return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken).getBody();
	}

	/**
	 * @param jwtToken
	 * @return false if jwtToken is expired, true otherwise
	 */
	public Boolean isTokenNotExpired(final String jwtToken) {
		return !(isTokenExpired(jwtToken));
	}

	/**
	 * @param jwtToken
	 * @return role of user
	 */
	public String role(final String jwtToken) {
		return "USER";
	}
}
