package br.com.breno_barbosa1.services;

import br.com.breno_barbosa1.data.dto.v1.security.AccountCredentialsDTO;
import br.com.breno_barbosa1.data.dto.v1.security.TokenDTO;
import br.com.breno_barbosa1.exception.RequiredObjectIsNullException;
import br.com.breno_barbosa1.model.User;
import br.com.breno_barbosa1.repository.UserRepository;
import br.com.breno_barbosa1.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AuthService {

    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserRepository repository;

    public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credentials.getUsername(),
                credentials.getPassword()
            )
        );

        var user = repository.findByUsername(credentials.getUsername());

        if (user == null) throw new UsernameNotFoundException("Username " + credentials.getUsername() + " not found!");

        var token = tokenProvider.createAccessToken(
            credentials.getUsername(),
            user.getRoles()
        );

        return ResponseEntity.ok(token);
    }

    public ResponseEntity<TokenDTO> refreshToken(String username, String refreshToken) {

        TokenDTO token;
        var user = repository.findByUsername(username);
        if (user != null) {
            token = tokenProvider.refreshToken(refreshToken);
        } else {
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }

        return ResponseEntity.ok(token);
    }

    public AccountCredentialsDTO create(AccountCredentialsDTO user) {

        if (user == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one Person!");
        var entity = new User();
        entity.setFullName(user.getFullname());
        entity.setUserName(user.getUsername());
        entity.setPassword(generateHashedPassword(user.getPassword()));
        entity.setAccountNonExpired(true);
        entity.setAccountNonLocked(true);
        entity.setCredentialsNonExpired(true);
        entity.setEnabled(true);
        var dto = repository.save(entity);

        return new AccountCredentialsDTO(dto.getUsername(), dto.getPassword(), dto.getFullName());
    }

    private String generateHashedPassword(String password) {

        PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder(
                "", 8, 185000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("pbkdf2", pbkdf2Encoder);
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);

        passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
        return passwordEncoder.encode(password);
    }
}