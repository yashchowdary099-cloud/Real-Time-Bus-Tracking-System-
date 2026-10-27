package com.bustracking.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }

        model.addAttribute("status", statusCode);
        model.addAttribute("message", message != null && !message.toString().isEmpty() ? message : "An unexpected error occurred.");

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            return "error/403";
        }
        return "error/500";
    }

    @RequestMapping("/error/403")
    public String accessDenied(Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("message", "Access Denied: You do not have permission to view this resource.");
        return "error/403";
    }
}
