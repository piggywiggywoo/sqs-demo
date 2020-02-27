package org.cambridge.aop.sqsdemo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SampleJsonMessage {

    String field1;
    List<SampleJsonInnerClass> field2;

    @Getter
    @Setter
    static class SampleJsonInnerClass {
        String innerField1;
        String innerField2;
    }
}
