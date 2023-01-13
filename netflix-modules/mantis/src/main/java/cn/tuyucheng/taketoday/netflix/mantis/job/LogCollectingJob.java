package cn.tuyucheng.taketoday.netflix.mantis.job;

import cn.tuyucheng.taketoday.netflix.mantis.model.LogEvent;
import cn.tuyucheng.taketoday.netflix.mantis.sink.LogSink;
import cn.tuyucheng.taketoday.netflix.mantis.source.RandomLogSource;
import cn.tuyucheng.taketoday.netflix.mantis.stage.TransformLogStage;
import io.mantisrx.runtime.Job;
import io.mantisrx.runtime.MantisJob;
import io.mantisrx.runtime.MantisJobProvider;
import io.mantisrx.runtime.Metadata;
import io.mantisrx.runtime.ScalarToScalar;
import io.mantisrx.runtime.sink.Sink;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class LogCollectingJob extends MantisJobProvider<LogEvent> {

	private Sink<LogEvent> sink = new LogSink();

	@Override
	public Job<LogEvent> getJobInstance() {

		return MantisJob
			.source(new RandomLogSource())
			.stage(new TransformLogStage(), new ScalarToScalar.Config<>())
			.sink(sink)
			.metadata(new Metadata.Builder().build())
			.create();

	}

}
