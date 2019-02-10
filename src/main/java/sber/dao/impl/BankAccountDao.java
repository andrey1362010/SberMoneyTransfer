package sber.dao.impl;
import java.math.BigDecimal;

public interface BankAccountDao {

    void putMoney(long userId, BigDecimal value);

    void takeMoney(long userId, BigDecimal value);
}
