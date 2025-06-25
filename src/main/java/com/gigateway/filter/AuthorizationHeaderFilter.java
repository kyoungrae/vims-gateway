package com.gigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    private final String jwtSecret = "key";

    public AuthorizationHeaderFilter(){
        super(Config.class);
    }

    public static class Config{

    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain)->{
            ServerHttpRequest request = exchange.getRequest();

            //public end point
            if(
               request.getURI().getPath().startsWith("/")
//               ||request.getURI().getPath().startsWith("/api/login")
//               ||request.getURI().getPath().startsWith("/api/signup")
               )
            {
                return chain.filter(exchange);
            }

            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange, "NO Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = Objects.requireNonNull(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).get(0);
            String jwt = authorizationHeader.replace("Bearer","");

            if(!isJwtValid(jwt)){
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
            try{
                Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
                String userId = claims.getSubject();
                String userRole = claims.get("role",String.class);

                ServerHttpRequest mutatedRequest = request.mutate()
                                                   .header("X-User-Id", userId)
                                                   .header("X-User-Roles", userRole)
                                                   .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            }catch (Exception e){
                return onError(exchange, "JWT token error: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }
    private boolean isJwtValid(String jwt){
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
            return true;
        }catch (Exception e){
            System.err.println("JWT Validation Error: " + e.getMessage());
            return false;
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }


}
