package sber.controller;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sber.exceptions.NotEnoughMoneyException;
import sber.service.MoneyTransferService;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/api")
public class BankController {

    private MoneyTransferService moneyTransferService;

    @Autowired
    public BankController(MoneyTransferService moneyTransferService) {
        this.moneyTransferService = moneyTransferService;
    }

    @RequestMapping(value = "/topUp", method = RequestMethod.POST)
    public @ResponseBody String putMoney(@RequestParam long id, @RequestParam BigDecimal value, HttpServletResponse response) {
        try {
            moneyTransferService.topUp(id, value);
            response.setStatus(200);
            return successJson(id, value).toString();
        } catch (Exception e) {
            response.setStatus(400);
            return errorJson().toString();
        }
    }
    
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    public @ResponseBody String takeMoney(@RequestParam long id, @RequestParam BigDecimal value, HttpServletResponse response) {
        try {
            moneyTransferService.withdraw(id, value);
            response.setStatus(200);
            return successJson(id, value).toString();
        } catch (NotEnoughMoneyException e) {
            response.setStatus(400);
            return errorJson("Not enough money.").toString();
        } catch (Exception e) {
            response.setStatus(400);
            return errorJson().toString();
        }
    }

    private JsonObject errorJson(String description){
        final JsonObject errorJson = new JsonObject();
        errorJson.addProperty("status","error");
        errorJson.addProperty("description",description);
        return errorJson;
    }

    private JsonObject errorJson(){
        final JsonObject errorJson = new JsonObject();
        errorJson.addProperty("status","error");
        return errorJson;
    }

    private JsonObject successJson(long userId, BigDecimal value) {
        final JsonObject errorJson = new JsonObject();
        errorJson.addProperty("status","ok");
        errorJson.addProperty("userId", userId);
        errorJson.addProperty("value", value);
        return errorJson;
    }
}
