package cn.tuyucheng.taketoday.netflix.mantis.job;

import cn.tuyucheng.taketoday.netflix.mantis.model.LogAggregate;
import cn.tuyucheng.taketoday.netflix.mantis.source.RandomLogSource;
import cn.tuyucheng.taketoday.netflix.mantis.stage.CountLogStage;
import cn.tuyucheng.taketoday.netflix.mantis.stage.GroupLogStage;
import cn.tuyucheng.taketoday.netflix.mantis.stage.TransformLogStage;
import io.mantisrx.runtime.Job;
import io.mantisrx.runtime.MantisJob;
import io.mantisrx.runtime.MantisJobProvider;
import io.mantisrx.runtime.Metadata;
import io.mantisrx.runtime.sink.Sink;
import io.mantisrx.runtime.sink.Sinks;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LogAggregationJob extends MantisJobProvider<LogAggregate> {

	private Sink<LogAggregate> sink = Sinks.eagerSubscribe(Sinks.sse(LogAggregate::toJsonString));

	@Override
	public Job<LogAggregate> getJobInstance() {

		return MantisJob
			.source(new RandomLogSource())
			.stage(new TransformLogStage(), TransformLogStage.stageConfig())
			.stage(new GroupLogStage(), GroupLogStage.config())
			.stage(new CountLogStage(), CountLogStage.config())
			.sink(sink)
			.metadata(new Metadata.Builder().build())
			.create();

	}

}
