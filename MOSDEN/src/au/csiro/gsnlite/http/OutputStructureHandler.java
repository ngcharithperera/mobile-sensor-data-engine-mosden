package au.csiro.gsnlite.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import au.csiro.gsnlite.beans.DataField;
import au.csiro.gsnlite.beans.Mappings;
import au.csiro.gsnlite.utils.Logger;
import au.csiro.gsnlite.vsensor.VSensorConfig;

public class OutputStructureHandler implements RequestHandler {

	private static final transient Logger logger           = Logger.getInstance();
	private String TAG = "OutputStructureHandler.class";

    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        String vsName = request.getParameter("name");
        VSensorConfig sensorConfig = Mappings.getVSensorConfig(vsName);
        if (logger.isInfoEnabled())
            logger.info(TAG, new StringBuilder().append("Structure request for *").append(vsName).append("* received.").toString());
        StringBuilder sb = new StringBuilder("<virtual-sensor name=\"").append(vsName).append("\">\n");
        for (DataField df : sensorConfig.getOutputStructure())
            sb.append("<field name=\"").append(df.getName()).append("\" ").append("type=\"").append(df.getType()).append("\" ").append("description=\"").append(
                    StringEscapeUtils.escapeXml(df.getDescription())).append("\" />\n");
        sb.append("<field name=\"timed\" type=\"string\" description=\"The timestamp associated with the stream element\" />\n");
        sb.append("</virtual-sensor>");
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
        response.setHeader("Pragma", "no-cache");
        response.getWriter().write(sb.toString());
    }

    public boolean isValid(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String vsName = request.getParameter("name");

   

        if (vsName == null || vsName.trim().length() == 0) {
            response.sendError(WebConstants.MISSING_VSNAME_ERROR, "The virtual sensor name is missing");
            return false;
        }
        VSensorConfig sensorConfig = Mappings.getVSensorConfig(vsName);
        if (sensorConfig == null) {
            response.sendError(WebConstants.ERROR_INVALID_VSNAME, "The specified virtual sensor doesn't exist.");
            return false;
        }

        return true;
    }

}
