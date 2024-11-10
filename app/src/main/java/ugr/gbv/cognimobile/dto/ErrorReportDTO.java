package ugr.gbv.cognimobile.dto;

public class ErrorReportDTO {

    public ErrorReportDTO() {
    }

    public ErrorReportDTO(String errorMessage, String stackTrace) {
        this.errorMessage = errorMessage;
        this.stackTrace = stackTrace;
    }

    private String errorMessage;

    private String stackTrace;

    // Getters y Setters
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
