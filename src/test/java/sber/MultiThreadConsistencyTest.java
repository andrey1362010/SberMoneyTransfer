package sber;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import sber.service.MoneyTransferService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQL)
public class MultiThreadConsistencyTest {

    @Autowired
    private MoneyTransferService moneyTransferService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Многопоточный тест на консистентность БД.
     * Используем HSQL базу, так как Н2 не поддерживает уровень изолированности REPEATABLE_READ
     */
    @Test
    public void testMultiThreadConsistency() {

        final int threadCount = 4;
        final int operationsCount = 100;
        final AtomicReference<BigDecimal> total = new AtomicReference<>();
        final AtomicInteger totalSuccess = new AtomicInteger();
        final AtomicInteger totalError = new AtomicInteger();

        total.set(BigDecimal.ZERO);
        final List<Future> results = new ArrayList<>();
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < operationsCount; i++) {
            final Future future = executorService.submit(() -> {
                try {
                    final BigDecimal insertValue = new BigDecimal(100.);
                    moneyTransferService.topUp(1, insertValue);
                    total.accumulateAndGet(insertValue, (a, b) -> a.add(b));
                    totalSuccess.incrementAndGet();
                } catch (Throwable e) {
                    totalError.incrementAndGet();
                }
            });
            results.add(future);
        }

        results.forEach(r -> {
            try {
                r.get();
            } catch (Exception e) {}
        });


        final int errorCount = jdbcTemplate.queryForObject("SELECT count(*) FROM bank_account_operations WHERE operation_state = 0", Integer.class);
        final int successCount = jdbcTemplate.queryForObject("SELECT count(*) FROM bank_account_operations WHERE operation_state = 1", Integer.class);
        final BigDecimal result = jdbcTemplate.queryForObject("SELECT value FROM bank_account WHERE person_id = 1", BigDecimal.class);
        Assert.assertEquals(totalSuccess.get(), successCount);
        Assert.assertEquals(totalError.get(), errorCount);
        Assert.assertEquals(total.get(), result);
    }
}
