package seeker1389.dolors.dolors;

import java.util.HashMap;
import java.util.Map;

public class AppException extends Exception{

    /*
   Error codes for the program.....
    400 Bad Request. ...
    401 Unauthorized. ...
    403 Forbidden. ...
    404 Not Found. ...
    500 Internal Server Error. ...
    502 Bad Gateway. ...
    503 Service Unavailable. ...
    504 Gateway Timeout.

    Custom error codes for the program for internal use....
    100 data unavailable
    101 data type not recognised
    102 data not accessible
    103 data saving failed

    104 page corrupted



     */



    private int ErrorCode;
    private String reason;
    static HashMap<Integer,String> errorCodes = new HashMap<>();

    static{



        errorCodes.put(400, "Bad Request");
        errorCodes.put(401, "Unauthorized");
        errorCodes.put(403, "Forbidden");
        errorCodes.put(404, " (URL) Not Found");
        errorCodes.put(500, "Internal Error");
        errorCodes.put(502, "Bad Gateway");
        errorCodes.put(503, "Service unavailable");
        errorCodes.put(504, "GateWay timeout");

        errorCodes.put(100, "Data unavailable");
        errorCodes.put(101, "data type not recognised");
        errorCodes.put(102, "data not accessible");
        errorCodes.put(103, "data saving failed");
    }



    AppException(int ErrorCode){
        super(Integer.toString(ErrorCode));
        this.ErrorCode=ErrorCode;
    }

    public String toString(){
        String message = "Error code undefined ->"+ErrorCode;
        for (Map.Entry<Integer, String> set : errorCodes.entrySet()) {
            if(set.getKey()==ErrorCode) {
                message= "Error Code "+set.getKey()+" "+set.getValue()+"";
            }
        }
        return message;
    }



}
