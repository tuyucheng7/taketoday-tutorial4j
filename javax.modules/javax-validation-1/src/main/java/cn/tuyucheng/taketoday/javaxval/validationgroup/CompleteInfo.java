package cn.tuyucheng.taketoday.javaxval.validationgroup;

import jakarta.validation.GroupSequence;

@GroupSequence({ BasicInfo.class, AdvanceInfo.class })
public interface CompleteInfo {

}
