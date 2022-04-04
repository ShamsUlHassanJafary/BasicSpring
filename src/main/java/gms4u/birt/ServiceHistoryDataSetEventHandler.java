package gms4u.birt;

import gms4u.domain.Booking;
import gms4u.domain.Job;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.IUpdatableDataSetRow;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.api.script.eventadapter.ScriptedDataSetEventAdapter;
import org.eclipse.birt.report.engine.api.script.instance.IDataSetInstance;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceHistoryDataSetEventHandler extends ScriptedDataSetEventAdapter {

    private Iterator<Booking> iterator;

    @Override
    public boolean fetch(IDataSetInstance dataSet, IUpdatableDataSetRow row) throws ScriptException {
        if (iterator == null || !iterator.hasNext()) {
            return false;
        }

        Booking booking = iterator.next();


        String jobs = booking.getJobs().stream().map(Job::getDescription).collect(Collectors.joining("\n"));

        row.setColumnValue("services", jobs);
        LocalDate bookingDate = booking.getBookingDate() != null ? booking.getBookingDate() : LocalDate.now();
        row.setColumnValue("bookingDate", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(bookingDate));
        ZonedDateTime bookingTime = booking.getBookingTime() != null ? booking.getBookingTime() : ZonedDateTime.now();
        row.setColumnValue("bookingTime", DateTimeFormatter.ofPattern("HH:mm").format(bookingTime));
        row.setColumnValue("furtherInstruction", booking.getFurtherInstruction());


        return true;
    }

    @Override
    public void close(IDataSetInstance dataSet) throws ScriptException {
        iterator = null;
    }

    @Override
    public void beforeOpen(IDataSetInstance dataSet, IReportContext reportContext) throws ScriptException {
        iterator = ((List<Booking>) reportContext.getAppContext().get("bookings")).iterator();
    }
}
