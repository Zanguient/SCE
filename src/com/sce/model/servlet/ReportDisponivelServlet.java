package com.sce.model.servlet;

import com.sce.model.domain.ItensNotaFiscal;
import com.sce.model.domain.NotaFiscal;
import com.sce.model.service.ItensNotaFiscalService;
import com.sce.model.service.NotaFiscalService;
import com.sce.model.servlet.writepdresponse.ReportHelp;
import com.sce.model.wrapper.ItensNotaFiscalXml;
import com.sce.model.wrapper.NotaFiscalXml;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andre on 07/06/2016.
 */
public class ReportDisponivelServlet extends HttpServlet {

    @Inject
    private NotaFiscalService notaFiscalService;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            List<NotaFiscal> notaFiscais = notaFiscalService.getNotaFiscais();

            NotaFiscalXml notaFiscalXml = new NotaFiscalXml();

            notaFiscalXml.setNotaFiscalList(notaFiscais);

            JAXBContext context;
            context = JAXBContext.newInstance(NotaFiscalXml.class);
            Marshaller marshaller = context.createMarshaller();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("C:\\Workspace\\SCE\\web\\xmls\\produtosDisponiveis.xml"), "UTF-8"
            ));
            marshaller.marshal(notaFiscalXml, out);

            String jasperReport = "C:\\Workspace\\SCE\\web\\reports\\SCEDisponivel.jasper";
            String dataSourceFile = "C:/Workspace/SCE/web/xmls/produtosDisponiveis.xml";
            String pdfout = "C:\\Workspace\\SCE\\web\\reports\\SCEDisponivel.pdf";

            Document document = JRXmlUtils.parse(JRLoader.getLocationInputStream(dataSourceFile));
            Map params = new HashMap();
            params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params);

            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfout);

            ReportHelp.writePdfResponse(response, pdfout);

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JRException e) {
            e.printStackTrace();
        }
    }
}
