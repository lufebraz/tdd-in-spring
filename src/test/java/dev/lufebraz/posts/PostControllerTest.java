package dev.lufebraz.posts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostRepository postRepository;

    List<Post> posts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // create some posts
        this.posts = List.of(
                new Post(1, 1, "hello, world", "This is my first post.", null),
                new Post(2, 1, "hello, world", "This is my second post.", null)
        );
    }

    // Rest Api

    // list
    @Test
    public void shouldFindAllPosts() throws Exception {
        String jsonResponse = """
                [{"id":1,"userId":1,"title":"hello, world","body":"This is my first post.","version":null},{"id":2,"userId":1,"title":"hello, world","body":"This is my second post.","version":null}]         
                """;
        when(postRepository.findAll()).thenReturn(posts);
        this.mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    // api/posts/1
    @Test
    public void shouldFindPostWhenGivenValidId() throws Exception {
        String jsonResponse = """
                  {"id":1,"userId":1,"title":"hello, world","body":"This is my first post.","version":null}
                """;

        when(postRepository.findById(1)).thenReturn(Optional.of(posts.get(0)));
        this.mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    // /api/posts/999
    @Test
    public void shouldNotFindPostWhenGivenInvalidId() throws Exception {
        when(postRepository.findById(1)).thenThrow(PostNotFoundException.class);
        this.mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isNoContent());
    }


    @Test
    public void shouldCreateNewPostWhenPostIsValid() throws Exception {
        var post = new Post(3, 1, "new title", "new body", null);
        when(postRepository.save(post)).thenReturn(post);

        String json = """
                  {"id":1,"userId":1,"title":"hello, world","body":"This is my first post.","version":null}
                """;

        this.mockMvc.perform
                        (post("/api/posts")
                                .contentType("application/json")
                                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldNotCreatePostWhenPostIsInvalid() throws Exception {
        var post = new Post(3, 1, "", "", null);
        when(postRepository.save(post)).thenReturn(post);

        String json = """
                  {"id":1,"userId":1,"title":"","body":"","version":null}
                """;

        this.mockMvc.perform
                        (post("/api/posts")
                                .contentType("application/json")
                                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdatePostWhenPostIsValid() throws Exception {
        Post updated = new Post(1, 1, "new title", "new body", 1);

        when(postRepository.findById(1)).thenReturn(Optional.of(updated));
        when(postRepository.save(updated)).thenReturn(updated);

        String json = """
                  {"id":1,"userId":1,"title":"new title","body":"new body","version":1}
                """;
        this.mockMvc.perform(put("/api/posts/1")
                        .contentType("application/json")
                .content(json))
                .andExpect(status().isOk());

    }
    @Test
    public void shouldNotUpdatePostWhenPostIsInvalid() throws Exception {
        Post updated = new Post(1, 1, "new title", "new body", 1);

        when(postRepository.findById(1)).thenReturn(Optional.empty());
        when(postRepository.save(updated)).thenReturn(updated);

        String json = """
                  {"id":1,"userId":1,"title":"new title","body":"new body","version":1}
                """;
        this.mockMvc.perform(put("/api/posts/1")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNoContent());

    }
    @Test
    public void shouldDeletePostWhenGivenValidId() throws Exception {

        doNothing().when(postRepository).deleteById(1);

        this.mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(postRepository, times(1)).deleteById(1);
    }
}
