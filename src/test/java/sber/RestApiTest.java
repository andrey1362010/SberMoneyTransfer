package sber;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sber.controller.BankController;
import sber.exceptions.NotEnoughMoneyException;
import sber.service.MoneyTransferService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class RestApiTest {


    @Mock
    private MoneyTransferService moneyTransferService;

    @InjectMocks
    private BankController bankController;

    private MockMvc mvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(bankController).build();
    }

    @Test
    public void testWithdrawValid() throws Exception {
        this.mvc.perform(post("/api/withdraw?id=1&value=1")).andExpect(status().isOk())
                .andDo(mvcResult -> {
                    final String json = mvcResult.getResponse().getContentAsString();
                    final JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    Assert.assertEquals("ok", jsonObject.get("status").getAsString());
                    Assert.assertEquals(1, jsonObject.get("userId").getAsLong());
                });
    }

    @Test
    public void testTopUpValid() throws Exception {
        this.mvc.perform(post("/api/topUp?id=1&value=1")).andExpect(status().isOk())
                .andDo(mvcResult -> {
                    final String json = mvcResult.getResponse().getContentAsString();
                    final JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    Assert.assertEquals("ok", jsonObject.get("status").getAsString());
                    Assert.assertEquals(1, jsonObject.get("userId").getAsLong());
                });
    }

    @Test
    public void testWithdrawNotEnoughParameters() throws Exception {
        this.mvc.perform(post("/api/withdraw?value=1")).andExpect(status().is(400));
    }

    @Test
    public void testTopUpNotEnoughParameters() throws Exception {
        this.mvc.perform(post("/api/topUp?id=1")).andExpect(status().is(400));
    }

    @Test
    public void testWithdrawNotEnoughMoney() throws Exception {
        doThrow(NotEnoughMoneyException.class).when(moneyTransferService).withdraw(any(Long.class), any(BigDecimal.class));
        this.mvc.perform(post("/api/withdraw?id=1&value=1")).andExpect(status().is(400))
                .andDo(mvcResult -> {
                    final String json = mvcResult.getResponse().getContentAsString();
                    final JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    Assert.assertEquals("error", jsonObject.get("status").getAsString());
                    Assert.assertEquals("Not enough money.", jsonObject.get("description").getAsString());
                });
    }
    @Test
    public void testTopUpError() throws Exception {
        doThrow(IllegalStateException.class).when(moneyTransferService).topUp(any(Long.class), any(BigDecimal.class));
        this.mvc.perform(post("/api/topUp?id=1&value=1")).andExpect(status().is(400))
                .andDo(mvcResult -> {
                    final String json = mvcResult.getResponse().getContentAsString();
                    final JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    Assert.assertEquals("error", jsonObject.get("status").getAsString());
                });
    }
}
