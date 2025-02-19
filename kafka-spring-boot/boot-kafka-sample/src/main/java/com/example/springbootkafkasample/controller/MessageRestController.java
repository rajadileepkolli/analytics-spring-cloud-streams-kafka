package com.example.springbootkafkasample.controller;

import com.example.springbootkafkasample.dto.KafkaListenerRequest;
import com.example.springbootkafkasample.dto.MessageDTO;
import com.example.springbootkafkasample.dto.TopicInfo;
import com.example.springbootkafkasample.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class MessageRestController {

    private final MessageService messageService;

    MessageRestController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Get the state of all Kafka listeners")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Retrieved listeners state successfully",
                        content =
                                @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
            })
    @GetMapping("/listeners")
    ResponseEntity<Map<String, Boolean>> getListeners() {
        return ResponseEntity.ok(messageService.getListenersState());
    }

    @Operation(summary = "Update the state of a Kafka listener")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Listener state updated successfully",
                        content =
                                @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
                @ApiResponse(responseCode = "404", description = "Listener not found", content = @Content)
            })
    @PostMapping("/listeners")
    ResponseEntity<Map<String, Boolean>> updateListenerState(
            @RequestBody @Valid final KafkaListenerRequest kafkaListenerRequest) {
        return ResponseEntity.ok(messageService.updateListenerState(kafkaListenerRequest));
    }

    @PostMapping("/messages")
    void sendMessage(@RequestBody MessageDTO messageDTO) {
        this.messageService.send(messageDTO);
    }

    @GetMapping("/topics")
    List<TopicInfo> getTopicsWithPartitionsCount(
            @RequestParam(required = false, defaultValue = "false") boolean showInternalTopics) {
        return messageService.getTopicsWithPartitions(showInternalTopics);
    }
}
