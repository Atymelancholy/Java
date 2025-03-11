package com.example.bookblog.service;

import com.example.bookblog.entity.Response;
import com.example.bookblog.entity.User;
import com.example.bookblog.exception.PostNotFoundException;
import com.example.bookblog.exception.UserNotFoundException;
import com.example.bookblog.repository.ResponseRepository;
import com.example.bookblog.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {
    private final ResponseRepository responseRepository;
    private final UserRepository userRepository;

    public ResponseService(ResponseRepository responseRepository, UserRepository userRepository) {
        this.responseRepository = responseRepository;
        this.userRepository = userRepository;
    }

    public Response createPost(Long userId, String content) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Response post = new Response(content, user);
        return responseRepository.save(post);
    }

    public List<Response> getUserPosts(Long userId) throws PostNotFoundException {
        List<Response> posts = responseRepository.findByUserId(userId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException("No posts found for user with id: " + userId);
        }
        return posts;
    }

    public void deletePost(Long postId) throws PostNotFoundException {
        if (!responseRepository.existsById(postId)) {
            throw new PostNotFoundException("Post with id " + postId + " not found.");
        }
        responseRepository.deleteById(postId);
    }

    public void updatePost(Long postId, String content) throws PostNotFoundException {
        Response post = responseRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException("Post with this id does not exist!!!"));
        post.setContent(content);
        responseRepository.save(post);
    }
}