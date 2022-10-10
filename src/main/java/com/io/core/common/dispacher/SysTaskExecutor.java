package com.io.core.common.dispacher;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SysTaskExecutor {

    private long timeSnap = 200L;

    private final Map<Integer, TaskProxy> tasks = new HashMap<>();

    public void regularTask(Task task) {
        // TODO: 2022/10/9 fix this
        TaskProxy proxy = tasks.get(task.getId());

        // 如果存在同样的任务
        if (proxy != null) {
            // 如果任务正在执行,则阻塞
            while (proxy.lastExecuteTime == null) {
            }
            // 如果已过期， 则删除
            if (System.currentTimeMillis() - proxy.lastExecuteTime > timeSnap)
                tasks.remove(task.getId());
            // 否则任务不会重复执行
            else
                return;
        }

        TaskProxy newTask = new TaskProxy(task);

        tasks.put(task.getId(), newTask);
        newTask.run();
    }



    static class TaskProxy implements Runnable {
        private Task task;
        Long lastExecuteTime;
        public TaskProxy(Task task) {
//            this.lastExecuteTime = System.currentTimeMillis();
            this.task = task;
        }


        @Override
        public void run() {

            task.run();
            this.lastExecuteTime = System.currentTimeMillis();
        }
    }
}
