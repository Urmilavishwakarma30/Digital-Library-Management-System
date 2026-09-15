package com.library.digitallibrary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.entity.UserQuery;

public interface UserQueryRepository extends JpaRepository<UserQuery, Long> {

    List<UserQuery> findByUser(User user);
}