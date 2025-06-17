package com.example.demo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class DemoRestController {

    @Autowired
    private DemoService service;

    @Autowired
    private DtoMapper mapper;

    @GetMapping("/gen")
    public ResponseEntity<Void> generate(
            @RequestParam(value = "name", defaultValue = "World") String name,
            @RequestParam(value = "size", defaultValue = "512") Integer size,
            @RequestParam(value = "type", defaultValue = "bytes") String type,
            @RequestParam(value = "count", defaultValue = "1") Integer count) {
        log.info("GET /gen");
        service.generate(name, size, type, count);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public List<ParentDTO> index() {
        log.info("GET /");
        return mapper.parentListToParentDtoList(service.parentList());
    }

    @GetMapping("/list")
    public int list(@RequestParam(value = "id", defaultValue = "1") Long id) {
        log.info("GET /list");
        return service.getChildrenCount(id);
    }

    @GetMapping("/size")
    public long get(@RequestParam(value = "id", defaultValue = "1") Long id) throws SQLException {
        log.info("GET /get");
        return service.getChildSize(id);
    }

    @GetMapping("/stream")
    public ResponseEntity<StreamingResponseBody> stream(@RequestParam(value = "id", defaultValue = "1") Long id)
            throws SQLException, IOException {
        log.info("GET /stream");

        final var child = service.streamChildContent(id);
        log.info("type={}", child.type());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generic_file_name.bin")
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Should be details.type()
                .body(child.stream()::accept);
    }

    @GetMapping("/mat")
    public ResponseEntity<byte[]> materialize(@RequestParam(value = "id", defaultValue = "1") Long id)
            throws SQLException, IOException {
        log.info("GET /mat");

        final var details = service.getChildContent(id);
        log.info("type={}", details.type());

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Should be details.type()
                .body(details.data());
    }
}
