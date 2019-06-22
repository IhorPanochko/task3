package net.proselyte.jwtappdemo.security;

import lombok.extern.slf4j.Slf4j;
import net.proselyte.jwtappdemo.model.Card;
import net.proselyte.jwtappdemo.security.jwt.JwtUser;
import net.proselyte.jwtappdemo.security.jwt.JwtUserFactory;
import net.proselyte.jwtappdemo.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final CardService cardService;

    @Autowired
    public JwtUserDetailsService(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Card card = cardService.findByUsername(username);

        if (card == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(card);
        log.info("IN loadUserByUsername - user with username: {} successfully loaded", username);
        return jwtUser;
    }
}
