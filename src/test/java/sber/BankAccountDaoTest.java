package sber;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import sber.dao.BankAccountDao;
import sber.exceptions.NotEnoughMoneyException;

import java.math.BigDecimal;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BankAccountDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BankAccountDao bankAccountDao;

    @After
    public void afterTest() throws SQLException {
        jdbcTemplate.update("update bank_account set value = ? where person_id = ?", 0, 0);
        jdbcTemplate.update("update bank_account set value = ? where person_id = ?", 0, 1);
    }

    /**
     * Положить деньги.
     */
    @Test
    public void testTopUp() {
        bankAccountDao.topUp(0, new BigDecimal(100));
        final BigDecimal result1 = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 0", BigDecimal.class);
        Assert.assertEquals(new BigDecimal(100), result1);
        bankAccountDao.topUp(0, new BigDecimal(0.5));
        final BigDecimal result2 = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 0", BigDecimal.class);
        Assert.assertEquals(new BigDecimal(100.5), result2);
    }

    /**
     * Снять деньги.
     */
    @Test
    public void testWithdraw() throws NotEnoughMoneyException {
        jdbcTemplate.update("update bank_account set value = ? where person_id = ?", 100, 0);
        bankAccountDao.withdraw(0, new BigDecimal(50));
        final BigDecimal result1 = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 0", BigDecimal.class);
        Assert.assertEquals(new BigDecimal(50), result1);
    }

    /**
     * Снять деньги. не хватает средств.
     */
    @Test(expected = NotEnoughMoneyException.class)
    public void testWithdrawNotEnoughMoney() throws NotEnoughMoneyException {
        try {
            jdbcTemplate.update("update bank_account set value = ? where person_id = ?", 100, 0);
            bankAccountDao.withdraw(0, new BigDecimal(1000));
        } catch (Exception e) {
            final BigDecimal result = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 0", BigDecimal.class);
            Assert.assertEquals(new BigDecimal(100), result);
            throw e;
        }
    }

    /**
     * Отрицательная сумма.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTopUpBadParam() {
        try {
            bankAccountDao.topUp(0, new BigDecimal(-10));
        } catch (Exception e) {
            final BigDecimal result = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 0", BigDecimal.class);
            Assert.assertEquals(new BigDecimal(0), result);
            throw e;
        }
    }

    /**
     * Отрицательная сумма.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWithdrawBadParam() throws NotEnoughMoneyException {
        try {
            bankAccountDao.withdraw(0, new BigDecimal(-10));
        } catch (Exception e) {
            final BigDecimal result = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 0", BigDecimal.class);
            Assert.assertEquals(new BigDecimal(0), result);
            throw e;
        }
    }

    /**
     * Не существующий юзер
     */
    @Test(expected = Exception.class)
    public void testTopUpUserNotExist() {
        try {
            bankAccountDao.topUp(12345, new BigDecimal(10));
        } catch (Exception e) {
            final BigDecimal result = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 0", BigDecimal.class);
            Assert.assertEquals(new BigDecimal(0), result);
            throw e;
        }
    }

    /**
     * Не существующий юзер
     */
    @Test(expected = Exception.class)
    public void testWithdrawUserNotExist() {
        try {
            bankAccountDao.topUp(12345, new BigDecimal(10));
        } catch (Exception e) {
            final BigDecimal result = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 0", BigDecimal.class);
            Assert.assertEquals(new BigDecimal(0), result);
            throw e;
        }
    }


}
