package com.movieflex.MovieApi.auth.services;

import com.movieflex.MovieApi.auth.entities.RefreshToken;
import com.movieflex.MovieApi.auth.entities.User;
import com.movieflex.MovieApi.auth.repositories.RefreshTokenRepository;
import com.movieflex.MovieApi.auth.repositories.UserRepository;
import org.hibernate.sql.Insert;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService  {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;


    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username) {
       User user =  userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+ username));
        long refreshTokenValidity = 30 * 1000;
        RefreshToken refreshToken = user.getRefreshToken();

        if(refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }
        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found!"));

        if (refToken.getExpirationTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException("Refresh Token expired");
        }

        return refToken;
    }


}
