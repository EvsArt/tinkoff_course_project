package edu.java.client;

import edu.java.dto.stackoverflow.StackOverflowQuestionRequest;
import edu.java.dto.stackoverflow.StackOverflowQuestionResponse;
import reactor.core.publisher.Mono;

public interface StackOverflowClient extends Client {

    Mono<StackOverflowQuestionResponse> getQuestion(StackOverflowQuestionRequest request);

}
