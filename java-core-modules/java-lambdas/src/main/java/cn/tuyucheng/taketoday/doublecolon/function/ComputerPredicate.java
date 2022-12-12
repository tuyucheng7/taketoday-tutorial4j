package cn.tuyucheng.taketoday.doublecolon.function;

import cn.tuyucheng.taketoday.doublecolon.Computer;

@FunctionalInterface
public interface ComputerPredicate {

    boolean filter(Computer c);
}