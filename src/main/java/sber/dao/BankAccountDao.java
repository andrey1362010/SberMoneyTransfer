package sber.dao;
import sber.exceptions.NotEnoughMoneyException;

import java.math.BigDecimal;

public interface BankAccountDao {

    void topUp(long userId, BigDecimal value);

    void withdraw(long userId, BigDecimal value) throws NotEnoughMoneyException;
}
