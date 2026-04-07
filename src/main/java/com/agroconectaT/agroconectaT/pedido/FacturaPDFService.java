package com.agroconectaT.agroconectaT.pedido;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
public class FacturaPDFService {

    public void exportar(HttpServletResponse response, Pedido pedido) throws IOException {
        Document documento = new Document(PageSize.A4, 36, 36, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(documento, response.getOutputStream());
        
        // --- MARCA DE AGUA AGROCONECTA ---
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContentUnder();
                try {
                    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
                    cb.beginText();
                    cb.setColorFill(new Color(240, 245, 240)); // Verde muy pálido
                    cb.setFontAndSize(bf, 70);
                    cb.showTextAligned(Element.ALIGN_CENTER, "AGROCONECTA", 300, 400, 45);
                    cb.endText();
                    
                    // LÍNEA TRICOLOR AL PIE
                    cb.setLineWidth(2f);
                    cb.setColorStroke(new Color(255, 205, 0)); // Amarillo
                    cb.moveTo(36, 30); cb.lineTo(210, 30); cb.stroke();
                    cb.setColorStroke(new Color(0, 56, 147));  // Azul
                    cb.moveTo(210, 30); cb.lineTo(384, 30); cb.stroke();
                    cb.setColorStroke(new Color(206, 17, 38));  // Rojo
                    cb.moveTo(384, 30); cb.lineTo(559, 30); cb.stroke();
                } catch (Exception e) { e.printStackTrace(); }
            }
        });

        documento.open();

        // --- DEFINICIÓN DE COLORES ---
        Color verdeOscuro = new Color(46, 125, 50);
        Color verdeClaro = new Color(76, 175, 80);
        Color amarilloAgro = new Color(248, 152, 32);
        
        // --- FUENTES ---
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, verdeOscuro);
        Font fontSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        Font fontDatos = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        Font fontMonto = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, verdeOscuro);

        // --- ENCABEZADO CON DISEÑO ---
        PdfPTable headTable = new PdfPTable(2);
        headTable.setWidthPercentage(100);
        headTable.setWidths(new float[]{3f, 2f});

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.addElement(new Paragraph("AGROCONECTA", fontTitulo));
        logoCell.addElement(new Paragraph("Tecnología para el campo colombiano", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, verdeClaro)));
        headTable.addCell(logoCell);

        PdfPCell nroFacturaCell = new PdfPCell();
        nroFacturaCell.setBorder(Rectangle.NO_BORDER);
        nroFacturaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph nro = new Paragraph("FACTURA ELECTRÓNICA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.GRAY));
        nro.setAlignment(Element.ALIGN_RIGHT);
        nroFacturaCell.addElement(nro);
        Paragraph idF = new Paragraph("N° AC-000" + pedido.getId(), fontMonto);
        idF.setAlignment(Element.ALIGN_RIGHT);
        nroFacturaCell.addElement(idF);
        headTable.addCell(nroFacturaCell);

        documento.add(headTable);
        documento.add(new Paragraph(" "));

        // --- BLOQUE INFO CLIENTE Y VENDEDOR ---
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10);

        // Cabeceras verdes
        PdfPCell h1 = new PdfPCell(new Phrase("INFORMACIÓN DEL COMPRADOR", fontSeccion));
        h1.setBackgroundColor(verdeOscuro); h1.setPadding(5); h1.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(h1);

        PdfPCell h2 = new PdfPCell(new Phrase("ORIGEN DE LA COSECHA", fontSeccion));
        h2.setBackgroundColor(verdeClaro); h2.setPadding(5); h2.setBorder(Rectangle.NO_BORDER);
        infoTable.addCell(h2);

        // Datos de las partes
        PdfPCell d1 = new PdfPCell();
        d1.setPadding(8); d1.setBorder(Rectangle.LEFT | Rectangle.BOTTOM); d1.setBorderColor(verdeOscuro);
        d1.addElement(new Paragraph("Nombre: " + pedido.getUsuario().getNombre(), fontDatos));
        d1.addElement(new Paragraph("Email: " + pedido.getUsuario().getCorreo(), fontDatos));
        infoTable.addCell(d1);

        PdfPCell d2 = new PdfPCell();
        d2.setPadding(8); d2.setBorder(Rectangle.RIGHT | Rectangle.BOTTOM); d2.setBorderColor(verdeClaro);
        d2.addElement(new Paragraph("Campesino: " + pedido.getProducto().getUsuario().getNombre(), fontDatos));
        d2.addElement(new Paragraph("Ubicación: " + pedido.getProducto().getUbicacion(), fontDatos));
        infoTable.addCell(d2);

        documento.add(infoTable);
        documento.add(new Paragraph(" "));

        // --- TABLA DE PRODUCTOS ---
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4f, 1f, 2.5f, 2.5f});

        String[] headers = {"PRODUCTO / DESCRIPCIÓN", "CANT.", "UNITARIO", "TOTAL"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontSeccion));
            cell.setBackgroundColor(verdeOscuro);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
        }

        // Fila única de producto
        table.addCell(new PdfPCell(new Phrase(pedido.getProducto().getNombre(), fontDatos)));
        PdfPCell cantCell = new PdfPCell(new Phrase(String.valueOf(pedido.getCantidad()), fontDatos));
        cantCell.setHorizontalAlignment(Element.ALIGN_CENTER); table.addCell(cantCell);

        NumberFormat colFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        colFormat.setMaximumFractionDigits(0);

        PdfPCell priceCell = new PdfPCell(new Phrase(colFormat.format(pedido.getProducto().getPrecio()), fontDatos));
        priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT); table.addCell(priceCell);

        PdfPCell totalCell = new PdfPCell(new Phrase(colFormat.format(pedido.getTotal()), fontDatos));
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT); table.addCell(totalCell);

        documento.add(table);

        // --- RESUMEN Y TOTAL ---
        PdfPTable resTable = new PdfPTable(2);
        resTable.setWidthPercentage(40);
        resTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        resTable.setSpacingBefore(15);

        PdfPCell tL = new PdfPCell(new Phrase("TOTAL PAGADO:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
        tL.setBackgroundColor(amarilloAgro); tL.setPadding(10); tL.setBorder(Rectangle.NO_BORDER);
        resTable.addCell(tL);

        PdfPCell tV = new PdfPCell(new Phrase(colFormat.format(pedido.getTotal()), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
        tV.setBackgroundColor(amarilloAgro); tV.setPadding(10); tV.setBorder(Rectangle.NO_BORDER);
        tV.setHorizontalAlignment(Element.ALIGN_RIGHT);
        resTable.addCell(tV);

        documento.add(resTable);

        // --- PIE DE PÁGINA FINAL ---
        documento.add(new Paragraph(" "));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Paragraph fecha = new Paragraph("Fecha de pago: " + sdf.format(pedido.getFecha()), fontDatos);
        fecha.setAlignment(Element.ALIGN_CENTER);
        documento.add(fecha);

        Paragraph leyenda = new Paragraph("\nEste documento es un soporte de pago electrónico generado por AgroConecta.\n" +
                "El 100% de este dinero va destinado al productor local.", 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, verdeOscuro));
        leyenda.setAlignment(Element.ALIGN_CENTER);
        documento.add(leyenda);

        documento.close();
    }
}