package edu.java.bot.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MetricsConstants {

    public static final String UPDATE_NAME = "update";
    public static final String UPDATE_STATUS = "status";
    public static final String UPDATE_STATUS_RECEIVED = "received";
    public static final String UPDATE_STATUS_ERROR = "error";

    public static final String UPDATE_FROM = "from";
    public static final String UPDATE_FROM_KAFKA = "kafka";
    public static final String UPDATE_FROM_HTTP = "http";

    public static final String MESSAGES_NAME = "messages";
    public static final String MESSAGES_NAME_FROM = "from";
    public static final String MESSAGES_NAME_FROM_TG = "telegram";
}
