package evertsson.risch.ezDispatch;

import android.os.Handler;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by johanrisch on 6/18/13.
 */
public class ezDispatch {
    /**
     * The normal priority thread.
     */
    private static ezThread sNormalThread;
    /**
     * The low priority thread
     */
    private static ezThread sLowThread;
    /**
     * the high priority thread.
     */
    private static ezThread sHighThread;

    /**
     * Static int telling {@see ezDispatch} to execute on the high priority thread
     * Constant value = 0
     */
    public static final int HIGH = 0;
    /**
     * Static int telling {@see ezDispatch} to execute on the normal priority thread
     * Constant value = 1
     */
    public static final int NORMAL = 1;
    /**
     * Static int telling {@see ezDispatch} to execute on the low priority thread
     * Constant value = 2
     */
    public static final int LOW = 2;
    /**
     * Static int telling {@see ezDispatch} to execute on the UI thread
     * Constant value = 3
     */
    public static final int MAIN = 3;
    /**
     * Handler used to execute on UI thread.
     */
    private Handler sMainHandler;
    /**
     * Map used to cache method mappings.
     */
    private HashMap<String, Method> mMethodMap;

    private static ezDispatch instance = null;

    private ezDispatch() {
        initezDispatch();
    }


    private static int sHighCore = 4;
    private static int sHighMax = 8;
    private static long sHighTimeout = 60L;
    private static BlockingDeque<Runnable> sHighQueue = null;

    private static int sNormalCore = 4;
    private static int sNormalMax = 8;
    private static long sNormalTimeout = 60L;
    private static BlockingDeque<Runnable> sNormalQueue = null;

    private static int sLowCore = 4;
    private static int sLowMax = 8;
    private static long sLowTimeout = 60L;
    private static BlockingDeque<Runnable> sLowQueue = null;

    /**
     * Method to get an initialized instance of ezDispatch
     *
     * @return an initialized instance of ezDispatch
     */
    public static synchronized ezDispatch getInstance() {
        if (instance == null) {
            instance = new ezDispatch();
            instance.initezDispatch();
        }
        return instance;
    }


    /**
     * initiates ezDispatch. MUST be called at before scheduling blocks.
     */
    public void initezDispatch() {
        //init HighThread
        if(sHighQueue == null){
            sHighQueue = new LinkedBlockingDeque<>();
        }
        //init NormalThread
        if(sNormalQueue == null){
            sNormalQueue = new LinkedBlockingDeque<>();
        }
        //init LowQueue
        if(sLowQueue == null){
            sLowQueue = new LinkedBlockingDeque<>();
        }



        sMainHandler = new Handler();
        sHighThread = new ezThread(sHighCore,sHighMax,sHighTimeout, sHighQueue);
        sNormalThread = new ezThread(sNormalCore,sNormalMax,sNormalTimeout, sNormalQueue);
        sLowThread = new ezThread(sLowCore,sLowMax,sLowTimeout,sLowQueue);
        mMethodMap = new HashMap<String, Method>(250);
    }


    public static void setUpHighThread(int core, int max, long timeout, BlockingDeque<Runnable> queue){
        sHighCore = core;
        sHighMax = max;
        sHighTimeout = timeout;
        sHighQueue = queue;
    }

    public static void setUpNormalThread(int core, int max, long timeout, BlockingDeque<Runnable> queue){
        sNormalCore = core;
        sNormalMax = max;
        sNormalTimeout = timeout;
        sNormalQueue = queue;
    }

    public static void setUpLowThread(int core, int max, long timeout, BlockingDeque<Runnable> queue){
        sLowCore = core;
        sLowMax = max;
        sLowTimeout = timeout;
        sLowQueue = queue;
    }

    /**
     * Schedules a block for execution on the given queue.
     *
     * @param block the block to be executed on the chosen queue
     * @param queue the queue to execute the supplied block on.
     * @return true if the enqueueing was successfull.
     * @throws RuntimeException if the supplied queue does not exist.
     */
    public boolean executeOn(int queue, final Callable<?> block) {
        Log.d("ezDispatch", "scheduling execution of a block on " + queue);
        switch (queue) {
            case HIGH:
                sHighThread.putBlock(block);
                break;
            case NORMAL:
                sNormalThread.putBlock(block);
                break;
            case LOW:
                sLowThread.putBlock(block);
                break;
            case MAIN:
                sMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            block.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            default:
                throw new RuntimeException("Invalid thread ID, " + queue +
                        " please supply one of LOW, NORMAL, HIGH or MAIN");
        }
        return true;
    }

    /**
     * Schedules a method for execution on the supplied thread.
     * ezDispatch keeps an internal Hashmap of method name and containing class in order to speed up consecutive
     * calls to the method. At the moment polymorphism is not supported.
     *
     * @param queue    that the method execution should be put in.
     * @param instance the instance of the object that the method should be executed on.
     * @param name     the name of the method to execute.
     * @param args     the arguments for the method.
     * @return true if the execution was successful.
     */
    public boolean executeMethodOn(int queue, final Object instance, String name, final Object... args) {
        try {
            Method m = mMethodMap.get(instance.getClass().getName() + name);
            if (m == null) {
                //Method is not cached find the method...
                //TODO- handle case when hashmap of methods is to large.
                Class[] classes = null;
                if (args.length > 0) {
                    classes = new Class[args.length];
                    for (int i = 0; i < classes.length; i++) {
                        classes[i] = args[i].getClass();
                    }
                }
                m = instance.getClass().getMethod(name, classes);

                mMethodMap.put(instance.getClass().getName() + name, m);
            }
            final Method finalM = m;
            executeOn(queue, new Callable<Integer>() {
                @Override
                public Integer call() {
                    try {
                        finalM.invoke(instance, args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
