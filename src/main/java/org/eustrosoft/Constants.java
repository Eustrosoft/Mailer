package org.eustrosoft;

public final class Constants {

    public static final String EMPTY = "";

    public static final String PARAM_TO = "to";
    public static final String PARAM_SUBJECT = "subject";
    public static final String PARAM_MESSAGE = "message";

    public static final String PROP_MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String PROP_MAIL_SMTP_POST = "mail.smtp.port";
    public static final String PROP_MAIL_SMTP_SSL = "mail.smtp.ssl.enabled";
    public static final String PROP_MAIL_SMTP_AUTH = "mail.smtp.auth";

    public static final String PROP_MAIL_USER = "mail.user";
    public static final String PROP_MAIL_PASSWORD = "mail.password";
    public static final String PROP_MAIL_FROM = "mail.from";


    public static final String RECIPIENTS_DELIMITER = ";";
    public static final String FILES_PART_REGEX = "file";
    public static final Integer FILE_BYTE_BUFFER_SIZE = 2048;

    public static final String MSG_METHOD_NOT_SUPPORTED = "%s method not supported";
    public static final String MSG_MAIL_SENT = "Mail sent successfully!";
    public static final String MSG_MAIL_SEND_ERROR = "Mail was not send due to exception: %s";
    public static final String MSG_FILE_CREATION_ERROR = "File does not created %s";

    private Constants() {

    }
}
