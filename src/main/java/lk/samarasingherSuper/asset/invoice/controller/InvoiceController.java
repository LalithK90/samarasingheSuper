package lk.samarasingherSuper.asset.invoice.controller;

import lk.samarasingherSuper.asset.invoice.entity.Invoice;
import lk.samarasingherSuper.asset.invoice.service.InvoiceService;
import lk.samarasingherSuper.util.interfaces.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping( "/invoice" )
public class InvoiceController implements AbstractController< Invoice, Integer > {
  private final InvoiceService invoiceService;

  @Autowired
  public InvoiceController(InvoiceService invoiceService) {
    this.invoiceService = invoiceService;
  }

  @GetMapping
  public String findAll(Model model) {
    model.addAttribute("invoices", invoiceService.findAll());
    return "invoice/invoice";
  }

  @GetMapping( "/{id}" )
  public String findById(@PathVariable("id") Integer id, Model model) {
    Invoice invoice =  invoiceService.findById(id);
    model.addAttribute("invoiceDetail",invoice);
    model.addAttribute("customerDetail",invoice.getCustomer());
    return "invoice/invoice-detail";
  }


  public String edit(Integer id, Model model) {
    return null;
  }


  public String persist(@Valid @ModelAttribute Invoice invoice, BindingResult bindingResult,
                        RedirectAttributes redirectAttributes, Model model) {
    return null;
  }


  public String delete(Integer id, Model model) {
    return null;
  }
}