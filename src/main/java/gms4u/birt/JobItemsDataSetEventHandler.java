package gms4u.birt;

import gms4u.domain.Job;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.IUpdatableDataSetRow;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.api.script.eventadapter.ScriptedDataSetEventAdapter;
import org.eclipse.birt.report.engine.api.script.instance.IDataSetInstance;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class JobItemsDataSetEventHandler extends ScriptedDataSetEventAdapter {

    private static final int VISIBLE_ROWS = 17;
    private final int counter = VISIBLE_ROWS;
    private final int jobSize = 0;
    private Iterator<Job> iterator;

    @Override
    public void beforeOpen(IDataSetInstance iDataSetInstance, IReportContext iReportContext) throws ScriptException {

        Set<Job> jobs = (Set<Job>) iReportContext.getAppContext().get("bookingJobs");
//        jobSize = jobs.size();
//        if (jobSize < VISIBLE_ROWS) {
//            counter = counter - jobSize;
//        }
        iterator = jobs.iterator();
    }

    @Override
    public boolean fetch(IDataSetInstance dataSet, IUpdatableDataSetRow row) throws ScriptException {

        if (iterator == null || !iterator.hasNext()) {
            return false;
        }

//            if (iterator.hasNext()) {
        Job job = iterator.next();
        row.setColumnValue("jobName", "");
        row.setColumnValue("jobDescription", job.getDescription());
        row.setColumnValue("jobQuantity", "");
        row.setColumnValue("jobUnitPrice", NumberFormat.getCurrencyInstance(Locale.UK).format(job.getPrice()));
//                if (jobSize < VISIBLE_ROWS) {
//                    counter--;
//                }
        return true;
//            }


    }

    @Override
    public void close(IDataSetInstance dataSet) throws ScriptException {
        iterator = null;
    }

}
