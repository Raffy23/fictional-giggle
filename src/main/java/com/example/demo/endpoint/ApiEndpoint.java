package com.example.demo.endpoint;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.example.demo.DemoService;
import com.example.demo.DtoMapper;
import com.example.demo.model.ImageDto;
import com.example.demo.model.MessageDetailsDto;
import com.example.demo.model.MessageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * The ApiEndpoint controller exposes a Rest API that can be used to interact with 
 * the service.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/")
public class ApiEndpoint {

    @Autowired
    private DemoService service;

    @Autowired
    private DtoMapper mapper;

    @RequestMapping(path = { "/generate" }, method = { RequestMethod.GET, RequestMethod.POST })
    public MessageDetailsDto generate(
            @RequestParam(value = "name", defaultValue = "World") String name,
            @RequestParam(value = "size", defaultValue = "1024") Integer size,
            @RequestParam(value = "type", defaultValue = "application/octet-stream") String type) {
        log.info("GET /gen");

        return mapper.messageToMessageDto(service.generate(name, "<generated>", List.of(size)));
    }

    @GetMapping("/message")
    public List<MessageDto> messages() {
        log.info("GET /message");
        return mapper.messageListToMessageDtoList(service.getMessageOverviewList());
    }

    @GetMapping("/message/{id}")
    public MessageDetailsDto list(@PathVariable(value = "id") Long id) {
        log.info("GET /message/{}", id);
        return mapper.messageToMessageDto(service.getMessageDetails(id));
    }

    @GetMapping("/image")
    public List<ImageDto> images() {
        log.info("GET /image");
        return mapper.imageListToImageDtoList(service.getImages());
    }

    @GetMapping("/image/{id}/:size")
    public long getImageSize(@PathVariable(value = "id") Long id) throws SQLException {
        log.info("GET /image/{id}/:size");
        return service.getImageSize(id);
    }

    @GetMapping("/image/{id}/:type")
    public String getImageType(@PathVariable(value = "id") Long id) throws SQLException {
        log.info("GET /image/{id}/:type");
        return service.getImageType(id);
    }

    @GetMapping("/image/{id}/:stream")
    public ResponseEntity<StreamingResponseBody> stream(@PathVariable(value = "id") Long id)
            throws SQLException, IOException {
        log.info("GET /image/{}/:stream", id);

        final var image = service.streamImageData(id);
        log.info("type={}", image.type());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", image.name()))
                .contentType(MediaType.valueOf(image.type()))
                .body(image.stream()::accept);
    }

    @GetMapping("/image/{id}/:materialize")
    public ResponseEntity<byte[]> materialize(@PathVariable(value = "id") Long id)
            throws SQLException, IOException {
        log.info("GET /image/{}/:materialize", id);

        final var image = service.getImageData(id);
        log.info("type={}", image.type());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", image.name()))
                .contentType(MediaType.valueOf(image.type()))
                .body(image.data());
    }
}
