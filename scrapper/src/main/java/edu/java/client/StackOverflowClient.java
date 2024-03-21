package edu.java.client;

import edu.java.dto.StackOverflowQuestionRequest;
import edu.java.dto.StackOverflowQuestionResponse;
import reactor.core.publisher.Mono;

public interface StackOverflowClient extends Client {

    Mono<StackOverflowQuestionResponse> getQuestion(StackOverflowQuestionRequest request);

}
