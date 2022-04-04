package gms4u.birt;

import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.api.script.eventadapter.ImageEventAdapter;
import org.eclipse.birt.report.engine.api.script.instance.IImageInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GarageLogoImageEventAdapter extends ImageEventAdapter {

    private final Logger log = LoggerFactory.getLogger(GarageLogoImageEventAdapter.class);

    @Override
    public void onCreate(IImageInstance image, IReportContext reportContext) throws ScriptException {

        byte[] garageLogoBytes = (byte[]) reportContext.getAppContext().get("garageLogo");

        boolean hasGarageLogo = garageLogoBytes != null;
        if (hasGarageLogo) {
            log.info("Setting Business Logo");
            image.setData(garageLogoBytes);
        } else {
            log.warn("No logo found.");
        }

    }
}
