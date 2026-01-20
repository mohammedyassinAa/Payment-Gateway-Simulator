package com.pfa.paymentgateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for demo UI pages.
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    /**
     * Display payment form for testing.
     */
    @GetMapping("/payment")
    public String paymentForm() {
        return "payment";
    }

    /**
     * Display payment result page.
     */
    @GetMapping("/result")
    public String resultPage() {
        return "result";
    }
}

/**
 * Controller for root path.
 */
@Controller
class RootController {

    /**
     * Redirect root path to demo payment page.
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/demo/payment";
    }
}
