package edu.java.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.api.constants.Headers;
import edu.java.api.dto.AddLinkRequest;
import edu.java.api.dto.RemoveLinkRequest;
import edu.java.model.entity.Link;
import edu.java.service.LinkInfoService;
import edu.java.service.LinkService;
import edu.java.service.LinksTransformService;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksController.class)
public class LinksControllerRateLimitTest {

    @Autowired MockMvc mockMvc;
    @MockBean LinkService linkService;
    @MockBean LinksTransformService linksTransformService;
    @MockBean LinkInfoService linkInfoService;
    @MockBean(name = "LinksRateLimitBucket") Bucket bucket;

    @Test
    void getRequest_shouldReturnTooManyRequests() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(false);
        mockMvc.perform(get("/links")
                .header(Headers.TG_CHAT_ID, 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    void postRequest_shouldReturnTooManyRequests() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(false);
        mockMvc.perform(post("/links")
                .header(Headers.TG_CHAT_ID, 1)
                .content(new ObjectMapper().writeValueAsBytes(new AddLinkRequest("link")))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    void deleteRequest_shouldReturnTooManyRequests() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(false);
        mockMvc.perform(delete("/links")
                .header(Headers.TG_CHAT_ID, 1)
                .content(new ObjectMapper().writeValueAsBytes(new AddLinkRequest("link")))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isTooManyRequests());
    }

    @Test
    void getRequest_shouldReturnOk() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(true);
        mockMvc.perform(get("/links")
                .header(Headers.TG_CHAT_ID, 1)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void postRequest_shouldReturnOk() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(true);
        Mockito.when(linksTransformService.toLink(Mockito.any(AddLinkRequest.class))).thenReturn(new Link());
        mockMvc.perform(post("/links")
                .header(Headers.TG_CHAT_ID, 1)
                .content(new ObjectMapper().writeValueAsBytes(new AddLinkRequest("link")))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void deleteRequest_shouldReturnOk() throws Exception {
        Mockito.when(bucket.tryConsume(Mockito.anyLong())).thenReturn(true);
        Mockito.when(linksTransformService.toLink(Mockito.any(RemoveLinkRequest.class))).thenReturn(new Link());
        mockMvc.perform(delete("/links")
                .header(Headers.TG_CHAT_ID, 1)
                .content(new ObjectMapper().writeValueAsBytes(new RemoveLinkRequest("link")))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

}
