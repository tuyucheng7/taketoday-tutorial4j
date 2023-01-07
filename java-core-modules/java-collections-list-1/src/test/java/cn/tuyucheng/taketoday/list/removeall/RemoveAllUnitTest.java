package cn.tuyucheng.taketoday.list.removeall;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithCallingRemoveUntilModifies;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithCollectingAndReturningRemainingElements;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithCollectingRemainingElementsAndAddingToOriginalList;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithForEachLoop;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithForLoopDecrementOnRemove;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithForLoopIncrementIfRemains;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithIterator;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithRemoveIf;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithStandardForLoopUsingIndex;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithStreamFilter;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithWhileLoopNonPrimitiveElement;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithWhileLoopPrimitiveElement;
import static cn.tuyucheng.taketoday.list.removeall.RemoveAll.removeWithWhileLoopStoringFirstOccurrenceIndex;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RemoveAllUnitTest {

    private List<Integer> list(Integer... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    @Test
    public void givenAList_whenRemovingElementsWithWhileLoopUsingPrimitiveElement_thenTheResultCorrect() {
        // given
        List<Integer> list = list(1, 2, 3);
        int valueToRemove = 1;

        // when
        assertThatThrownBy(() -> removeWithWhileLoopPrimitiveElement(list, valueToRemove))
              .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void givenAList_whenRemovingElementsWithWhileLoopUsingNonPrimitiveElement_thenTheResultCorrect() {
        // given
        List<Integer> list = list(1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithWhileLoopNonPrimitiveElement(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithWhileLoopStoringFirstOccurrenceIndex_thenTheResultCorrect() {
        // given
        List<Integer> list = list(1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithWhileLoopStoringFirstOccurrenceIndex(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithCallingRemoveUntilModifies_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithCallingRemoveUntilModifies(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAListWithoutDuplication_whenRemovingElementsWithStandardForLoopUsingIndex_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithStandardForLoopUsingIndex(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAListWithAdjacentElements_whenRemovingElementsWithStandardForLoop_thenTheResultIsInCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithStandardForLoopUsingIndex(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(1, 2, 3));
    }

    @Test
    public void givenAListWithAdjacentElements_whenRemovingElementsWithForLoopAndDecrementOnRemove_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithForLoopDecrementOnRemove(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAListWithAdjacentElements_whenRemovingElementsWithForLoopAndIncrementIfRemains_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithForLoopIncrementIfRemains(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithForEachLoop_thenExceptionIsThrown() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        assertThatThrownBy(() -> removeWithForEachLoop(list, valueToRemove))
              .isInstanceOf(ConcurrentModificationException.class);
    }

    @Test
    public void givenAList_whenRemovingElementsWithIterator_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithIterator(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithCollectingAndReturningRemainingElements_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        List<Integer> result = removeWithCollectingAndReturningRemainingElements(list, valueToRemove);

        // then
        assertThat(result).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithCollectingRemainingAndAddingToOriginalList_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithCollectingRemainingElementsAndAddingToOriginalList(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithStreamFilter_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        List<Integer> result = removeWithStreamFilter(list, valueToRemove);

        // then
        assertThat(result).isEqualTo(list(2, 3));
    }

    @Test
    public void givenAList_whenRemovingElementsWithCallingRemoveIf_thenTheResultIsCorrect() {
        // given
        List<Integer> list = list(1, 1, 2, 3);
        int valueToRemove = 1;

        // when
        removeWithRemoveIf(list, valueToRemove);

        // then
        assertThat(list).isEqualTo(list(2, 3));
    }

}
