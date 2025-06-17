package com.example.demo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.example.demo.model.ImageDto;
import com.example.demo.model.MessageDetailsDto;
import com.example.demo.model.MessageDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class DemoEndpoint {

    @Autowired
    private DemoService service;

    @Autowired
    private DtoMapper mapper;

    @GetMapping("/gen")
    public ResponseEntity<Void> generate(
            @RequestParam(value = "name", defaultValue = "World") String name,
            @RequestParam(value = "size", defaultValue = "512") Integer size,
            @RequestParam(value = "type", defaultValue = "application/octet-stream") String type,
            @RequestParam(value = "count", defaultValue = "1") Integer count) {
        log.info("GET /gen");
        service.generate(name, size, type, count);

        return ResponseEntity.ok().build();
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
                .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename={}.bin", id))
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
                .contentType(MediaType.valueOf(image.type()))
                .body(image.data());
    }
}
