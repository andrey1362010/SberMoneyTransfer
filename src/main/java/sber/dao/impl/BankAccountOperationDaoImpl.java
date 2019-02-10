package sber.dao.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sber.dao.BankAccountOperationDao;

import java.math.BigDecimal;

@Repository
public class BankAccountOperationDaoImpl implements BankAccountOperationDao {

    private static String ADD_OPERATION_QUERY = "INSERT INTO bank_account_operations (person_id, operation_type, operation_value, operation_success) VALUES (?,?,?,?)";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void logSuccess(long userId, int operationType, BigDecimal value) {
        jdbcTemplate.update(ADD_OPERATION_QUERY, userId, operationType, value, true);
    }

    @Override
    public void logError(long userId, int operationType, BigDecimal value) {
        jdbcTemplate.update(ADD_OPERATION_QUERY, userId, operationType, value, false);
    }
}
