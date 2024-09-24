package com.pard.root.content.repo;

import com.pard.root.content.entity.Content;
import com.pard.root.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("select c from Content c where c.user == :user order by c.id desc")
    List<Content> findAllByUser(@Param("user") User user);
}
