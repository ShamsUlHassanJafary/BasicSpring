package gms4u.service;

import gms4u.domain.*;
import gms4u.domain.enumeration.BookingStatus;
import gms4u.service.dto.FileDto;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ReportingService implements ApplicationContextAware, DisposableBean {

    private static final String FILENAME_DATE_PATTERN = "yyyy-MM-dd";
    private static final String GARAGES_FOLDER_LOCATION = System.getProperty("user.dir");
    private final Logger log = LoggerFactory.getLogger(ReportingService.class);
    @Value("classpath:/rptdesigns/teknigarage_invoice_template.rptdesign")
    private Resource invoiceReportDesignPath;
    @Value("classpath:/rptdesigns/teknigarage_quotation_template.rptdesign")
    private Resource quoteReportDesignPath;

    @Value("classpath:/rptdesigns/teknigarage_service_history_report_template.rptdesign")
    private Resource serviceHistoryReportDesignPath;

    private IReportEngine birtEngine;
    private ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    protected void initialize() throws BirtException {

        EngineConfig config = new EngineConfig();
        config.getAppContext().put("spring", this.context);
        config.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, Thread.currentThread().getContextClassLoader());

        Platform.startup(config);
        IReportEngineFactory factory = (IReportEngineFactory) Platform
            .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        birtEngine = factory.createReportEngine(config);

        log.info("BIRT Engine initialised.");

    }

    /**
     * Load report files to memory
     *
     * @return
     */
    public FileDto generateInvoice(Booking booking) throws EngineException, IOException {

        Quote quote = booking.getQuote();
        boolean hasQuote = quote != null;
        LocalDate date = hasQuote && quote.getQuoteDate() != null ? quote.getQuoteDate() : LocalDate.now();
        Garage garage = booking.getGarage();

        BigDecimal total = hasQuote && quote.getQuoteTotal() != null ? quote.getQuoteTotal() : BigDecimal.ZERO;
        BigDecimal vat = applyVAT(garage, total);
        BigDecimal finalTotalPrice = hasQuote ? total.add(vat) : BigDecimal.valueOf(0);
        BigDecimal discount = hasQuote && quote.getDiscount() != null ? quote.getDiscount() : BigDecimal.valueOf(0);

        IReportRunnable template = birtEngine.openReportDesign(quoteReportDesignPath.getInputStream());
        Invoice invoice = booking.getInvoice();
        String prefix = "quote";
        boolean hasInvoice = invoice != null;
        if (hasInvoice && booking.getStatus().equals(BookingStatus.INVOICED)) {
            total = invoice.getInvoiceTotal() != null ? invoice.getInvoiceTotal() : BigDecimal.ZERO;
            vat = applyVAT(garage, total);
            finalTotalPrice = total.add(vat);
            discount = invoice.getDiscount() != null ? invoice.getDiscount() : BigDecimal.valueOf(0);
            prefix = "invoice";
            template = birtEngine.openReportDesign(invoiceReportDesignPath.getInputStream());
            date = hasInvoice && invoice.getIssueDate() != null ? invoice.getIssueDate() : invoice.getInvoiceDate().toLocalDate();
        }
        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(Locale.UK);
        String formattedTotal = currencyInstance.format(finalTotalPrice);
        String formattedDate = DateTimeFormatter.ofPattern(FILENAME_DATE_PATTERN).format(date);

        String fileName = generateFileName(booking, date, prefix + "-") + ".pdf";

        String filePath = "garages" + File.separator + garage.getId() + File.separator + prefix + File.separator + formattedDate + File.separator;

        Map<String, Object> params = new HashMap<>();
        Customer customer = booking.getCustomer();

        params.put("garageName", garage.getBusinessName());
        params.put("garageAddressLine1", garage.getLineAddress1());
        params.put("garageAddressLine2", garage.getLineAddress2());
        params.put("garageAddressCity", garage.getCity());
        params.put("garageAddressPostcode", garage.getPostcode());
        BigDecimal paid = BigDecimal.ZERO;

        if (hasInvoice) {
            params.put("invoiceNumber", "_INV_" + invoice.getReference());
            paid = invoice.getPaid() != null ? invoice.getPaid() : BigDecimal.ZERO;

        } else if (hasQuote) {
            params.put("invoiceNumber", "_QUO_" + quote.getReference());
        }

        params.put("invoiceDate", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date));
        params.put("invoiceDueDate", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(date));


        params.put("bookingReference", "INV_" + (booking != null ? booking.getReference() : "________"));

        params.put("quoteReference", "QUO_" + (quote != null ? quote.getReference() : "________"));

        params.put("customerFullName", customer.getFirstName() + " " + customer.getLastName());
        params.put("customerEmail", customer.getEmail());
        params.put("customerPhone", customer.getPhoneNumber());

        params.put("vehicleRegistration", booking.getVehicle().getRegistration());
        params.put("vehicleMake", booking.getVehicle().getMake());
        params.put("vehicleModel", booking.getVehicle().getModel());
        params.put("vehicleColour", booking.getVehicle().getColour());
        params.put("bookingVehicleMileage", String.valueOf(booking.getMileage()));
        params.put("notesFurtherInstruction", booking.getFurtherInstruction());
        params.put("VAT", currencyInstance.format(vat));
        params.put("totalDiscount", currencyInstance.format(discount));

        params.put("totalPrice", formattedTotal);

        BigDecimal balance = finalTotalPrice.subtract(paid);
        params.put("paid", currencyInstance.format(paid));
        params.put("balance", currencyInstance.format(balance));

        birtEngine.getConfig().getAppContext().put("bookingJobs", booking.getJobs());

        setGarageLogo(garage, params);

        generateOutputStreamReport(fileName, filePath, params, template);
        return findExistingFile(filePath, fileName);
    }

    /**
     * Load report files to memory
     *
     * @return
     */
    public FileDto generateServiceHistoryReport(Garage garage, Customer customer, Vehicle vehicle, List<Booking> bookings) throws EngineException, IOException {


        IReportRunnable template = birtEngine.openReportDesign(serviceHistoryReportDesignPath.getInputStream());

        String fileName = generateFileName(customer, vehicle) + ".pdf";

        String formattedDate = DateTimeFormatter.ofPattern(FILENAME_DATE_PATTERN).format(LocalDate.now());
        String filePath = "garages" + File.separator + garage.getId() + File.separator + "service-history" + File.separator + formattedDate + File.separator;

        Map<String, Object> params = new HashMap<>();

        params.put("garageName", garage.getBusinessName());
        params.put("garageAddressLine1", garage.getLineAddress1());
        params.put("garageAddressLine2", garage.getLineAddress2());
        params.put("garageAddressCity", garage.getCity());
        params.put("garageAddressPostcode", garage.getPostcode());

        params.put("customerFullName", customer.getFirstName() + " " + customer.getLastName());
        params.put("customerEmail", customer.getEmail());
        params.put("customerPhone", customer.getPhoneNumber());

        params.put("vehicleRegistration", vehicle.getRegistration());
        params.put("vehicleMake", vehicle.getMake());
        params.put("vehicleModel", vehicle.getModel());
        params.put("vehicleColour", vehicle.getColour());

        birtEngine.getConfig().getAppContext().put("bookings", bookings);

        setGarageLogo(garage, params);

        generateOutputStreamReport(fileName, filePath, params, template);
        return findExistingFile(filePath, fileName);
    }

    private BigDecimal applyVAT(Garage garage, BigDecimal total) {
        return garage.isVatRegistered() != null && garage.isVatRegistered() && (total != null && total.compareTo(BigDecimal.ZERO) > 1) ? (total.multiply(BigDecimal.valueOf(20.00))).divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
    }

    private void setGarageLogo(Garage garage, Map<String, Object> params) throws IOException {
        File file = new File(GARAGES_FOLDER_LOCATION + garage.getLogoUrl());

        if (file.exists()) {

            try (FileInputStream fileInputStream = new FileInputStream(file); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) file.length())) {

                int c = -1;

                while ((c = fileInputStream.read()) != -1) byteArrayOutputStream.write(c);

                byte[] byteArray = byteArrayOutputStream.toByteArray();
                boolean hastGarageLogoData = byteArray != null && byteArray.length > 0;

                birtEngine.getConfig().getAppContext().put("garageLogo", byteArray);

                params.put("hasGarageLogo", hastGarageLogoData);
            }
        }
    }

    private String generateFileName(Booking booking, LocalDate date, String prefix) {
        String formattedDate = DateTimeFormatter.ofPattern(FILENAME_DATE_PATTERN).format(date);

        StringBuilder filePathBuilder = new StringBuilder(prefix);
        if (booking.getQuote() != null) {
            filePathBuilder.append(booking.getQuote().getReference());
            filePathBuilder.append("-");
        }

        if (booking.getInvoice() != null) {
            filePathBuilder.append(booking.getInvoice().getReference());
            filePathBuilder.append("-");
        }

        filePathBuilder.append(booking.getVehicle().getRegistration());
        filePathBuilder.append("-");
        filePathBuilder.append(formattedDate);

        return filePathBuilder.toString();
    }

    private String generateFileName(Customer customer, Vehicle vehicle) {
        String formattedDate = DateTimeFormatter.ofPattern(FILENAME_DATE_PATTERN).format(LocalDate.now());

        StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder.append(customer.getFirstName().toLowerCase());
        filePathBuilder.append("_");
        filePathBuilder.append(customer.getLastName().toLowerCase());
        filePathBuilder.append("-");

        filePathBuilder.append(vehicle.getRegistration());
        filePathBuilder.append("-");
        filePathBuilder.append(formattedDate);
        return filePathBuilder.toString();
    }

    /**
     * @param fileName     The name of the file.
     * @param fileLocation optional file location e.g. if specified to
     * @param params       Map report parameters
     * @param report       The .rptdesign file template
     * @return
     */
    private byte[] generateOutputStreamReport(String fileName, String fileLocation, Map<String, Object> params, IReportRunnable report) throws IOException {
        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
        IRenderOption options = new RenderOption();
        options.setOutputFormat("pdf");
        if (fileLocation != null)
            options.setOutputFileName(fileLocation + fileName);
        PDFRenderOption pdfRenderOption = new PDFRenderOption(options);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        pdfRenderOption.setOutputStream(byteArrayOutputStream);
        runAndRenderTask.setParameterValues(params);
        runAndRenderTask.setRenderOption(pdfRenderOption);
        runAndRenderTask.getAppContext().put(EngineConstants.PROJECT_CLASSPATH_KEY, "request");

        try {
            runAndRenderTask.run();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            runAndRenderTask.close();
            byteArrayOutputStream.close();
        }
    }

    public FileDto findExistingFile(String filePath, String fileName) throws IOException {
        File file = new File(filePath + fileName);

        if (file.exists())
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) file.length());
                int c = -1;

                while ((c = fileInputStream.read()) != -1) byteArrayOutputStream.write(c);

                return new FileDto(fileName, byteArrayOutputStream.toByteArray());
            }

        return new FileDto();

    }


    @Override
    public void destroy() {

        birtEngine.destroy();
        Platform.shutdown();

        log.info("BIRT Engine shutdown.");
    }

}
