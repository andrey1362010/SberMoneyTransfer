package sber.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sber.dao.BankAccountDao;
import sber.dao.BankAccountOperationDao;
import sber.service.MoneyTransferService;

import java.math.BigDecimal;


@Service
public class MoneyTransferServiceImpl implements MoneyTransferService {

    private Logger logger = LoggerFactory.getLogger(MoneyTransferServiceImpl.class);

    private BankAccountDao bankAccountDao;
    private BankAccountOperationDao bankAccountOperationDao;

    @Autowired
    public MoneyTransferServiceImpl(BankAccountDao bankAccountDao, BankAccountOperationDao bankAccountOperationDao) {
        this.bankAccountDao = bankAccountDao;
        this.bankAccountOperationDao = bankAccountOperationDao;
    }

    /**
     * При падении сервака в момент транзакции изменения счета, информация по этой (неуспешной) транзакции в лог таблицу не запишется.
     * Если это критично, то можно сначала писать неуспешный лог, а потом вместе с транзакцией изменения счета апдейтить его на success
     */
    @Override
    public void putMoney(long userId, BigDecimal value) {
        try {
            bankAccountDao.putMoney(userId, value);
            bankAccountOperationDao.logSuccess(userId, BankAccountOperationDao.OPERATION_TYPE_PUT, value);
        } catch (Exception e) {
            logger.error("Error put money.", e);
            bankAccountOperationDao.logError(userId, BankAccountOperationDao.OPERATION_TYPE_PUT, value);
            throw e;
        }
    }

    /**
     * Аналогично putMoney
     */
    @Override
    public void takeMoney(long userId, BigDecimal value) {
        try {
            bankAccountDao.takeMoney(userId, value);
            bankAccountOperationDao.logSuccess(userId, BankAccountOperationDao.OPERATION_TYPE_TAKE, value);
        } catch (Exception e) {
            logger.error("Error take money.", e);
            bankAccountOperationDao.logError(userId, BankAccountOperationDao.OPERATION_TYPE_TAKE, value);
            throw e;
        }
    }
}
