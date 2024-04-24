package edu.java.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.api.constants.Headers;
import edu.java.api.dto.AddLinkRequest;
import edu.java.api.dto.LinkResponse;
import edu.java.api.dto.ListLinksResponse;
import edu.java.api.dto.RemoveLinkRequest;
import edu.java.exceptions.LinkNotExistsException;
import edu.java.model.entity.Link;
import edu.java.service.LinkInfoService;
import edu.java.service.LinkService;
import edu.java.service.LinksTransformService;
import io.github.bucket4j.Bucket;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource("classpath:/test.env")
class LinksControllerTest {

    @MockBean LinkService linksService;
    @MockBean LinksTransformService linksTransformService;
    @MockBean LinkInfoService linkInfoService;
    @MockBean(name = "linksRateLimitBucket") Bucket bucket;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @BeforeEach
    void setBucket() {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(true);
    }

    @Test
    void getLinksShouldReturnOk() throws Exception {
        ListLinksResponse linksResponse = new ListLinksResponse(List.of(
            new LinkResponse("myUrl1"),
            new LinkResponse("myUrl2"),
            new LinkResponse("myUrl3")
        ));
        List<Link> links = List.of(
            new Link(URI.create("myUrl1"), "myUrl1"),
            new Link(URI.create("myUrl2"), "myUrl2"),
            new Link(URI.create("myUrl3"), "myUrl3")
        );

        Long chatId = 1L;
        Mockito.when(linksService.findAllByTgChatId(chatId)).thenReturn(links);
        Mockito.when(linksTransformService.toListLinksResponse(links)).thenReturn(linksResponse);

        mockMvc.perform(get("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(linksResponse)));
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
        Link link = new Link(URI.create(linkUrl), "");

        LinkResponse expResponse = new LinkResponse(linkUrl);
        Mockito.when(linksService.addLink(Mockito.eq(chatId), Mockito.any(), Mockito.any())).thenReturn(link);
        Mockito.when(linksTransformService.toLink(Mockito.any(AddLinkRequest.class))).thenReturn(link);
        Mockito.when(linksTransformService.toLinkResponse(link)).thenReturn(expResponse);

        mockMvc.perform(post("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
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
        Link link = new Link(URI.create(linkUrl), "");
        LinkResponse expResponse = new LinkResponse(linkUrl);
        Mockito.when(linksService.addLink(Mockito.eq(chatId), Mockito.any(), Mockito.any())).thenReturn(link);
        Mockito.when(linksTransformService.toLinkResponse(link)).thenReturn(expResponse);

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
        Link link = new Link(URI.create(linkUrl), "");
        LinkResponse expResponse = new LinkResponse(linkUrl);

        Mockito.when(linksService.removeLink(Mockito.eq(chatId), Mockito.any())).thenReturn(link);
        Mockito.when(linksTransformService.toLink(Mockito.any(RemoveLinkRequest.class))).thenReturn(link);
        Mockito.when(linksTransformService.toLinkResponse(link)).thenReturn(expResponse);

        mockMvc.perform(delete("/links")
                .header(Headers.TG_CHAT_ID, chatId)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
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
        Link link = new Link(URI.create(linkUrl), "");

        Mockito.when(linksTransformService.toLink(Mockito.any(RemoveLinkRequest.class))).thenReturn(link);
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
