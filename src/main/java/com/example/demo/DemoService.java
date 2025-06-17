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

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class DemoService {
    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ChildRepository childRepository;

    public void generate(String name, int size, String type, int count) {
        log.info("generate({}, {}, {}, {})", name, size, type, count);
        final var random = new Random();

        final List<Child> children = new ArrayList<>(count);
        for (var i = 0; i < count; i++) {
            final byte[] bytes = new byte[size];
            random.nextBytes(bytes);

            final var blob = BlobProxy.generateProxy(bytes);

            children.add(Child.of(type, blob));
        }

        final var parent = Parent.of(name, children);

        childRepository.saveAll(children);
        parentRepository.save(parent);

        log.info("created Parent: {}", parent.getId());
    }

    public List<Parent> parentList() {
        log.info("parentList()");
        
        // Required if jpa.open-in-view = false
        final var list = parentRepository.findAll();
        for(final var parent : list) {
            Hibernate.initialize(parent.getChildren());
        }

        return parentRepository.findAll();
    }

    public int getChildrenCount(Long id) {
        log.info("getChildrenCount({})", id);

        final var parent = parentRepository.getReferenceById(id);
        return parent.getChildren().size();
    }

    public long getChildSize(Long id) throws SQLException {
        log.info("getChildSize({})", id);

        final var child = childRepository.getReferenceById(id);
        return child.getData().length();
    }

    public static record ChildContentBytes(String type, byte[] data) {
    }

    public ChildContentBytes getChildContent(Long id) throws SQLException, IOException {
        log.info("getChildContent({})", id);

        final var child = childRepository.getReferenceById(id);
        final var inputStream = child.getData().getBinaryStream();

        final var data = new byte[(int) child.getData().length()];
        inputStream.read(data);
        inputStream.close();

        return new ChildContentBytes(child.getType(), data);
    }

    @FunctionalInterface
    public static interface OutputStreamCallback<T> {
        void accept(T t) throws IOException;
    }

    public static record ChildContentStream(String type, OutputStreamCallback<OutputStream> stream) {
    }

    public ChildContentStream streamChildContent(Long id) throws SQLException, IOException {
        log.info("streamChildContent({})", id);
        
        final var child = childRepository.getReferenceById(id);
        final var inputStream = child.getData().getBinaryStream();

        return new ChildContentStream(child.getType(), (outputStream) -> {
            inputStream.transferTo(outputStream);
            inputStream.close();
        });
    }

}
