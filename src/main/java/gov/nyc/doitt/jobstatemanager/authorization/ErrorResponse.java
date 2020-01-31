package gov.nyc.doitt.jobstatemanager.authorization;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
	private Map<String, String> errorsMap = new HashMap<String, String>();
    
    public ErrorResponse(Map<String, String> errors) {
    	errorsMap = errors;
    }	

    public ErrorResponse(String key, String value) {
    	errorsMap.put(key, value);
    }	

    public Map<String, String> getErrors(){
    	return errorsMap;
    }
    
}