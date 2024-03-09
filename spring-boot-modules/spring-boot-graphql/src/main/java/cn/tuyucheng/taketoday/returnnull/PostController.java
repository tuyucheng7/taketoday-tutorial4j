package cn.tuyucheng.taketoday.returnnull;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class PostController {

   List<Post> posts = new ArrayList<>();

   @MutationMapping
   public Void createPostReturnCustomScalar(@Argument String title, @Argument String text, @Argument String category, @Argument String author) {
      Post post = new Post();
      post.setId(UUID.randomUUID()
            .toString());
      post.setTitle(title);
      post.setText(text);
      post.setCategory(category);
      post.setAuthor(author);
      posts.add(post);
      return null;
   }
}