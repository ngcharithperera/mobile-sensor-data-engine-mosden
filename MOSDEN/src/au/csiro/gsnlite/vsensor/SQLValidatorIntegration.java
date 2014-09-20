package au.csiro.gsnlite.vsensor;



import java.sql.SQLException;

import au.csiro.gsnlite.Main;
import au.csiro.gsnlite.utils.Logger;
import au.csiro.gsnlite.utils.SQLValidator;

public class SQLValidatorIntegration implements VSensorStateChangeListener{
	
	private SQLValidator validator;
	private String TAG = "SQLValidatorIntegration.class";
	
	public SQLValidatorIntegration(SQLValidator validator) throws SQLException {
		this.validator = validator;
	}
	

	private static final transient Logger logger = Logger.getInstance();

	public boolean vsLoading(VSensorConfig config) {
		try {
            String ddl = Main.getValidationStorage().getStatementCreateTable(config.getName(), config.getOutputStructure(), validator.getSampleConnection()).toString();
			validator.executeDDL(ddl);
		}catch (Exception e) {
			logger.error(TAG, e.getMessage());
		}
		return true;
	}

	public boolean vsUnLoading(VSensorConfig config) {
		try {
			String ddl = Main.getValidationStorage().getStatementDropTable(config.getName(), validator.getSampleConnection()).toString();
			validator.executeDDL(ddl);
		}catch (Exception e) {
			logger.error(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	public void release() throws Exception {
		validator.release();
		
	}
}
