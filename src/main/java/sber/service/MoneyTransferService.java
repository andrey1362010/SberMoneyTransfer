package sber.service;

import java.math.BigDecimal;

public interface MoneyTransferService {
    void putMoney(long userId, BigDecimal value);
    void takeMoney(long userId, BigDecimal value);
}
