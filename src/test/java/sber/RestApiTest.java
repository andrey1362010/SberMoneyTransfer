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
    public void test1() throws Exception {
        this.mvc.perform(post("/api/takeMoney?id=1&value=1")).andExpect(status().isOk())
                .andDo(mvcResult -> {
                    final String json = mvcResult.getResponse().getContentAsString();
                    final JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    Assert.assertEquals("ok", jsonObject.get("status").getAsString());
                    Assert.assertEquals(1, jsonObject.get("userId").getAsLong());
                });
    }

    @Test
    public void test2() throws Exception {
        this.mvc.perform(post("/api/putMoney?id=1&value=1")).andExpect(status().isOk())
                .andDo(mvcResult -> {
                    final String json = mvcResult.getResponse().getContentAsString();
                    final JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    Assert.assertEquals("ok", jsonObject.get("status").getAsString());
                    Assert.assertEquals(1, jsonObject.get("userId").getAsLong());
                });
    }

    @Test
    public void test3() throws Exception {
        this.mvc.perform(post("/api/takeMoney?value=1")).andExpect(status().is(400));
    }

    @Test
    public void test4() throws Exception {
        this.mvc.perform(post("/api/putMoney?id=1")).andExpect(status().is(400));
    }

    @Test
    public void test5() throws Exception {
        doThrow(IllegalStateException.class).when(moneyTransferService).takeMoney(any(Long.class), any(BigDecimal.class));
        this.mvc.perform(post("/api/takeMoney?id=1&value=1")).andExpect(status().is(400))
                .andDo(mvcResult -> {
                    final String json = mvcResult.getResponse().getContentAsString();
                    final JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    Assert.assertEquals("error", jsonObject.get("status").getAsString());
                });
    }
    @Test
    public void test6() throws Exception {
        doThrow(IllegalStateException.class).when(moneyTransferService).putMoney(any(Long.class), any(BigDecimal.class));
        this.mvc.perform(post("/api/putMoney?id=1&value=1")).andExpect(status().is(400))
                .andDo(mvcResult -> {
                    final String json = mvcResult.getResponse().getContentAsString();
                    final JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    Assert.assertEquals("error", jsonObject.get("status").getAsString());
                });
    }
}
