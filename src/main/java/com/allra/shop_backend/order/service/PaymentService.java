package com.allra.shop_backend.order.service;

import com.allra.shop_backend.order.entity.Order;
import com.allra.shop_backend.order.entity.PaymentLog;
import com.allra.shop_backend.order.enums.PaymentStatus;
import com.allra.shop_backend.order.payload.PaymentApiRequest;
import com.allra.shop_backend.order.payload.PaymentApiResponse;
import com.allra.shop_backend.order.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final WebClient webClient;
    private final PaymentLogRepository paymentLogRepository;

    public PaymentApiResponse processPayment(Order order) {
        PaymentLog paymentLog = new PaymentLog(order.getUser().getId(), order.getId(), order.getTotalPayment());

        try {
            // 외부 결제 API 호출
            PaymentApiResponse response = webClient
                    .post()
                    .uri("/api/v1/payment")
                    .bodyValue(new PaymentApiRequest(String.valueOf(order.getId()), order.getTotalPayment()))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::is5xxServerError,
                            clientResponse -> clientResponse.bodyToMono(PaymentApiResponse.class)
                                    .map(body -> {
                                        paymentLog.updateStatus(body.transactionId(), body.message(), PaymentStatus.FAILED);
                                        paymentLogRepository.save(paymentLog);
                                        return new RuntimeException("결제 실패하였습니다. : " + body.message());
                                    })
                    )
                    .bodyToMono(PaymentApiResponse.class)
                    .block();

            if (response != null && "SUCCESS".equals(response.status())) {
                paymentLog.updateStatus(response.transactionId(), response.message(), PaymentStatus.SUCCESS);
                paymentLogRepository.save(paymentLog);
            }

            return response;

        } catch (WebClientException ex) {
            throw new RuntimeException("결제에 실패하였습니다.", ex);
        }
    }
}
