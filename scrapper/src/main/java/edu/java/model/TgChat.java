package edu.java.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TgChat {

    private Long id;
    @NotNull private Long chatId;
    @NotNull private String name;

}
