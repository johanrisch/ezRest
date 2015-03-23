package evertsson.risch.ezDispatch;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * Created by johanrisch on 6/18/13.
 */

/**
 * The thread class used by {@see ezDispatch} to execute {@see ezBlock} on.
 */
public class ezConcurrentThread {

    private ThreadPoolExecutor mService;

    /**
     * Boolean used to determine if the thread should continue.
     */
    protected boolean running = true;

    public ezConcurrentThread() {
        mService = new ThreadPoolExecutor(8,8,60L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Puts a block into the Queue
     * @param block the block to push.
     */
    synchronized void putBlock(ezBlock block) {
       mService.submit(block);
    }
}
