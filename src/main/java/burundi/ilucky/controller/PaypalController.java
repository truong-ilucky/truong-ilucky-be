package burundi.ilucky.controller;

import burundi.ilucky.jwt.JwtFilter;
import burundi.ilucky.jwt.JwtTokenProvider;
import burundi.ilucky.model.User;
import burundi.ilucky.service.PaypalService;
import burundi.ilucky.service.UserService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.PayerInfo;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;




@Controller
@RequestMapping("/api/paypal")
public class PaypalController {
    @Autowired
    private PaypalService paypalService;
    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;

    private final String cancelUrl = "http://localhost:8085/api/paypal/cancel";
    private final String successUrl = "http://localhost:8085/api/paypal/success";
    private final BigDecimal exchangeRate = new BigDecimal("25454");
    private final BigDecimal price = new BigDecimal("1000");

    @PostMapping("/create")
    public ResponseEntity<?> createPaypal(@RequestBody Map<String,String> params,
                                          @AuthenticationPrincipal UserDetails userDetails,
                                          HttpServletRequest request) {
        String token = jwtFilter.getJwtFromRequest(request);

        try {

            BigDecimal total = new BigDecimal(params.get("total"));
            BigDecimal quantity = total.divide(price,0,BigDecimal.ROUND_HALF_DOWN);
            String currency = params.get("currency");
            String successUrlWithToken = successUrl + "?jwt=" + token + "&quantity=" + quantity;
            String totalUSD = null;
            if("VND".equals(currency)) {
                BigDecimal result = total.divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);
                totalUSD = result.toString();
                currency = "USD";
            }
                Payment payment = paypalService.createPayment(totalUSD, currency, "paypal", "sale",
                        "paypal description", cancelUrl, successUrlWithToken);


                for (Links link : payment.getLinks()) {
                    if (link.getRel().equals("approval_url")) {
                        Map<String, String> response = new HashMap<>();
                        response.put("approvalUrl", link.getHref());
                        response.put("username", userDetails.getUsername());
                        return ResponseEntity.ok(response);
                    }
                }


                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Unable to create payment approval URL"));



        } catch (PayPalRESTException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/success")

    public String executePayment(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId,
                                 @RequestParam("jwt") String jwt, @RequestParam("quantity") int quantity, RedirectAttributes redirectAttributesx) {
        try {
            if(jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromJWT(jwt);
                Payment payment = paypalService.executePayment(paymentId, payerId);

                PayerInfo payerInfo = payment.getPayer().getPayerInfo();
                String payerName = payerInfo.getFirstName() + " " + payerInfo.getLastName();
                String totalAmount = payment.getTransactions().get(0).getAmount().getTotal();

                User user = userService.findByUserName(username);
                user.setTotalPlay(user.getTotalPlay() + quantity);
                userService.saveUser(user);


                return "success";
            }else {
                return "redirect:http://localhost:3000/";
            }

        } catch (PayPalRESTException e) {
            return "redirect:http://localhost:3000/";
        }
    }

        @GetMapping("/cancel")
        @ResponseBody
        public String cancelPayment(RedirectAttributes redirectAttributes) {
            return "redirect:http://localhost:3000/";
        }
        }


