package edu.java.bot.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ScrapperApiPaths {

    public static final String ID_PARAM = "id";
    public static final String CHAT = String.format("/tg-chat/{%s}", ID_PARAM);
    public static final String LINKS = "/links";

}
