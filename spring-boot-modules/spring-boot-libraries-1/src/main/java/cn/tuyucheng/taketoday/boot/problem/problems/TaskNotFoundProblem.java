package cn.tuyucheng.taketoday.boot.problem.problems;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class TaskNotFoundProblem extends AbstractThrowableProblem {

   private static final URI TYPE = URI.create("https://example.org/not-found");

   public TaskNotFoundProblem(Long taskId) {
      super(TYPE, "Not found", Status.NOT_FOUND, STR."Task '\{taskId}' not found");
   }
}