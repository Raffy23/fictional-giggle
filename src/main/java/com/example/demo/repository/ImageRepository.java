package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.entitiy.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

    /**
     * Eagerly fetches the id, type and data properties from an {@link Image} with the specified id.
     * 
     * @param id - Id of the Image
     * @return a Image entity
     * @see EntityManager#getReference(Class, Object) for details on when an exception is thrown.
     */
    @Query("SELECT new Image(id, type, name, data) FROM Image WHERE id= ?1")
    Image getReferenceByIdWithData(Long id);

}
