package lk.samarasingher_super.asset.invoice.controller;


import com.itextpdf.text.DocumentException;
import lk.samarasingher_super.asset.customer.service.CustomerService;
import lk.samarasingher_super.asset.discount_ratio.service.DiscountRatioService;
import lk.samarasingher_super.asset.invoice.entity.Invoice;
import lk.samarasingher_super.asset.invoice.entity.enums.InvoicePrintOrNot;
import lk.samarasingher_super.asset.invoice.entity.enums.InvoiceValidOrNot;
import lk.samarasingher_super.asset.invoice.entity.enums.PaymentMethod;
import lk.samarasingher_super.asset.invoice.service.InvoiceService;
import lk.samarasingher_super.asset.invoice_ledger.entity.InvoiceLedger;
import lk.samarasingher_super.asset.ledger.controller.LedgerController;
import lk.samarasingher_super.asset.ledger.entity.Ledger;
import lk.samarasingher_super.asset.ledger.service.LedgerService;
import lk.samarasingher_super.util.service.DateTimeAgeService;
import lk.samarasingher_super.util.service.MakeAutoGenerateNumberService;
import lk.samarasingher_super.util.service.TwilioMessageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Controller
@RequestMapping( "/invoice" )
public class InvoiceController {
  private final InvoiceService invoiceService;
  private final CustomerService customerService;
  private final LedgerService ledgerService;
  private final DateTimeAgeService dateTimeAgeService;
  private final DiscountRatioService discountRatioService;
  private final MakeAutoGenerateNumberService makeAutoGenerateNumberService;
  private final TwilioMessageService twilioMessageService;

  public InvoiceController(InvoiceService invoiceService, CustomerService customerService,
                           LedgerService ledgerService, DateTimeAgeService dateTimeAgeService,
                           DiscountRatioService discountRatioService,
                           MakeAutoGenerateNumberService makeAutoGenerateNumberService,
                           TwilioMessageService twilioMessageService) {
    this.invoiceService = invoiceService;
    this.customerService = customerService;
    this.ledgerService = ledgerService;
    this.dateTimeAgeService = dateTimeAgeService;
    this.discountRatioService = discountRatioService;
    this.makeAutoGenerateNumberService = makeAutoGenerateNumberService;
    this.twilioMessageService = twilioMessageService;
  }

  @GetMapping
  public String invoice(Model model) {
    model.addAttribute("invoices",
            invoiceService.findAll());
    /*invoiceService.findByCreatedAtIsBetween(dateTimeAgeService.dateTimeToLocalDateStartInDay(dateTimeAgeService.getPastDateByMonth(3)), dateTimeAgeService.dateTimeToLocalDateEndInDay(LocalDate.now())));*/
    model.addAttribute("firstInvoiceMessage", true);
    return "invoice/invoice";
  }

  @GetMapping( "/search" )
  public String invoiceSearch(@RequestAttribute( "startDate" ) LocalDate startDate,
                              @RequestAttribute( "endDate" ) LocalDate endDate, Model model) {
    model.addAttribute("invoices",
            invoiceService.findByCreatedAtIsBetween(dateTimeAgeService.dateTimeToLocalDateStartInDay(startDate), dateTimeAgeService.dateTimeToLocalDateEndInDay(endDate)));
    model.addAttribute("firstInvoiceMessage", true);
    return "invoice/invoice";
  }

  private String common(Model model, Invoice invoice) {
    model.addAttribute("invoice", invoice);
    model.addAttribute("invoicePrintOrNots", InvoicePrintOrNot.values());
    model.addAttribute("paymentMethods", PaymentMethod.values());
    model.addAttribute("customers", customerService.findAll());
    model.addAttribute("discountRatios", discountRatioService.findAll());
    model.addAttribute("ledgerItemURL", MvcUriComponentsBuilder
            .fromMethodName(LedgerController.class, "findId", "")
            .build()
            .toString());
    System.out.println("Sixe" + ledgerService.findAll().size());
    //send not expired and not zero quantity
    model.addAttribute("ledgers", ledgerService.findAll()
            .stream()
            .filter(x -> 0 < Integer.parseInt(x.getQuantity()) && x.getExpiredDate().isAfter(LocalDate.now()))
            .collect(Collectors.toList()));
    return "invoice/addInvoice";
  }

  @GetMapping( "/add" )
  public String getInvoiceForm(Model model) {
    return common(model, new Invoice());
  }

  @GetMapping( "/{id}" )
  public String viewDetails(@PathVariable Integer id, Model model) {
    Invoice invoice = invoiceService.findById(id);
    model.addAttribute("invoiceDetail", invoice);
    model.addAttribute("customerDetail", invoice.getCustomer());
    return "invoice/invoice-detail";
  }

  @PostMapping
  public String persistInvoice(@Valid @ModelAttribute Invoice invoice, BindingResult bindingResult, Model model) {
    if ( bindingResult.hasErrors() ) {
      return common(model, invoice);
    }
    if ( invoice.getId() == null ) {
      if ( invoiceService.findByLastInvoice() == null ) {
        //need to generate new one
        invoice.setCode("SSCI" + makeAutoGenerateNumberService.numberAutoGen(null).toString());
      } else {

        //if there is customer in db need to get that customer's code and increase its value
        String previousCode = invoiceService.findByLastInvoice().getCode().substring(4);
        invoice.setCode("SSCI" + makeAutoGenerateNumberService.numberAutoGen(previousCode).toString());
      }
    }
    invoice.setInvoiceValidOrNot(InvoiceValidOrNot.VALID);
    Invoice saveInvoice = invoiceService.persist(invoice);

    for ( InvoiceLedger invoiceLedger : saveInvoice.getInvoiceLedgers() ) {
      Ledger ledger = ledgerService.findById(invoiceLedger.getLedger().getId());
      String quantity = invoiceLedger.getQuantity();
      int availableQuantity = Integer.parseInt(ledger.getQuantity());
      int sellQuantity = Integer.parseInt(quantity);
      ledger.setQuantity(String.valueOf(availableQuantity - sellQuantity));
      ledgerService.persist(ledger);
    }
    if ( saveInvoice.getCustomer() != null ) {
      try {
        twilioMessageService.sendSMS(saveInvoice.getCustomer().getMobile(), "Thank You Come Again \n Samarasinghe Super ");
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return "redirect:/invoice/fileView/"+saveInvoice.getId();
  }


  @GetMapping( "/remove/{id}" )
  public String removeInvoice(@PathVariable( "id" ) Integer id) {
    Invoice invoice = invoiceService.findById(id);
    invoice.setInvoiceValidOrNot(InvoiceValidOrNot.NOTVALID);
    invoiceService.persist(invoice);
    return "redirect:/invoice";
  }

  @GetMapping(value = "/file/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity< InputStreamResource > invoicePrint(@PathVariable("id")Integer id) throws DocumentException {
    var headers = new HttpHeaders();
    headers.add("Content-Disposition", "inline; filename=invoice.pdf");
    return ResponseEntity
        .ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_PDF)
        .body(new InputStreamResource(invoiceService.createPDF(id)));
  }

  @GetMapping("/fileView/{id}")
  public String fileRequest(@PathVariable("id") Integer id, Model model, HttpServletRequest request) {
    String fullPath = request.getServletContext().getRealPath("/invoice/file/" + id);
    model.addAttribute("pdfFile",fullPath);
    return "invoice/pdfSilentPrint";
  }

}
