package com.invenza.services;

import com.invenza.dto.BillDTO;
import com.invenza.dto.BillItemDTO;
import com.invenza.dto.QuotationDTO;
import com.invenza.dto.QuotationItemDTO;
import com.invenza.entities.Product;
import com.invenza.repositories.ProductRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class PDFService {

    private final ProductRepository productRepository;
    private BillDTO billDTO;

    public PDFService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // -------------------- Bill PDF --------------------
    public byte[] generateBillPDF(BillDTO bill) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 30, 20, 40); // margins
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new PageBorderEvent()); // border + footer + page numbers
        document.open();

        // ---- Fonts ----
        Font headerFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font tableFont = new Font(Font.HELVETICA, 9);
        Font boldTableFont = new Font(Font.HELVETICA, 9, Font.BOLD);

        // ---- Seller Info ----
        Paragraph sellerPara1 = new Paragraph("SANTOSH TRADERS", new Font(Font.HELVETICA, 25, Font.BOLD));
        Paragraph sellerPara2 = new Paragraph("Main Road, Birdev Nagar, Rendal-416203\nPhone: 8087365990 | Email: omahajan723@gmail.com\nGSTIN: 27AALFC1094M1Z2", new Font(Font.HELVETICA, 10));
        sellerPara1.setAlignment(Element.ALIGN_CENTER);
        sellerPara2.setAlignment(Element.ALIGN_CENTER);
        sellerPara1.setSpacingAfter(5);
        sellerPara2.setSpacingAfter(8);
        document.add(sellerPara1);
        document.add(sellerPara2);

        // ---- Invoice Info ----
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{2f, 2f, 2f, 2f});
        infoTable.addCell(infoCell("Invoice No:", bill.getId() != null ? "BILL-" + bill.getId() : "BILL-DRAFT"));
        infoTable.addCell(infoCell("Place of Supply:", "Maharashtra (27)"));
        infoTable.addCell(infoCell("Date of Invoice:", bill.getBillDate().toString()));
        infoTable.addCell(infoCell("", ""));
        document.add(infoTable);

        // ---- Title ----
        Paragraph header = new Paragraph("TAX INVOICE", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingBefore(10);
        header.setSpacingAfter(10);
        document.add(header);

        // ---- Customer Info ----
        PdfPTable partyTable = new PdfPTable(2);
        partyTable.setWidthPercentage(100);
        PdfPCell billedTo = new PdfPCell(new Phrase("Billed To:\n" + bill.getCustomerName() + "\n" + bill.getCustomerAddress() + "\nGSTIN: " + bill.getCustomerGSTIN(), tableFont));
        PdfPCell shippedTo = new PdfPCell(new Phrase("Shipped To:\n" + bill.getCustomerName() + "\n" + bill.getCustomerAddress() + "\nGSTIN: " + bill.getCustomerGSTIN(), tableFont));
        billedTo.setPadding(8);
        shippedTo.setPadding(8);
        partyTable.addCell(billedTo);
        partyTable.addCell(shippedTo);
        partyTable.setSpacingAfter(12);
        document.add(partyTable);

        // ---- Items Table ----
        PdfPTable itemsTable = new PdfPTable(13);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{0.8f, 3f, 1.35f, 0.8f, 0.85f, // SN, Desc, HSN, Qty, Unit
                1.2f, 1.3f, 1.5f,           // MRP, Selling, Discount
                1.1f, 1.1f, 1.1f, 1.1f, 1.4f    // CGST%, CGST Amt, SGST%, SGST Amt, Total
        });

        String[] headers = {"No.", "Description ", "HSN", "Qty", "Unit", "MRP Price", "Selling Price", "Discount", "CGST %", "CGST Amt", "SGST %", "SGST Amt", "Total"};
        for (String h : headers) itemsTable.addCell(headerCell(h));

        // Totals initialization
        double taxable = 0, cgstTotal = 0, sgstTotal = 0;
        int slNo = 1;

        for (BillItemDTO item : bill.getItems()) {
            PdfPCell cell;

            double mrpPrice = item.getMrpPrice();
            double sellingPrice = item.getDiscountedPrice();
            double discount = mrpPrice - sellingPrice;
            double gstAmt = item.getGstAmount();
            double cgstAmt = gstAmt / 2;
            double sgstAmt = gstAmt / 2;
            double cgstRate = (cgstAmt / sellingPrice) * 100;
            double sgstRate = (sgstAmt / sellingPrice) * 100;
            double total = item.getTotalFinalPrice();

            taxable += sellingPrice * item.getQuantity();
            cgstTotal += cgstAmt;
            sgstTotal += sgstAmt;

            // S.N.
            cell = new PdfPCell(new Phrase(String.valueOf(slNo++), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(cell);

            // Description
            itemsTable.addCell(new PdfPCell(new Phrase(item.getProductName(), tableFont)));

            // HSN
            itemsTable.addCell(new PdfPCell(new Phrase(item.getHsnCode(), tableFont)));

            // Quantity
            cell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);

            // Unit
            String unit = item.getProduct() != null ? item.getProduct().getUnit() : "PCS";
            itemsTable.addCell(new PdfPCell(new Phrase(unit, tableFont)));

            // MRP Price
            cell = new PdfPCell(new Phrase(String.format("%.2f", mrpPrice), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);

            // Selling Price
            cell = new PdfPCell(new Phrase(String.format("%.2f", sellingPrice), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);

            // Discount
            cell = new PdfPCell(new Phrase(String.format("%.2f", discount), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);

            // CGST %
            cell = new PdfPCell(new Phrase(String.format("%.2f", cgstRate), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);

            // CGST Amt
            cell = new PdfPCell(new Phrase(String.format("%.2f", cgstAmt), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);

            // SGST %
            cell = new PdfPCell(new Phrase(String.format("%.2f", sgstRate), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);

            // SGST Amt
            cell = new PdfPCell(new Phrase(String.format("%.2f", sgstAmt), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);

            // Total
            cell = new PdfPCell(new Phrase(String.format("%.2f", total), tableFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            itemsTable.addCell(cell);
        }

        itemsTable.setSpacingAfter(10);
        document.add(itemsTable);

        // ---- Totals Section ----
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(40);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        totalsTable.addCell(totalCell("Taxable Amount:"));
        totalsTable.addCell(totalCell("₹" + String.format("%.2f", taxable)));
        totalsTable.addCell(totalCell("CGST Total:"));
        totalsTable.addCell(totalCell("₹" + String.format("%.2f", cgstTotal)));
        totalsTable.addCell(totalCell("SGST Total:"));
        totalsTable.addCell(totalCell("₹" + String.format("%.2f", sgstTotal)));
        totalsTable.addCell(totalCell("Grand Total:"));
        totalsTable.addCell(totalCell("₹" + String.format("%.2f", bill.getFinalAmount())));

        document.add(totalsTable);

        document.close();
        return baos.toByteArray();
    }

    // -------------------- Quotation PDF --------------------
    public byte[] generateQuotationPDF(QuotationDTO quotation) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new PageBorderEvent());
        document.open();

        // Seller Info
        Paragraph sellerPara1 = new Paragraph("SANTOSH TRADERS", new Font(Font.HELVETICA, 25, Font.BOLD));
        Paragraph sellerPara2 = new Paragraph("Main Road, Birdev Nagar, Rendal-416203\nPhone: 8087365990 | Email: omahajan723@gmail.com\nGSTIN: 27AALFC1094M1Z2", new Font(Font.HELVETICA, 10, Font.BOLD));
        sellerPara1.setAlignment(Element.ALIGN_CENTER);
        sellerPara2.setAlignment(Element.ALIGN_CENTER);
        sellerPara1.setSpacingAfter(10);
        sellerPara2.setSpacingAfter(10);
        document.add(sellerPara1);
        document.add(sellerPara2);

        // Title
        Paragraph header = new Paragraph("QUOTATION", new Font(Font.HELVETICA, 14, Font.BOLD));
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(12);
        document.add(header);

        // Customer Info
        PdfPTable partyTable = new PdfPTable(2);
        partyTable.setWidthPercentage(100);
        PdfPCell billedTo = new PdfPCell(new Phrase("Billed To:\n" + quotation.getCustomerName() + "\n" + quotation.getCustomerAddress() + "\nGSTIN: " + quotation.getCustomerGSTIN(), new Font(Font.HELVETICA, 9)));
        PdfPCell shippedTo = new PdfPCell(new Phrase("Shipped To:\n" + quotation.getCustomerName() + "\n" + quotation.getCustomerAddress() + "\nGSTIN: " + quotation.getCustomerGSTIN(), new Font(Font.HELVETICA, 9)));
        billedTo.setPadding(8);
        shippedTo.setPadding(8);
        partyTable.addCell(billedTo);
        partyTable.addCell(shippedTo);
        partyTable.setSpacingAfter(12);
        document.add(partyTable);

        // Items Table
        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.8f, 2.3f, 1.2f, 0.8f, 0.7f, 1f, 1.2f, 1.2f, 1.2f, 1.6f});
        String[] headers = {"S.N.", "Description", "HSN", "Qty", "Unit", "MRP", "Sell Price", "Discount", "GST %", "Total"};
        for (String h : headers) table.addCell(headerCell(h));

        int sn = 1;
        for (QuotationItemDTO item : quotation.getItems()) {
            Optional<Product> productOpt = item.getProductId() != null ? productRepository.findById(item.getProductId()) : Optional.empty();
            String unit = productOpt.map(Product::getUnit).orElse("PCS");
            String hsn = productOpt.map(Product::getHsnCode).orElse("-");
            double mrp = productOpt.map(Product::getPrice).orElse(item.getMrpPrice());
            double sellingPrice = item.getDiscountedPrice() != null ? item.getDiscountedPrice() : mrp;
            double discount = mrp - sellingPrice;
            double gstRate = productOpt.map(Product::getGstRate).orElse(18.0);
            double totalPrice = (sellingPrice + sellingPrice * gstRate / 100) * item.getQuantity();

            table.addCell(cell(sn++));
            table.addCell(cell(item.getProductName()));
            table.addCell(cell(hsn));
            table.addCell(cell(item.getQuantity()));
            table.addCell(cell(unit));
            table.addCell(cell(String.format("%.2f", mrp)));
            table.addCell(cell(String.format("%.2f", sellingPrice)));
            table.addCell(cell(String.format("%.2f", discount)));
            table.addCell(cell(String.format("%.1f%%", gstRate)));
            table.addCell(cell(String.format("%.2f", totalPrice)));
        }
        table.setSpacingAfter(10);
        document.add(table);

        // ---- Total Amount Section ----
        PdfPTable totalTable = new PdfPTable(1);
        totalTable.setWidthPercentage(30); // Only 30% of page width
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT); // Align right

        PdfPCell amountCell = new PdfPCell(new Phrase("Total Amount: " + String.format("%.2f", quotation.getTotalAmount()), new Font(Font.HELVETICA, 10, Font.BOLD)));
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        amountCell.setPadding(6);
        totalTable.addCell(amountCell);

        document.add(totalTable);

        document.close();
        return baos.toByteArray();
    }

    // -------------------- Helper Cells --------------------
    private PdfPCell cell(Object value) {
        PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(value), new Font(Font.HELVETICA, 9)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell headerCell(String value) {
        PdfPCell hCell = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 9, Font.BOLD)));
        hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        hCell.setPadding(5);
        hCell.setBackgroundColor(new Color(230, 230, 230));
        return hCell;
    }

    private PdfPCell infoCell(String label, String value) {
        PdfPCell cell = new PdfPCell(new Phrase(label + " " + value, new Font(Font.HELVETICA, 8)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell totalCell(String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    // -------------------- Page Border + Footer --------------------
    private static class PageBorderEvent extends PdfPageEventHelper {
        Font footerFont = new Font(Font.HELVETICA, 8);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            cb.setLineWidth(0.5f);

            // Border
            cb.rectangle(document.left() - 10, document.bottom() - 10, document.getPageSize().getWidth() - 18, document.getPageSize().getHeight() - 30);
            cb.stroke();

            // Footer Text (always at bottom of page)
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Terms & Conditions: Goods once sold will not be taken back. Subject to Ichalkaranji Jurisdiction only.", footerFont), document.left(), document.bottom() + 25, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Declaration: This document shows actual price & all particulars are true.", footerFont), document.left(), document.bottom() + 15, 0);

            // Page Number (centered)
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("" + writer.getPageNumber(), footerFont), (document.right() + document.left()) / 2, document.bottom() - 5, 0);
        }
    }
}
