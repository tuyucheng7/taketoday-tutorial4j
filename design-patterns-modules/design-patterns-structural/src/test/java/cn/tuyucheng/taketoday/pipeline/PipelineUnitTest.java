package cn.tuyucheng.taketoday.pipeline;

import static org.assertj.core.api.Assertions.assertThat;

import cn.tuyucheng.taketoday.pipeline.immutable.Pipe;
import cn.tuyucheng.taketoday.pipeline.immutable.Pipeline;
import org.junit.jupiter.api.Test;

class PipelineUnitTest {

    @Test
    void simplePipelineTest() {
        Pipe<Integer, Integer> square = s -> s * s;
        Pipe<Integer, Integer> half = s -> s / 2;
        Pipe<Integer, String> toString = Object::toString;
        Pipeline<Integer, Integer> squarePipeline = Pipeline.of(square);
        Pipeline<Integer, Integer> squareAndHalfPipeline = squarePipeline.withNextPipe(half);
        Pipeline<Integer, String> squareHalfAndStringPipeline = squareAndHalfPipeline.withNextPipe(toString);

        String result = squareHalfAndStringPipeline.process(5);
        String expected = "12";
        assertThat(result).isEqualTo(expected);
    }
}