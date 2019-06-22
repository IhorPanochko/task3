package net.proselyte.jwtappdemo.rest;

import net.proselyte.jwtappdemo.dto.CardDto;
import net.proselyte.jwtappdemo.dto.TransactinDto;
import net.proselyte.jwtappdemo.model.Card;
import net.proselyte.jwtappdemo.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/v1/card/")

public class CardController {

    Logger logger = LoggerFactory.getLogger(CardController.class);


    @Autowired
    private CardService cardService;



    @GetMapping("get")
    public ResponseEntity<?> getMyInfo() {
Authentication auth = SecurityContextHolder.getContext().getAuthentication();

       CardDto card=cardService.getInfo(auth.getName());

        Map<Object, Object> response = new HashMap<>();

        if (card==null){
            response.put("message", "anonim");

        }else {
            response.put("your info", card);
            logger.info("user with card: "+ card.getUsername()+" watched info ");
        }



        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("getMoney/{sum}")
    public ResponseEntity<?> getMyMoney(@PathVariable BigDecimal sum) {
        Map<Object, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Card card=cardService.findByUsername(auth.getName());

        if (card==null){
            response.put("message", "anonim");

        }
        if (card.getMoney().compareTo(sum)==-1)
        {
            response.put("message", "not anought money");
            logger.info("user with card: "+ card.getUsername()+"tried get money: "+sum+"$");

        }else {
            cardService.getMoney(auth.getName(),sum);
            logger.info("user with card: "+ card.getUsername()+" got money: "+sum+"$");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping ("putMoney/{sum}")
    public ResponseEntity<?> putMoney(@PathVariable BigDecimal sum) {
        Map<Object, Object> response = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Card card=cardService.findByUsername(auth.getName());

        if (card==null){
            response.put("message", "anonim");
        }else
        if (true){

            cardService.putOnCard(auth.getName(),sum);
            logger.info("user with card: "+ card.getUsername()+"put money: "+sum+"$ on card");

            response.put("message", "you put "+ sum +"$ on your card: "+ card);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping ("send")
    public ResponseEntity<?> putMoney(@RequestBody TransactinDto transactinDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<Object, Object> response = new HashMap<>();

        if(!transactinDto.getCode().equals(auth.getName())){
            response.put("message","is not your account");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else
        return new ResponseEntity<>(cardService.transact(transactinDto), HttpStatus.OK);

    }

    @GetMapping("getTransactions")
    public ResponseEntity<?> getTransactions() {
        Map<Object, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Card card=cardService.findByUsername(auth.getName());
        response.put("Transactions",card.getTransactions());
        return new ResponseEntity<>( response, HttpStatus.OK);

    }

}
