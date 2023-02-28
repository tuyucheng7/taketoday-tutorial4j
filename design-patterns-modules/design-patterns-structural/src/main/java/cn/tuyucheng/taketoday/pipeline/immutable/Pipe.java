package cn.tuyucheng.taketoday.pipeline.immutable;

public interface Pipe<IN, OUT> {
    OUT process(IN input);
}
