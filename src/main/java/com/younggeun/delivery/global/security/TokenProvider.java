package com.younggeun.delivery.global.security;

import com.younggeun.delivery.admin.service.AdminService;
import com.younggeun.delivery.global.entity.RoleType;
import com.younggeun.delivery.partner.service.PartnerService;
import com.younggeun.delivery.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TokenProvider {

  private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour
  private static final String KEY_ROLES = "roles";
  private final UserService userService;
  private final PartnerService partnerService;
  private final AdminService adminService;
  private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);


  @Value("${spring.jwt.secret}")
  private String secretKey;

  // 토큰 생성
  public String generateToken(String username, RoleType roles) {
    Claims claims = Jwts.claims().setSubject(username);
    claims.put(KEY_ROLES, roles);
    var now = new Date();
    var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiredDate)
        .signWith(key)
        .compact();

  }

  public Authentication getUserAuthentication(String jwt) {
    Claims claims = this.parseClaims(jwt);
    String username = claims.getSubject();
    String roles = this.getRolesFromToken(jwt);

    if (username != null && roles != null) {
      UserDetails userDetails = this.userService.loadUserByUsername(username);
      return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    return null;
  }

  public Authentication getPartnerAuthentication(String jwt) {
    Claims claims = this.parseClaims(jwt);
    String username = claims.getSubject();
    String roles = this.getRolesFromToken(jwt);

    if (username != null && roles != null) {
      UserDetails userDetails = this.partnerService.loadUserByUsername(username);
      return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    return null;

  }

  public Authentication getAdminAuthentication(String jwt) {
    Claims claims = this.parseClaims(jwt);
    String username = claims.getSubject();
    String roles = this.getRolesFromToken(jwt);

    if (username != null && roles != null) {
      UserDetails userDetails = adminService.loadUserByUsername(username);
      return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    return null;

  }

  public String getUserName(String token) {
    return this.parseClaims(token).getSubject();
  }

  public boolean validateToken(String token) {
    if (!StringUtils.hasText(token)) {
      return false;
    }

    var claims = this.parseClaims(token);
    return !claims.getExpiration().before(new Date());
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  public String getRolesFromToken(String token) {
    Claims claims = this.parseClaims(token);
    String roles = (String) claims.get(KEY_ROLES);

    return roles.isEmpty() ? null : roles;
  }
}
