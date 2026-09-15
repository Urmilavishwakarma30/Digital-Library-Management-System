package com.library.digitallibrary.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.library.digitallibrary.entity.User;
import com.library.digitallibrary.entity.UserQuery;
import com.library.digitallibrary.repository.UserQueryRepository;

@Service
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;

    public UserQueryService(UserQueryRepository userQueryRepository) {
        this.userQueryRepository = userQueryRepository;
    }

    // Save user query
    public UserQuery saveQuery(UserQuery query, User user) {
        query.setUser(user);
        query.setQueryDate(LocalDateTime.now());

        return userQueryRepository.save(query);
    }

    // Get all queries for admin
    public List<UserQuery> getAllQueries() {
        return userQueryRepository.findAll();
    }

    // Get queries of a particular user
    public List<UserQuery> getUserQueries(User user) {
        return userQueryRepository.findByUser(user);
    }
}