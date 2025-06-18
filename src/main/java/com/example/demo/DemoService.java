package com.example.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.Hibernate;
import org.hibernate.engine.jdbc.proxy.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entitiy.Image;
import com.example.demo.entitiy.Message;
import com.example.demo.model.CreateMessageDto;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.MessageRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class DemoService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ImageRepository imageRepository;

    public Message generate(String name, int size, String type, int count) {
        log.info("generate({}, {}, {}, {})", name, size, type, count);
        final var random = new Random();

        final List<Image> images = new ArrayList<>(count);
        for (var i = 0; i < count; i++) {
            final byte[] bytes = new byte[size];
            random.nextBytes(bytes);

            final var blob = BlobProxy.generateProxy(bytes);

            images.add(Image.of(type, "<generated>", blob));
        }

        final var message = Message.of(name, "<generated>", "<generated>", images);
        messageRepository.save(message);

        // Force GC after generation of a lot of data.
        Runtime.getRuntime().gc();

        return message;
    }

    public static record CreateImageDto(String type, String name, byte[] data) {
    }

    public void createMessage(@Valid CreateMessageDto message, List<CreateImageDto> images) {
        final List<Image> imageEntities = new ArrayList<>(images.size());
        for (final var imageDto : images) {
            imageEntities.add(
                    Image.of(
                            imageDto.type(),
                            imageDto.name(),
                            BlobProxy.generateProxy(imageDto.data())));
        }

        final var messageEntity = Message.of(
                message.getTitle(),
                message.getSummary(),
                message.getMessage(),
                imageEntities);

        messageRepository.save(messageEntity);
    }

    public List<Message> getMessageOverviewList() {
        log.info("getMessageOverviewList()");
        return messageRepository.findAll();
    }

    public Message getMessageDetails(Long id) {
        log.info("getMessageDetails({})", id);

        final var message = messageRepository.getReferenceById(id);
        Hibernate.initialize(message.getImages());

        return message;
    }

    public long getImageSize(Long id) throws SQLException {
        log.info("getImageSize({})", id);

        final var child = imageRepository.getReferenceById(id);
        return child.getData().length();
    }

    public String getImageType(Long id) {
        log.info("getImageType({})", id);

        return imageRepository.getReferenceById(id).getType();
    }

    public List<Image> getImages() {
        log.info("getImages()");

        return imageRepository.findAll();
    }

    // region Child Data streaming / retrieval

    public static record ChildContentBytes(String type, String name, byte[] data) {
    }

    public ChildContentBytes getImageData(Long id) throws SQLException, IOException {
        log.info("getImageData({})", id);

        final var image = imageRepository.getReferenceByIdWithData(id);
        final var inputStream = image.getData().getBinaryStream();

        final var data = new byte[(int) image.getData().length()];
        inputStream.read(data);
        inputStream.close();

        return new ChildContentBytes(image.getType(), image.getName(), data);
    }

    @FunctionalInterface
    public static interface OutputStreamCallback<T> {
        void accept(T t) throws IOException;
    }

    public static record ChildContentStream(String type, String name, OutputStreamCallback<OutputStream> stream) {
    }

    public ChildContentStream streamImageData(Long id) throws SQLException, IOException {
        log.info("streamImageData({})", id);

        final var image = imageRepository.getReferenceByIdWithData(id);
        final var inputStream = image.getData().getBinaryStream();

        return new ChildContentStream(image.getType(), image.getName(), (outputStream) -> {
            inputStream.transferTo(outputStream);
            inputStream.close();
        });
    }

    // endregion

}
