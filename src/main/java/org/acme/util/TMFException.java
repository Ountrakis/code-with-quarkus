package org.acme.util;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class TMFException extends RuntimeException{
    private String message;
    private int status;

    public TMFException (){}
    public TMFException(int status,String message){
        super(message);
        this.status=status;
        this.message=message;
    }
    public Response toResponse(RuntimeException exception){
        return Response.status(512).build();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static Builder Builder (int status , String message){ return new Builder(status,message) ;  }


    public static final class Builder {
        private TMFException tmfException;
        private Builder (int status,String message){
            this.tmfException = new TMFException(status,message);
        }
        public static Builder builder (int status,String message){
            return new Builder(status,message);
        }
        public Builder withStatusCode(int status){
            this.tmfException.setStatus(status);
            return this;
        }
        public Builder withStackTrace(StackTraceElement[] stackTrace) {
            tmfException.setStackTrace(stackTrace);
            return this;
        }
        public TMFException build() {return this.tmfException;}
    }

}
