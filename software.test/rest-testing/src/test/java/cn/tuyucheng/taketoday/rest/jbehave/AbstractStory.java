package cn.tuyucheng.taketoday.rest.jbehave;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import java.util.Collections;
import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;

public abstract class AbstractStory extends JUnitStories {

   abstract String storyName();

   @Override
   public Configuration configuration() {
      return new MostUsefulConfiguration()
            .useStoryLoader(new LoadFromClasspath(this.getClass()))
            .useStoryReporterBuilder(new StoryReporterBuilder()
                  .withCodeLocation(codeLocationFromClass(this.getClass()))
                  .withFormats(CONSOLE));
   }

   abstract Object stepInstance();

   @Override
   public InjectableStepsFactory stepsFactory() {
      return new InstanceStepsFactory(configuration(), stepInstance());
   }

   @Override
   protected List<String> storyPaths() {
      return Collections.singletonList(storyName());
   }
}