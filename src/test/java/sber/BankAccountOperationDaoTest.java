package sber;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;
import sber.dao.BankAccountOperationDao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BankAccountOperationDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BankAccountOperationDao bankAccountOperationDao;

    @After
    public void afterTest() throws SQLException {
        jdbcTemplate.execute("TRUNCATE TABLE bank_account_operations");
    }

    @Test
    public void testDao1() {
        bankAccountOperationDao.logError(1, 1, new BigDecimal(100));
        final List<OperationRow> operationRows = jdbcTemplate.query("SELECT * FROM bank_account_operations", new OperationRowMapper());
        Assert.assertEquals(1, operationRows.size());
        Assert.assertEquals(1, operationRows.get(0).userId);
        Assert.assertEquals(0, operationRows.get(0).success);
        Assert.assertEquals(1, operationRows.get(0).operationType);
        Assert.assertEquals(new BigDecimal(100), operationRows.get(0).value);
    }

    @Test
    public void testDao2() {
        bankAccountOperationDao.logSuccess(1, 1, new BigDecimal(100));
        final List<OperationRow> operationRows = jdbcTemplate.query("SELECT * FROM bank_account_operations", new OperationRowMapper());
        Assert.assertEquals(1, operationRows.size());
        Assert.assertEquals(1, operationRows.get(0).userId);
        Assert.assertEquals(1, operationRows.get(0).success);
        Assert.assertEquals(1, operationRows.get(0).operationType);
        Assert.assertEquals(new BigDecimal(100), operationRows.get(0).value);
    }

    private static class OperationRowMapper implements RowMapper<OperationRow> {
        @Override
        public OperationRow mapRow(ResultSet resultSet, int i) throws SQLException {
            return new OperationRow(
                    resultSet.getInt("operation_state"),
                    resultSet.getLong("person_id"),
                    resultSet.getInt("operation_type"),
                    resultSet.getBigDecimal("operation_value")
            );
        }
    }

    private static class OperationRow {
        public final int success;
        public final long userId;
        public final int operationType;
        public final BigDecimal value;

        OperationRow(int success, long userId, int operationType, BigDecimal value) {
            this.success = success;
            this.userId = userId;
            this.operationType = operationType;
            this.value = value;
        }
    }
}
