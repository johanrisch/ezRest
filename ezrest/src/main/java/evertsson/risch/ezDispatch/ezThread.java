package evertsson.risch.ezDispatch;

import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * Created by johanrisch on 6/18/13.
 */

/**
 * The thread class used by {@see ezDispatch} to execute {@see ezBlock} on.
 */
public class ezThread {

    private ExecutorService mService;
    private HashMap<String,Future<?>> mFutures;


    public ezThread(int coreThreads, int maxThreads, long timeout, BlockingDeque<Runnable> queue) {
        this.mService = new ThreadPoolExecutor(coreThreads,maxThreads,timeout,TimeUnit.SECONDS,queue);
        this.mFutures = new HashMap<>();
    }

    /**
     * Puts a block into the Queue and discards the Future<?> created by it
     * @param block the block to push.
     */
    synchronized void putBlock(Callable<?> block) {
        mService.submit(block);
    }

    /**
     * Puts a block into the Queue and saves the futures for future references, the caller is responsible for keeping the keys unique.
     * @param block the block to push.
     * @param key the key to save the future with.
     */
    synchronized void putBlock(Callable<?> block,String key) {
        mFutures.put(key, mService.submit(block));
    }

    /**
     * Return the future saved with the supplied key. NOTE: this call clears the entry for the key.
     * If you call this two times in a row the second call will always return null.
     * @param key
     * @return the future saved with key, or null if it has been called with the same key before
.     */
    public Future<?> getFuture(String key){
        Future<?> f = mFutures.get(key);
        mFutures.put(key,null);
        return f;
    }

}
