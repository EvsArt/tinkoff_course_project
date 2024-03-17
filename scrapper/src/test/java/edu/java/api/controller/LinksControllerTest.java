package edu.java.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.api.constants.Headers;
import edu.java.api.dto.AddLinkRequest;
import edu.java.api.dto.LinkResponse;
import edu.java.api.dto.ListLinksResponse;
import edu.java.api.dto.RemoveLinkRequest;
import edu.java.api.exceptions.LinkNotExistsException;
import edu.java.api.service.LinksService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksController.class)
class LinksControllerTest {

    @MockBean LinksService linksService;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void getLinksShouldReturnOk() throws Exception {
        ListLinksResponse links = new ListLinksResponse(List.of(
            new LinkResponse(1L, "myUrl1"),
            new LinkResponse(2L, "myUrl2"),
            new LinkResponse(3L, "myUrl3")
        ));

        Long chatId = 1L;
        Mockito.when(linksService.getListLinksResponseByTgChatId(chatId)).thenReturn(links);

        mockMvc.perform(get("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(links)));
    }

    @Test
    void getLinksWithWrongHeaderShouldReturnBadRequest() throws Exception {
        String chatId = "1s";

        mockMvc.perform(get("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MethodArgumentTypeMismatchException.class.getName()));
    }

    @Test
    void getLinksWithoutHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/links")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MissingRequestHeaderException.class.getName()));
    }

    @Test
    void addLinkShouldReturnOk() throws Exception {
        String linkUrl = "abc";
        Long chatId = 1L;

        AddLinkRequest requestBody = new AddLinkRequest(linkUrl);
        LinkResponse expResponse = new LinkResponse(1L, linkUrl);
        Mockito.when(linksService.saveLink(Mockito.eq(chatId), Mockito.any())).thenReturn(expResponse);

        mockMvc.perform(post("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.url").value(linkUrl));
    }

    @Test
    void addLinkWithWrongHeaderShouldReturnBadRequest() throws Exception {
        String chatId = "1s";

        mockMvc.perform(post("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MethodArgumentTypeMismatchException.class.getName()));
    }

    @Test
    void addLinkWithoutHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/links")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MissingRequestHeaderException.class.getName()));
    }

    @Test
    void addEmptyLinkShouldReturnBadRequest() throws Exception {
        String linkUrl = "";
        Long chatId = 1L;

        AddLinkRequest requestBody = new AddLinkRequest(linkUrl);
        LinkResponse expResponse = new LinkResponse(1L, linkUrl);
        Mockito.when(linksService.saveLink(Mockito.eq(chatId), Mockito.any())).thenReturn(expResponse);

        mockMvc.perform(post("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MethodArgumentNotValidException.class.getName()));
    }

    @Test
    void removeLinkShouldReturnOk() throws Exception {
        String linkUrl = "abc";
        Long chatId = 1L;

        RemoveLinkRequest requestBody = new RemoveLinkRequest(linkUrl);
        LinkResponse expResponse = new LinkResponse(1L, linkUrl);
        Mockito.when(linksService.removeLink(Mockito.eq(chatId), Mockito.any())).thenReturn(expResponse);

        mockMvc.perform(delete("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.url").value(linkUrl));
    }

    @Test
    void removeLinkWithWrongHeaderShouldReturnBadRequest() throws Exception {
        String chatId = "1s";

        mockMvc.perform(delete("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MethodArgumentTypeMismatchException.class.getName()));
    }

    @Test
    void removeLinkWithoutHeaderShouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/links")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.exceptionName").value(MissingRequestHeaderException.class.getName()));
    }

    @Test
    void removeNotExistedLinkShouldReturnNotFound() throws Exception {
        String linkUrl = "aa";
        Long chatId = 1L;

        RemoveLinkRequest requestBody = new RemoveLinkRequest(linkUrl);
        Mockito.when(linksService.removeLink(Mockito.eq(chatId), Mockito.any()))
            .thenThrow(LinkNotExistsException.class);

        mockMvc.perform(delete("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.exceptionName").value(LinkNotExistsException.class.getName()));
    }

}
