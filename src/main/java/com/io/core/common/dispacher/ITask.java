package com.io.core.common.dispacher;

public interface ITask extends Runnable{
    default void BeforeRun() {

    }

    default void AfterRun() {

    }
}
