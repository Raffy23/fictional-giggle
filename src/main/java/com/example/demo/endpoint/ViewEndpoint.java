package com.example.demo.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.DemoService;
import com.example.demo.DemoService.CreateImageDto;
import com.example.demo.model.CreateMessageDto;
import com.example.demo.model.GenerateMessageDto;

import lombok.extern.slf4j.Slf4j;

/**
 * The ViewEndpoint controller is used for Thymeleaf views / endpoints
 */
@Slf4j
@Controller
public class ViewEndpoint {

    @Autowired
    private DemoService service;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("messages", service.getMessageOverviewList());
        model.addAttribute("message", new CreateMessageDto());
        model.addAttribute("gen_message", new GenerateMessageDto("", "", 512));

        return "index";
    }

    @PostMapping("/message")
    public String createMessage(
            @RequestParam("title") String title,
            @RequestParam("summary") String summary,
            @RequestParam("message") String message,
            @RequestParam("images") List<MultipartFile> imageMultipartFiles) throws IOException {
        log.info("POST /message");

        final var dto = new CreateMessageDto(title, summary, message);
        final var images = new ArrayList<CreateImageDto>(imageMultipartFiles.size());
        for (final var file : imageMultipartFiles) {
            if (file.isEmpty()) {
                continue;
            }

            images.add(
                    new CreateImageDto(
                            file.getContentType(),
                            file.getOriginalFilename(),
                            file.getBytes()));
        }

        service.createMessage(dto, images);

        return "redirect:/";
    }

    @PostMapping(path = "/message/:generate", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public String generate(GenerateMessageDto dto) {
        service.generate(dto.title(), dto.summary(), List.of(dto.size()));

        return "redirect:/";
    }

    @GetMapping("/message/{id}")
    public String getMessageDetails(@PathVariable("id") Long id, Model model) {
        model.addAttribute("message", service.getMessageDetails(id));

        return "fragments/message-details :: dialog";
    }

}
