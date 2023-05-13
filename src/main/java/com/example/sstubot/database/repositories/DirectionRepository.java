package com.example.sstubot.database.repositories;

import com.example.sstubot.database.model.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectionRepository extends JpaRepository<Direction, Long> {
    @Query("select d from Direction d where d.urlToListOfClaims ILIKE :urlBudget")
    public Direction getDirectionByUrlToListOfClaims(String urlBudget);
    @Query("select d from Direction d where d.urlToListOfClaimsCommerce ILIKE :urlCommerce")
    public Direction getDirectionByUrlToListOfClaimsCommerce(String urlCommerce);
}
