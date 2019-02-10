package sber.service;

import sber.exceptions.NotEnoughMoneyException;

import java.math.BigDecimal;

public interface MoneyTransferService {
    void topUp(long userId, BigDecimal value);
    void withdraw(long userId, BigDecimal value) throws NotEnoughMoneyException;
}
