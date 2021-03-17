package lk.samarasingher_super.asset.invoice.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lk.samarasingher_super.asset.common_asset.model.enums.LiveDead;
import lk.samarasingher_super.asset.employee.entity.Employee;
import lk.samarasingher_super.asset.invoice.dao.InvoiceDao;
import lk.samarasingher_super.asset.invoice.entity.Invoice;
import lk.samarasingher_super.asset.invoice_ledger.entity.InvoiceLedger;
import lk.samarasingher_super.util.interfaces.AbstractService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService implements AbstractService< Invoice, Integer > {
  private final InvoiceDao invoiceDao;

  public InvoiceService(InvoiceDao invoiceDao) {
    this.invoiceDao = invoiceDao;
  }


  public List< Invoice > findAll() {
    return invoiceDao.findAll().stream()
        .filter(x -> LiveDead.ACTIVE.equals(x.getLiveDead()))
        .collect(Collectors.toList());
  }

  public Invoice findById(Integer id) {
    return invoiceDao.getOne(id);
  }

  public Invoice persist(Invoice invoice) {
    if ( invoice.getId() == null ) {
      invoice.setLiveDead(LiveDead.ACTIVE);
    }
    return invoiceDao.save(invoice);
  }

  public boolean delete(Integer id) {
    Invoice invoice = invoiceDao.getOne(id);
    invoice.setLiveDead(LiveDead.STOP);
    invoiceDao.save(invoice);
    return false;
  }

  public List< Invoice > search(Invoice invoice) {
    ExampleMatcher matcher = ExampleMatcher
        .matching()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
    Example< Invoice > invoiceExample = Example.of(invoice, matcher);
    return invoiceDao.findAll(invoiceExample);

  }

  public List< Invoice > findByCreatedAtIsBetween(LocalDateTime from, LocalDateTime to) {
    return invoiceDao.findByCreatedAtIsBetween(from, to);
  }

  public Invoice findByLastInvoice() {
    return invoiceDao.findFirstByOrderByIdDesc();
  }

  public List< Invoice > findByCreatedAtIsBetweenAndCreatedBy(LocalDateTime from, LocalDateTime to, String userName) {
    return invoiceDao.findByCreatedAtIsBetweenAndCreatedBy(from, to, userName);
  }

  public ByteArrayInputStream createPDF(Integer id) throws DocumentException {
    Invoice invoice = invoiceDao.getOne(id);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4, 15, 15, 45, 30);
    PdfWriter.getInstance(document, out);
    document.open();

    Font mainFont = FontFactory.getFont("Arial", 10, BaseColor.BLACK);
    Font secondaryFont = FontFactory.getFont("Arial", 8, BaseColor.BLACK);


    Paragraph paragraph = new Paragraph("Samarasingher Super Center \n \t\t Welipillewa\n \t Kadawatha \n", mainFont);
    paragraph.setAlignment(Element.ALIGN_CENTER);
    paragraph.setIndentationLeft(50);
    paragraph.setIndentationRight(50);
    paragraph.setSpacingAfter(10);
    document.add(paragraph);
//customer details and invoice main details
    float[] columnWidths = {200f, 200f};//column amount{column 1 , column 2 }
    PdfPTable mainTable = new PdfPTable(columnWidths);
    // add cell to table
    PdfPCell cell = new PdfPCell(new Phrase("Date : \t" + invoice.getCreatedAt().format(DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss")), secondaryFont));
    pdfCellBodyCommonStyle(cell);
    mainTable.addCell(cell);

    PdfPCell cell1 = new PdfPCell(new Phrase("Invoice Number : " + invoice.getCode(), secondaryFont));
    pdfCellBodyCommonStyle(cell1);
    mainTable.addCell(cell1);

    PdfPCell cell2;
    if ( invoice.getCustomer() != null ) {
      cell2 =
          new PdfPCell(new Phrase("Name : " + invoice.getCustomer().getTitle().getTitle() + " " + invoice.getCustomer().getName(), secondaryFont));
    } else {
      cell2 = new PdfPCell(new Phrase("Name : Anymouse Customer ", secondaryFont));
    }
    pdfCellBodyCommonStyle(cell2);
    mainTable.addCell(cell2);

    document.add(mainTable);

    PdfPTable legderItemDisplay = new PdfPTable(5);//column amount
    legderItemDisplay.setWidthPercentage(100);
    legderItemDisplay.setSpacingBefore(10f);
    legderItemDisplay.setSpacingAfter(10);

    Font tableHeader = FontFactory.getFont("Arial", 10, BaseColor.BLACK);
    legderItemDisplay.setWidths(columnWidths);

    PdfPCell indexHeader = new PdfPCell(new Paragraph("Index", tableHeader));
    pdfCellHeaderCommonStyle(indexHeader);
    legderItemDisplay.addCell(indexHeader);

    PdfPCell itemNameHeader = new PdfPCell(new Paragraph("Item Name", tableHeader));
    pdfCellHeaderCommonStyle(itemNameHeader);
    legderItemDisplay.addCell(itemNameHeader);

    PdfPCell unitPriceHeader = new PdfPCell(new Paragraph("Unit Price", tableHeader));
    pdfCellHeaderCommonStyle(unitPriceHeader);
    legderItemDisplay.addCell(unitPriceHeader);

    PdfPCell quantityHeader = new PdfPCell(new Paragraph("Quantity", tableHeader));
    pdfCellHeaderCommonStyle(quantityHeader);
    legderItemDisplay.addCell(quantityHeader);

    PdfPCell lineTotalHeader = new PdfPCell(new Paragraph("Line Total", tableHeader));
    pdfCellHeaderCommonStyle(lineTotalHeader);
    legderItemDisplay.addCell(lineTotalHeader);

    for ( int i = 0; i < invoice.getInvoiceLedgers().size(); i++ ) {
      PdfPCell index = new PdfPCell(new Paragraph(Integer.toString(i), tableHeader));
      pdfCellBodyCommonStyle(index);
      legderItemDisplay.addCell(index);

      PdfPCell itemName = new PdfPCell(new Paragraph(invoice.getInvoiceLedgers().get(i).getLedger().getItem().getName(), tableHeader));
      pdfCellBodyCommonStyle(itemName);
      legderItemDisplay.addCell(itemName);

      PdfPCell unitPrice = new PdfPCell(new Paragraph(invoice.getInvoiceLedgers().get(i).getLedger().getSellPrice().toString(), tableHeader));
      pdfCellBodyCommonStyle(unitPrice);
      legderItemDisplay.addCell(unitPrice);

      PdfPCell quantity = new PdfPCell(new Paragraph(invoice.getInvoiceLedgers().get(i).getQuantity(), tableHeader));
      pdfCellBodyCommonStyle(quantity);
      legderItemDisplay.addCell(quantity);

      PdfPCell lineTotal = new PdfPCell(new Paragraph(invoice.getInvoiceLedgers().get(i).getLineTotal().toString(), tableHeader));
      pdfCellBodyCommonStyle(lineTotal);
      legderItemDisplay.addCell(lineTotal);
    }

    document.add(legderItemDisplay);

    PdfPTable invoiceDetails = new PdfPTable(5);//column amount
    invoiceDetails.setWidthPercentage(100);
    invoiceDetails.setSpacingBefore(10f);
    invoiceDetails.setSpacingAfter(10);

    PdfPCell indexColumn = new PdfPCell(new Paragraph("", tableHeader));
    pdfCellHeaderCommonStyle(indexColumn);
    legderItemDisplay.addCell(indexColumn);

    PdfPCell itemNameColumn = new PdfPCell(new Paragraph("", tableHeader));
    pdfCellHeaderCommonStyle(itemNameColumn);
    legderItemDisplay.addCell(itemNameColumn);

    PdfPCell unitPriceColumn = new PdfPCell(new Paragraph("", tableHeader));
    pdfCellHeaderCommonStyle(unitPriceColumn);
    legderItemDisplay.addCell(unitPriceColumn);

    PdfPCell quantityColumn = new PdfPCell(new Paragraph("Quantity", tableHeader));
    pdfCellHeaderCommonStyle(quantityColumn);
    //todo
    legderItemDisplay.addCell(quantityColumn);

    PdfPCell lineTotalColumn = new PdfPCell(new Paragraph("Line Total", tableHeader));
    pdfCellHeaderCommonStyle(lineTotalColumn);
    legderItemDisplay.addCell(lineTotalColumn);

document.add(invoiceDetails);

    document.close();
    return new ByteArrayInputStream(out.toByteArray());
  }

  private void pdfCellHeaderCommonStyle(PdfPCell pdfPCell) {
    pdfPCell.setBorderColor(BaseColor.BLACK);
    pdfPCell.setPaddingLeft(10);
    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
    pdfPCell.setBackgroundColor(BaseColor.DARK_GRAY);
    pdfPCell.setExtraParagraphSpace(5f);
  }

  private void pdfCellBodyCommonStyle(PdfPCell pdfPCell) {
    pdfPCell.setBorderColor(BaseColor.BLACK);
    pdfPCell.setPaddingLeft(10);
    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
    pdfPCell.setBackgroundColor(BaseColor.WHITE);
    pdfPCell.setExtraParagraphSpace(5f);
  }
}
