package edu.java.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StackOverflowApiPaths {

    public static final String QUESTION_ID_PARAM = "id";
    public static final String GET_QUESTION = "/questions/{id}";

}
