package com.example.apigateway.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

/**
 * Thrown when the sum of selected line‐item prices doesn't match the provided total.
 * Spring will return 400 Bad Request with the exception’s message as the response body.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OrderPriceMismatchException extends RuntimeException {
    public OrderPriceMismatchException(BigDecimal calculatedTotal, BigDecimal providedTotal) {
        super(String.format(
                "THE PRICE OF THE ITEMS YOU SELECTED (%s) DOES NOT MATCH THE AMOUNT THAT YOU INPUT (%s)",
                calculatedTotal.toPlainString(),
                providedTotal.toPlainString()
        ));
    }
}
