package sber.dao.impl;

import java.math.BigDecimal;

public interface BankAccountOperationDao {
    int OPERATION_TYPE_PUT = 0;
    int OPERATION_TYPE_TAKE = 1;
    void logSuccess(long userId, int operationType, BigDecimal value);
    void logError(long userId, int operationType, BigDecimal value);
}
