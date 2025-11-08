package com.invenza.services;

import com.invenza.dto.*;
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

    public PDFService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // -----------------------------------------------------
    // 🧾 BILL PDF GENERATION
    // -----------------------------------------------------
    public byte[] generateBillPDF(BillDTO bill) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 30, 20, 40);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new PageBorderEvent());
        document.open();

        // ---- Fonts ----
        Font titleFont = new Font(Font.HELVETICA, 25, Font.BOLD);
        Font headerFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font subFont = new Font(Font.HELVETICA, 10);
        Font tableFont = new Font(Font.HELVETICA, 9);
        Font boldTableFont = new Font(Font.HELVETICA, 9, Font.BOLD);

        // ---- Header ----
        Paragraph sellerName = new Paragraph("YASH TRADERS", titleFont);
        sellerName.setAlignment(Element.ALIGN_CENTER);
        sellerName.setSpacingAfter(4);

        Paragraph sellerDetails = new Paragraph(
                "Main Road, Ramnagar, Rendal - 416203\n" +
                        "Phone: 7666285594 | Email: yashadashettygdsc@gmail.com\n" +
                        "GSTIN: 27AOJPE7732Z2K1", subFont);
        sellerDetails.setAlignment(Element.ALIGN_CENTER);
        sellerDetails.setSpacingAfter(8);

        document.add(sellerName);
        document.add(sellerDetails);

        // ---- Invoice Info ----
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{2f, 2f, 2f, 2f});
        infoTable.addCell(infoCell("Invoice No:", bill.getId() != null ? "BILL-" + bill.getId() : "BILL-DRAFT"));
        infoTable.addCell(infoCell("Place of Supply:", "Maharashtra (27)"));
        infoTable.addCell(infoCell("Date of Invoice:", bill.getBillDate().toString()));
        infoTable.addCell(infoCell("", ""));
        infoTable.setSpacingAfter(10);
        document.add(infoTable);

        // ---- Title ----
        Paragraph invoiceTitle = new Paragraph("TAX INVOICE", headerFont);
        invoiceTitle.setAlignment(Element.ALIGN_CENTER);
        invoiceTitle.setSpacingBefore(8);
        invoiceTitle.setSpacingAfter(10);
        document.add(invoiceTitle);

        // ---- Customer Info ----
        String gstinText = (bill.getCustomerGSTIN() != null && !bill.getCustomerGSTIN().trim().isEmpty())
                ? "\nGSTIN: " + bill.getCustomerGSTIN()
                : "";

        PdfPTable partyTable = new PdfPTable(2);
        partyTable.setWidthPercentage(100);

        PdfPCell billedTo = new PdfPCell(new Phrase(
                "Billed To:\n" + bill.getCustomerName() + "\n" +
                        bill.getCustomerAddress() + gstinText, tableFont));
        billedTo.setPadding(8);

        PdfPCell shippedTo = new PdfPCell(new Phrase(
                "Shipped To:\n" + bill.getCustomerName() + "\n" +
                        bill.getCustomerAddress() + gstinText, tableFont));
        shippedTo.setPadding(8);

        partyTable.addCell(billedTo);
        partyTable.addCell(shippedTo);
        partyTable.setSpacingAfter(12);
        document.add(partyTable);

        // ---- Items Table ----
        PdfPTable itemsTable = new PdfPTable(13);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{
                0.8f, 3f, 1.35f, 0.8f, 0.85f, // SN, Desc, HSN, Qty, Unit
                1.2f, 1.3f, 1.5f,             // MRP, Selling, Discount
                1.1f, 1.1f, 1.1f, 1.1f, 1.4f  // CGST%, CGST Amt, SGST%, SGST Amt, Total
        });

        String[] headers = {
                "No.", "Description", "HSN", "Qty", "Unit",
                "MRP Price", "Selling Price", "Discount",
                "CGST %", "CGST Amt", "SGST %", "SGST Amt", "Total"
        };
        for (String h : headers) itemsTable.addCell(headerCell(h));

        double taxable = 0, cgstTotal = 0, sgstTotal = 0;
        int slNo = 1;

        for (BillItemDTO item : bill.getItems()) {
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

            itemsTable.addCell(cell(String.valueOf(slNo++)));
            itemsTable.addCell(cell(item.getProductName()));
            itemsTable.addCell(cell(item.getHsnCode()));
            itemsTable.addCell(rightCell(item.getQuantity()));
            itemsTable.addCell(cell(item.getProduct() != null ? item.getProduct().getUnit() : "PCS"));
            itemsTable.addCell(rightCell(String.format("%.2f", mrpPrice)));
            itemsTable.addCell(rightCell(String.format("%.2f", sellingPrice)));
            itemsTable.addCell(rightCell(String.format("%.2f", discount)));
            itemsTable.addCell(rightCell(String.format("%.2f", cgstRate)));
            itemsTable.addCell(rightCell(String.format("%.2f", cgstAmt)));
            itemsTable.addCell(rightCell(String.format("%.2f", sgstRate)));
            itemsTable.addCell(rightCell(String.format("%.2f", sgstAmt)));
            itemsTable.addCell(rightCell(String.format("%.2f", total)));
        }

        itemsTable.setSpacingAfter(10);
        document.add(itemsTable);

        // ---- Totals Section ----
        PdfPTable totalsTable = new PdfPTable(2);
        totalsTable.setWidthPercentage(40);
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        totalsTable.addCell(totalCell("Taxable Amount:"));
        totalsTable.addCell(totalValue("₹" + String.format("%.2f", taxable)));
        totalsTable.addCell(totalCell("CGST Total:"));
        totalsTable.addCell(totalValue("₹" + String.format("%.2f", cgstTotal)));
        totalsTable.addCell(totalCell("SGST Total:"));
        totalsTable.addCell(totalValue("₹" + String.format("%.2f", sgstTotal)));
        totalsTable.addCell(totalCell("Grand Total:"));
        totalsTable.addCell(totalValue("₹" + String.format("%.2f", bill.getFinalAmount())));

        document.add(totalsTable);

        document.close();
        return baos.toByteArray();
    }

    // -----------------------------------------------------
    // 🧾 QUOTATION PDF GENERATION
    // -----------------------------------------------------
    public byte[] generateQuotationPDF(QuotationDTO quotation) throws DocumentException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 20, 20, 20, 20);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new PageBorderEvent());
        document.open();

        // Seller Info
        Paragraph sellerPara2 = new Paragraph("YASH TRADERS", new Font(Font.HELVETICA, 25, Font.BOLD));
         Paragraph sellerDetails = new Paragraph(
                "Main Road, Ramnagar, Rendal - 416203\n" +
                        "Phone: 7666285594 | Email: yashadashettygdsc@gmail.com\n" +
                        "GSTIN: 27AOJPE7732Z2K1", subFont);
                new Font(Font.HELVETICA, 10, Font.BOLD));
        sellerPara1.setAlignment(Element.ALIGN_CENTER);
        sellerPara2.setAlignment(Element.ALIGN_CENTER);
        sellerPara1.setSpacingAfter(10);
        sellerPara2.setSpacingAfter(10);
        document.add(sellerPara1);
        document.add(sellerPara2);

        Paragraph header = new Paragraph("QUOTATION", new Font(Font.HELVETICA, 14, Font.BOLD));
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(12);
        document.add(header);

        String gstinText = (quotation.getCustomerGSTIN() != null && !quotation.getCustomerGSTIN().trim().isEmpty())
                ? "\nGSTIN: " + quotation.getCustomerGSTIN()
                : "";

        PdfPTable partyTable = new PdfPTable(2);
        partyTable.setWidthPercentage(100);
        PdfPCell billedTo = new PdfPCell(new Phrase(
                "Billed To:\n" + quotation.getCustomerName() + "\n" +
                        quotation.getCustomerAddress() + gstinText, new Font(Font.HELVETICA, 9)));
        PdfPCell shippedTo = new PdfPCell(new Phrase(
                "Shipped To:\n" + quotation.getCustomerName() + "\n" +
                        quotation.getCustomerAddress() + gstinText, new Font(Font.HELVETICA, 9)));
        billedTo.setPadding(8);
        shippedTo.setPadding(8);
        partyTable.addCell(billedTo);
        partyTable.addCell(shippedTo);
        partyTable.setSpacingAfter(12);
        document.add(partyTable);

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{0.8f, 2.3f, 1.2f, 0.8f, 0.7f, 1f, 1.2f, 1.2f, 1.2f, 1.6f});

        String[] headers = {"S.N.", "Description", "HSN", "Qty", "Unit", "MRP", "Sell Price", "Discount", "GST %", "Total"};
        for (String h : headers) table.addCell(headerCell(h));

        int sn = 1;
        for (QuotationItemDTO item : quotation.getItems()) {
            Optional<Product> productOpt = item.getProductId() != null
                    ? productRepository.findById(item.getProductId())
                    : Optional.empty();
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

        // Total Section
        PdfPTable totalTable = new PdfPTable(1);
        totalTable.setWidthPercentage(30);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell amountCell = new PdfPCell(new Phrase(
                "Total Amount: ₹" + String.format("%.2f", quotation.getTotalAmount()),
                new Font(Font.HELVETICA, 10, Font.BOLD)));
        amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        amountCell.setPadding(6);
        totalTable.addCell(amountCell);

        document.add(totalTable);
        document.close();
        return baos.toByteArray();
    }

    // -----------------------------------------------------
    // 🧩 Helper Cells
    // -----------------------------------------------------
    private PdfPCell cell(Object value) {
        PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(value), new Font(Font.HELVETICA, 9)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell rightCell(Object value) {
        PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(value), new Font(Font.HELVETICA, 9)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        return cell;
    }

    private PdfPCell headerCell(String value) {
        PdfPCell hCell = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 9, Font.BOLD)));
        hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        hCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        hCell.setPadding(5);
        hCell.setBackgroundColor(new Color(240, 240, 240));
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

    private PdfPCell totalValue(String value) {
        PdfPCell cell = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 8, Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    // -----------------------------------------------------
    // 🧾 Page Border + Footer
    // -----------------------------------------------------
    private static class PageBorderEvent extends PdfPageEventHelper {
        Font footerFont = new Font(Font.HELVETICA, 8);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            cb.setLineWidth(0.5f);

            // Border
            cb.rectangle(document.left() - 10, document.bottom() - 10,
                    document.getPageSize().getWidth() - 18,
                    document.getPageSize().getHeight() - 30);
            cb.stroke();

            // Footer Text
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Terms & Conditions: Goods once sold will not be taken back. Subject to Ichalkaranji Jurisdiction only.", footerFont),
                    document.left(), document.bottom() + 25, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Declaration: This document shows actual price & all particulars are true.", footerFont),
                    document.left(), document.bottom() + 15, 0);

            // Page Number
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    new Phrase("" + writer.getPageNumber(), footerFont),
                    (document.right() + document.left()) / 2, document.bottom() - 5, 0);
        }
    }
}
