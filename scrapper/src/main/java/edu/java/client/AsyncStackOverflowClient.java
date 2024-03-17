package edu.java.client;

import edu.java.dto.StackOverflowQuestionResponse;
import reactor.core.publisher.Mono;

public interface AsyncStackOverflowClient extends Client {

    Mono<StackOverflowQuestionResponse> getQuestionById(Long id);

}
