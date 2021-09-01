package cn.alumik.shop.controller;


import cn.alumik.shop.entity.Item;
import cn.alumik.shop.entity.Refund;
import cn.alumik.shop.entity.RefundRequest;
import cn.alumik.shop.entity.Transaction;
import cn.alumik.shop.service.RefundRequestService;
import cn.alumik.shop.service.RefundService;
import cn.alumik.shop.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Controller
@RequestMapping("/transaction")
public class TransactionController {
    private TransactionService transactionService;
    private RefundRequestService refundRequestService;
    private RefundService refundService;

    public TransactionController(TransactionService transactionService, RefundRequestService refundRequestService, RefundService refundService, RefundService refundService1) {
        this.transactionService = transactionService;
        this.refundRequestService = refundRequestService;
        this.refundService = refundService1;
    }

    @GetMapping("/detail")
    public String actionDetailGetter(Model model, int id) {
        Transaction transaction = transactionService.getById(id);
        model.addAttribute("transaction", transaction);

        return "transaction/detail";
    }

    @GetMapping("/refund")
    public String actionRefundGetter(Model model, int id){
        Transaction transaction = transactionService.getById(id);
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setTransaction(transaction);
        refundRequest.setDealt(false);
        model.addAttribute("refund", refundRequest);
        return "transaction/refund";
    }

    @PostMapping("/refund")
    public String actionRefundPoster(@ModelAttribute("refund") RefundRequest refundRequest){
        refundRequest.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        refundRequestService.save(refundRequest);
        int id = refundRequest.getTransaction().getId();
        return "redirect:/transaction/detail?id=" + id;
    }

    @GetMapping("/dealRefund")
    public String actionDealRefundGetter(Model model, int id){
        RefundRequest refundRequest = refundRequestService.getById(id);
        model.addAttribute("refundRequest", refundRequest);
        return "transaction/dealRefund";
    }

    @PostMapping("/dealRefund")
    public String actionDealRefundPoster(@ModelAttribute("refundRequest")RefundRequest refundRequest,
                                         @RequestParam("agree") String agree,
                                         @RequestParam("agreeMoney") BigDecimal agreeMoney){
        refundRequest = refundRequestService.getById(refundRequest.getId());
        refundRequest.setDealt(true);
        if (agree.equals("agree")){
            Refund refund = new Refund();
            refundRequest.setRefund(refund);
            refund.setPrice(agreeMoney);
            refund.setRefundRequest(refundRequest);
            refund.setRefundedAt(new Timestamp(System.currentTimeMillis()));
            refundService.save(refund);
        }
        refundRequestService.save(refundRequest);

        int id = refundRequest.getTransaction().getItem().getId();
        return "redirect:/item/transactions?id=" + id;
    }

    @GetMapping("/refunds")
    public String actionRefundsGetter(Model model, int id){
        Transaction transaction = transactionService.getById(id);
        List<RefundRequest> refundRequests = refundRequestService.findAllByTransaction(transaction);
        model.addAttribute("refundRequests", refundRequests);
        Item item = transaction.getItem();
        model.addAttribute("item", item);
        return "transaction/refunds";
    }

    @GetMapping("/refundState")
    public String actionRefundStateGetter(Model model, int id) {
        Transaction transaction = transactionService.getById(id);
        List<RefundRequest> requests = refundRequestService.findAllByTransaction(transaction);
        model.addAttribute("requests", requests);
        model.addAttribute("transaction", transaction);
        return "transaction/refundState";
    }
}
