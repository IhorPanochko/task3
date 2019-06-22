package net.proselyte.jwtappdemo.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.proselyte.jwtappdemo.dto.CardDto;
import net.proselyte.jwtappdemo.dto.TransactinDto;
import net.proselyte.jwtappdemo.model.Card;
import net.proselyte.jwtappdemo.model.Role;
import net.proselyte.jwtappdemo.model.Transaction;
import net.proselyte.jwtappdemo.repository.RoleRepository;
import net.proselyte.jwtappdemo.repository.CardRepository;
import net.proselyte.jwtappdemo.repository.TransactionRepository;
import net.proselyte.jwtappdemo.service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.UserTransaction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
@Slf4j
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TransactionRepository transactionRepository;
    Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    public CardServiceImpl(CardRepository cardRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder,TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionRepository=transactionRepository;
    }

    @Override
    public Card register(Card card) {
        Role roleUser = roleRepository.findByName("user");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);

        card.setPassword(passwordEncoder.encode(card.getPassword()));
        card.setRoles(userRoles);
        Card registerCard = cardRepository.save(card);

        logger.info("IN register - user: successfully registered", registerCard);

        return registerCard;
    }

    @Override
    public List<Card> getAll() {
        List<Card> result = cardRepository.findAll();
        logger.info("IN getAll - {} users found", result.size());
        return result;
    }

    @Override
    public Card findByUsername(String username) {
        Card result = cardRepository.findByUsername(username);
        logger.info("IN findByUsername - user: {} found by username: {}", result, username);
        return result;
    }

    @Override
    public Card findById(Long id) {
        Card result = cardRepository.findById(id).orElse(null);

        if (result == null) {
            logger.warn("IN findById - no user found by id: {}", id);
            return null;
        }

        logger.info("IN findById - user: {} found by id: {}", result);
        return result;
    }

    @Override
    public void delete(Long id) {
        cardRepository.deleteById(id);
        logger.info("IN delete - user with id: {} successfully deleted");
    }

    @Override
    public void putOnCard(String code, BigDecimal sum) {
        Transaction transaction=new Transaction();
        transaction.setType("put");
        Card card=cardRepository.findByUsername(code);
        transaction.setCard(card);
        transaction.setSum(sum);

            plus(code,sum);

        transactionRepository.save(transaction);
        cardRepository.save(card);
    }

    @Override
    public void getMoney(String id, BigDecimal sum) {
        Transaction transaction=new Transaction();
        transaction.setType("get");
        transaction.setSum(sum);
        transaction.setCard(cardRepository.findByUsername(id));

        if ((ifEnoughMoney(id,sum)) ){
            minus(id,sum);
            transactionRepository.save(transaction);
            logger.info("get money");


        }
        logger.info("not enought money");
    }

    @Override
    public void sendMoney(String cardFrom, String cardTo, BigDecimal sum) {

Transaction transaction=new Transaction();
transaction.setCard(cardRepository.findByUsername(cardFrom));
transaction.setSum(sum);
transaction.setReciever(cardTo);
transaction.setType("send");
       if (ifEnoughMoney(cardFrom,sum)){
           minus(cardFrom,sum);
           plus(cardTo,sum);
           transactionRepository.save(transaction);
       }

    }

    @Override
    public CardDto getInfo(String code) {
        Card card=cardRepository.findByUsername(code);
        if (card==null)
        {
            return  null;
        }
        return new CardDto(card.getUsername(),card.getPassword(),card.getMoney());
    }

    private boolean ifEnoughMoney(String code, BigDecimal sum){

        if (cardRepository.findByUsername(code).getMoney().compareTo(sum)== -1 ){
            return false;
        }else
            return true;

    }


    private void minus(String code,BigDecimal sum){

        Card card=cardRepository.findByUsername(code);
        card.setMoney(card.getMoney().subtract(sum));
        cardRepository.save(card);
    }

    private void plus(String code,BigDecimal sum){

        Card card=cardRepository.findByUsername(code);
        card.setMoney(card.getMoney().add(sum));
        cardRepository.save(card);
    }

    @Override
    public Map<Object, Object> transact(TransactinDto transactinDto) {
        Map<Object, Object> response = new HashMap<>();

        Card card=cardRepository.findByUsername(transactinDto.getCode());

        if (transactinDto.getType().equals("get")){

            if (!ifEnoughMoney(card.getUsername(),transactinDto.getSum())){
                response.put("message","not enought money");
                return  response;

            }else { minus(card.getUsername(),transactinDto.getSum());
                response.put("message","done");
            }
            return  response;
        }

        if (transactinDto.getType().equals("send")){

                if (!transactinDto.getReciever().isEmpty() && (transactinDto.getReciever()!=null)){
                    if (ifEnoughMoney(card.getUsername(),transactinDto.getSum())){
                        sendMoney(card.getUsername(),transactinDto.getReciever(),transactinDto.getSum());
                        response.put("message"," send");

                    }   if (!ifEnoughMoney(card.getUsername(),transactinDto.getSum())){
                        response.put("message"," not enought moneu");

                    }
                }else {
                    response.put("message"," wrong gred");

                }
                return  response;

            }




return  response;
    }
}
