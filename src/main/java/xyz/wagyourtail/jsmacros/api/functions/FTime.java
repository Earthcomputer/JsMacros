package xyz.wagyourtail.jsmacros.api.functions;


import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

/**
 * 
 * Functions for getting and using raw java classes, methods and functions.
 * 
 * An instance of this class is passed to scripts as the {@code time} variable.
 * 
 * @author Wagyourtail
 */
 @Library("time")
public class FTime implements BaseLibrary {
    
    /**
     * @return current time in MS.
     */
    public long time() {
        return System.currentTimeMillis();
    }
    
    /**
     * Sleeps the current thread for the specified time in MS.
     * 
     * @param millis
     * @throws InterruptedException
     */
    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
