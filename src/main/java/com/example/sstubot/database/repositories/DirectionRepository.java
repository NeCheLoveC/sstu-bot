package com.example.sstubot.database.repositories;

import com.example.sstubot.database.model.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectionRepository extends JpaRepository<Direction, Long> {
    @Query("select d from Direction d where d.urlToListOfClaims ILIKE :urlBudget")
    public Direction getDirectionByUrlToListOfClaims(String urlBudget);
    @Query("select d from Direction d where d.urlToListOfClaimsCommerce ILIKE :urlCommerce")
    public Direction getDirectionByUrlToListOfClaimsCommerce(String urlCommerce);
    //@Query("select d from Direction d left join fetch Claim order by d.allClaims.size")
    //public List<Direction> getDirWithClaims();
}
