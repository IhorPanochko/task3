package net.proselyte.jwtappdemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDto{

    @JsonIgnore
    private Long id;

    private String username;

    private String password;

    private BigDecimal money;

    public CardDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public CardDto(String username, String password, BigDecimal money) {
        this.username = username;
        this.password = password;
        this.money = money;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
