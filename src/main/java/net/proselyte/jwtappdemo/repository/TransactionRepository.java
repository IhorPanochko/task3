package net.proselyte.jwtappdemo.repository;


import net.proselyte.jwtappdemo.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long>{
}
