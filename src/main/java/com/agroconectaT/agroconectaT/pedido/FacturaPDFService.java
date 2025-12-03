package com.agroconectaT.agroconectaT.pedido;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import com.agroconectaT.agroconectaT.usuario.Usuario;

import java.awt.Color;
import java.io.IOException;

@Service
public class FacturaPDFService {

    public void exportar(HttpServletResponse response, Pedido pedido) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // 1. Encabezado y Logo
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuenteTitulo.setSize(24);
        fuenteTitulo.setColor(new Color(34, 139, 34)); // Verde AgroConecta

        Paragraph titulo = new Paragraph("AgroConecta - Factura de Venta", fuenteTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        document.add(new Paragraph(" ")); // Espacio

        // 2. Datos del Cliente
        Font fuenteTexto = FontFactory.getFont(FontFactory.HELVETICA);
        fuenteTexto.setSize(12);

        Usuario comprador = pedido.getUsuario(); 
        
        document.add(new Paragraph("Fecha de Emisión: " + pedido.getFecha(), fuenteTexto));
        document.add(new Paragraph("Cliente: " + comprador.getNombre(), fuenteTexto));
        document.add(new Paragraph("Correo: " + comprador.getCorreo(), fuenteTexto));
        document.add(new Paragraph("ID Pedido: #" + pedido.getId(), fuenteTexto));
        
        document.add(new Paragraph(" ")); 
        document.add(new Paragraph("----------------------------------------------------------"));
        document.add(new Paragraph(" ")); 

        // 3. Tabla de Productos
        PdfPTable tabla = new PdfPTable(4); 
        tabla.setWidthPercentage(100f);
        tabla.setWidths(new float[] { 3.5f, 1.5f, 2.0f, 2.0f });
        tabla.setSpacingBefore(10);

        escribirCabecera(tabla, "Producto");
        escribirCabecera(tabla, "Cant.");
        escribirCabecera(tabla, "Precio Unit.");
        escribirCabecera(tabla, "Subtotal");

        // Datos del producto (Asumiendo un producto por pedido simplificado por ahora)
        tabla.addCell(pedido.getProducto().getNombre());
        tabla.addCell(String.valueOf(pedido.getCantidad()));
        tabla.addCell("$ " + pedido.getProducto().getPrecio());
        tabla.addCell("$ " + pedido.getTotal());

        document.add(tabla);

        // 4. Total Final
        document.add(new Paragraph(" "));
        Font fuenteTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fuenteTotal.setSize(16);
        
        Paragraph total = new Paragraph("TOTAL PAGADO: $ " + pedido.getTotal(), fuenteTotal);
        total.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(total);

        // 5. Pie de página
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Gracias por apoyar al campo colombiano. Pago procesado vía PSE.", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10));
        footer.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    private void escribirCabecera(PdfPTable tabla, String titulo) {
        PdfPCell celda = new PdfPCell();
        celda.setBackgroundColor(new Color(34, 139, 34)); 
        celda.setPadding(5);
        Font fuente = FontFactory.getFont(FontFactory.HELVETICA);
        fuente.setColor(Color.WHITE);
        celda.setPhrase(new Phrase(titulo, fuente));
        tabla.addCell(celda);
    }
}