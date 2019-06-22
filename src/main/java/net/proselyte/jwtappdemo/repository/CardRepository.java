package net.proselyte.jwtappdemo.repository;

import net.proselyte.jwtappdemo.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CardRepository extends JpaRepository<Card, Long> {
    Card findByUsername(String name);

}
