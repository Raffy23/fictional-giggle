package com.example.demo;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.demo.entitiy.Image;
import com.example.demo.entitiy.Message;
import com.example.demo.model.ImageDto;
import com.example.demo.model.MessageDetailsDto;
import com.example.demo.model.MessageDto;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    ImageDto imageToImageDto(Image image);

    MessageDetailsDto messageToMessageDto(Message message);
    
    List<MessageDto> messageListToMessageDtoList(List<Message> messages);

    List<ImageDto> imageListToImageDtoList(List<Image> images);
}
