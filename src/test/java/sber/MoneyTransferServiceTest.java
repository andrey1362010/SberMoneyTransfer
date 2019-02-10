package sber;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import sber.dao.impl.BankAccountDao;
import sber.dao.impl.BankAccountOperationDao;
import sber.service.impl.MoneyTransferServiceImpl;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MoneyTransferServiceTest {

    private MoneyTransferServiceImpl moneyTransferService;
    private BankAccountDao bankAccountDao;
    private BankAccountOperationDao bankAccountOperationDao;

    @Before
    public void setup(){
        bankAccountDao = mock(BankAccountDao.class);
        bankAccountOperationDao = mock(BankAccountOperationDao.class);
        moneyTransferService = new MoneyTransferServiceImpl(bankAccountDao, bankAccountOperationDao);
    }


    /**
     * при возникновении ошибки транзакции денег смотрим, что вызывается запись ошибки в лог и выбрасывается exception
     */
    @Test
    public void test1(){
        //Бросаем ошибку
        doThrow(IllegalStateException.class).when(bankAccountDao).takeMoney(any(Long.class), any(BigDecimal.class));
        try {
            moneyTransferService.takeMoney(1, new BigDecimal(100.));
            Assert.fail();
        } catch (Exception e) {
            //Проверяем, что ошибка перебрасывается
            Assert.assertEquals(IllegalStateException.class, e.getClass());
        }

        Mockito.verify(bankAccountDao, times(1)).takeMoney(any(Long.class), any(BigDecimal.class));
        Mockito.verify(bankAccountOperationDao, times(0)).logSuccess(any(Long.class), any(Integer.class), any(BigDecimal.class));
        Mockito.verify(bankAccountOperationDao, times(1)).logError(any(Long.class), any(Integer.class), any(BigDecimal.class));
    }

    /**
     * аналогично test1 только для putMoney
     */
    @Test
    public void test2(){
        //Бросаем ошибку
        doThrow(IllegalStateException.class).when(bankAccountDao).putMoney(any(Long.class), any(BigDecimal.class));
        try {
            moneyTransferService.putMoney(1, new BigDecimal(100.));
            Assert.fail();
        } catch (Exception e) {
            //Проверяем, что ошибка перебрасывается
            Assert.assertEquals(IllegalStateException.class, e.getClass());
        }

        Mockito.verify(bankAccountDao, times(1)).putMoney(any(Long.class), any(BigDecimal.class));
        Mockito.verify(bankAccountOperationDao, times(0)).logSuccess(any(Long.class), any(Integer.class), any(BigDecimal.class));
        Mockito.verify(bankAccountOperationDao, times(1)).logError(any(Long.class), any(Integer.class), any(BigDecimal.class));
    }


    /**
     * при успешной транзакции вызывается запись в лог
     */
    @Test
    public void test3(){
        //Бросаем ошибку
        moneyTransferService.putMoney(1, new BigDecimal(100.));
        Mockito.verify(bankAccountDao, times(1)).putMoney(any(Long.class), any(BigDecimal.class));
        Mockito.verify(bankAccountOperationDao, times(1)).logSuccess(any(Long.class), any(Integer.class), any(BigDecimal.class));
        Mockito.verify(bankAccountOperationDao, times(0)).logError(any(Long.class), any(Integer.class), any(BigDecimal.class));
    }
}
