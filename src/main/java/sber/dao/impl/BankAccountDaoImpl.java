package sber.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sber.dao.BankAccountDao;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Repository
public class BankAccountDaoImpl implements BankAccountDao {

    private static String SELECT_QUERY = "SELECT value FROM bank_account WHERE person_id = ?";
    private static String UPDATE_QUERY = "update bank_account set value = ? where person_id = ?";

    @Autowired
    private  JdbcTemplate jdbcTemplate;


    /**
     * Явно прописываем уровень изолированности REPEATABLE_READ
     * По умолчанию checked Exceptions не закрывают транзакцию, поэтому прописываем rollbackFor
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void putMoney(long userId, BigDecimal value) {
        if(value.compareTo(new BigDecimal(0.)) < 0) throw new IllegalArgumentException("Отрицательная сумма.");
        final BigDecimal oldValue = jdbcTemplate.queryForObject(SELECT_QUERY, new Object[]{userId}, BigDecimal.class);
        jdbcTemplate.update(UPDATE_QUERY, oldValue.add(value), userId);
    }

    /**
     * Аналогично putMoney
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public void takeMoney(long userId, BigDecimal value) {
        if(value.compareTo(new BigDecimal(0.)) < 0) throw new IllegalArgumentException("Отрицательная сумма.");
        final BigDecimal oldValue = jdbcTemplate.queryForObject(SELECT_QUERY, new Object[]{userId}, BigDecimal.class);
        final BigDecimal newValue = oldValue.subtract(value);
        if(newValue.compareTo(new BigDecimal(0.)) < 0) throw new IllegalStateException("Отрицательный баланс.");
        jdbcTemplate.update(UPDATE_QUERY, newValue, userId);
    }


}
