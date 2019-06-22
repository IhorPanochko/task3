package net.proselyte.jwtappdemo.service;

import net.proselyte.jwtappdemo.dto.CardDto;
import net.proselyte.jwtappdemo.dto.TransactinDto;
import net.proselyte.jwtappdemo.model.Card;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CardService {

    Card register(Card user);

    List<Card> getAll();

    Card findByUsername(String username);

    Card findById(Long id);

    void delete(Long id);

    void putOnCard(String code,BigDecimal sum);

    void getMoney(String id,BigDecimal sum);

    void sendMoney(String cardFrom,String cardTo,BigDecimal sum);

    CardDto getInfo(String code);

    Map<Object, Object> transact(TransactinDto transactinDto);

}
