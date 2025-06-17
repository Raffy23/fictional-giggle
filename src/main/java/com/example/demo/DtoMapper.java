package com.example.demo;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    ChildDTO childToChildDto(Child child);

    ParentDTO parentToParentDto(Parent parent);
    
    List<ParentDTO> parentListToParentDtoList(List<Parent> parents);
}
