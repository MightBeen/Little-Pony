package com.io.core.common.dispacher;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SysTaskExecutor {

    private long timeSnap = 200L;

    private final Map<Integer, TaskProxy> tasks = new HashMap<>();

    public void regularTask(Task task) {
        TaskProxy proxy = tasks.get(task.getId());

        if (proxy != null) {
            if (System.currentTimeMillis() - proxy.lastExecuteTime > timeSnap)
                tasks.remove(task.getId());
            else
                return;
        }

        TaskProxy newTask = new TaskProxy(task);

        tasks.put(task.getId(), newTask);
        newTask.run();
    }



    static class TaskProxy implements Runnable {
        private Task task;
        long lastExecuteTime;
        public TaskProxy(Task task) {
            this.lastExecuteTime = System.currentTimeMillis();
            this.task = task;
        }


        @Override
        public void run() {

            task.run();

        }
    }
}
