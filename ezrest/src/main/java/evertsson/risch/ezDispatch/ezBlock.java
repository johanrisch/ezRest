package evertsson.risch.ezDispatch;


import java.util.concurrent.Callable;

/**
 * @Deprecated
 * Please use {@link java.util.concurrent.Callable} instead.
 * Created by johanrisch on 6/18/13.
 * <br/>
 * This is the smallest stone of ezDispatch. An ezBlock contains the code to be executed
 * on a selected Queue.
 */
@Deprecated
public abstract class ezBlock implements Callable<Integer>{
    private ezThread mThread;

    /**
     * Creates an ezBlock with a handler created from the calling thread.
     */
    public ezBlock(){
    }

    @Override
    public Integer call() throws Exception {
        return doAction();
    }

    public abstract Integer doAction();
}
