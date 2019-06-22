package net.proselyte.jwtappdemo.rest;

import net.proselyte.jwtappdemo.dto.AuthenticationRequestDto;
import net.proselyte.jwtappdemo.dto.CardDto;
import net.proselyte.jwtappdemo.dto.SignUpForm;
import net.proselyte.jwtappdemo.model.Card;
import net.proselyte.jwtappdemo.model.Role;
import net.proselyte.jwtappdemo.security.jwt.JwtTokenProvider;
import net.proselyte.jwtappdemo.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final CardService cardService;


    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, CardService cardService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.cardService = cardService;

    }


    @PostMapping("signup")
    public ResponseEntity<?> registerCard(@Valid @RequestBody SignUpForm registerRequest) {

        Card card = new Card(registerRequest.getCode(), registerRequest.getPassword()
        );
        card.setMoney(new BigDecimal("0"));
        List<Role>strings=new ArrayList<>();

        strings.add(new Role("user"));
        card.setRoles(strings);
        cardService.register(card);

        Map<Object, Object> response = new HashMap<>();
        response.put("code", card.getUsername());
        response.put("password", card.getPassword());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<?> logn(@RequestBody AuthenticationRequestDto requestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getCode(), requestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.createToken(requestDto.getCode(),cardService.findByUsername(requestDto.getCode()).getRoles());



        System.out.println(jwt);
        Map<Object, Object> response = new HashMap<>();
            response.put("code", requestDto.getCode());
            response.put("token", jwt);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
