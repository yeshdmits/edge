package yeshenko.edge.constant;

public class Constants {

    public static final String[] HEADERS = new String[]{"Serial Number", "Model", "Description"};
    public static final String PATH_SERIAL_NUMBER = "/{serialNumber}";
    public static final String MESSAGE_TEMPLATE = "Filename to upload: %s. Number of device: %d. With current date: %s";
    public static final String MEDIA_TYPE = "text/csv";
    public static final String EMAIL_FROM = "lenovonix.example@gmail.com";
    public static final String EMAIL_TO = "dmytro.yeshenko@nixsolutions.com";
    public static final String EMAIL_SUBJECT = "Request to upload file has been success.";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd_HH-mm-ss";
    public static final String UNDERLINE = "_";
    public static final String ATTACHMENT_FILENAME = "attachment; filename=";
    public static final String SCHEMA_REQUIREMENT = "oauth2";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SCHEME_OAUTH = "OAuth";


    private Constants() {
    }
}
