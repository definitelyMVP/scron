package def.mvp.scron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * @author defMVP
 * @date 2013-02-04
 */
public class SingleNodeTaskScheduler extends ThreadPoolTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(SingleNodeTaskScheduler.class);

    private LeaderElector leaderElector;

    private Runnable taskWrapper(final Runnable task) {
        return new Runnable() {
            @Override
            public void run() {
                if (leaderElector.isLeader()) {
                    task.run();
                    log.info("===========cron job has been executed.=========");
                } else {
                    log.info("===========NOT leader, cron job will NOT be executed!===========");
                }
            }
        };
    }

    @Override
    public ScheduledFuture schedule(Runnable task, Trigger trigger) {
        return super.schedule(taskWrapper(task), trigger);
    }

    @Override
    public ScheduledFuture schedule(Runnable task, Date startTime) {
        return super.schedule(taskWrapper(task), startTime);
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return super.scheduleAtFixedRate(taskWrapper(task), startTime, period);
    }

    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable task, long period) {
        return super.scheduleAtFixedRate(taskWrapper(task), period);
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return super.scheduleWithFixedDelay(taskWrapper(task), startTime, delay);
    }

    @Override
    public ScheduledFuture scheduleWithFixedDelay(Runnable task, long delay) {
        return super.scheduleWithFixedDelay(taskWrapper(task), delay);
    }

    public LeaderElector getLeaderElector() {
        return leaderElector;
    }

    public void setLeaderElector(LeaderElector leaderElector) {
        this.leaderElector = leaderElector;
    }
}
