package dev.lufebraz.posts;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("")
    List<Post> findAll() {
        return postRepository.findAll();
    }
    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable int id) {
        return Optional.ofNullable(postRepository.findById(id).orElseThrow(PostNotFoundException::new));
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    Post create(@RequestBody @Valid Post post) {
        return postRepository.save(post);
    }
    @PutMapping("/{id}")
    Post save(@PathVariable Integer id, @RequestBody @Valid Post post) {
        Optional<Post> byId = this.postRepository.findById(id);
        if (byId.isPresent()){
            Post updatedPost = new Post(
                    byId.get().id(),
                    byId.get().userId(),
                    post.title(),
                    post.body(),
                    post.version()
                    );
            return postRepository.save(updatedPost);
        } else {
            throw new PostNotFoundException();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        postRepository.deleteById(id);
    }
}
