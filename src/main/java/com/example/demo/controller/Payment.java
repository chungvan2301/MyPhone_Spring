package com.example.demo.controller;

import com.example.demo.dto.OrderRequestDTO;
import com.example.demo.dto.StatusRequestDTO;
import com.example.demo.service.GetPaymentStatusService;
import com.example.demo.service.OrderPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Payment {
    @Autowired
    private GetPaymentStatusService getPaymentStatusService;
    @Autowired
    private OrderPaymentService orderPaymentService;
    @GetMapping("/api/v1/callback")
    public ResponseEntity<Map<String, Object>> doCallBack(@RequestParam Map<String, Object> callBackInfo) {

        System.out.println(callBackInfo);
        return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
    }

    @PostMapping("/api/v1/get-status")
    public ResponseEntity<Map<String, Object>> getStatus(HttpServletRequest request, @ModelAttribute StatusRequestDTO statusRequestDTO) throws IOException {
        Map<String, Object> result = getPaymentStatusService.getStatus(request, statusRequestDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @PostMapping("/api/v1/create-order")
    public ResponseEntity<Map<String, Object>> createOrderPayment(HttpServletRequest request, @ModelAttribute OrderRequestDTO orderRequestDTO) throws IOException {
        Map<String, Object> result = orderPaymentService.createOrder(request, orderRequestDTO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
