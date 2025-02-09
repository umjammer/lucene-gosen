package net.java.sen.util;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;


/**
 * a subset of org.apache.lucene.util.IOUtils.
 */
public class IOUtils {
    /** This reflected {@link Method} is {@code null} before Java 7 */
    private static final Method SUPPRESS_METHOD;

    static {
        Method m;
        try {
            m = Throwable.class.getMethod("addSuppressed", Throwable.class);
        } catch (Exception e) {
            m = null;
        }
        SUPPRESS_METHOD = m;
    }

    /**
     * Closes all given <tt>Closeable</tt>s.  Some of the
     * <tt>Closeable</tt>s may be null; they are
     * ignored.  After everything is closed, the method either
     * throws the first exception it hit while closing, or
     * completes normally if there were no exceptions.
     *
     * @param objects objects to call <tt>close()</tt> on
     */
    public static void close(Closeable... objects) throws IOException {
        Throwable th = null;

        for (Closeable object : objects) {
            try {
                if (object != null) {
                    object.close();
                }
            } catch (Throwable t) {
                addSuppressed(th, t);
                if (th == null) {
                    th = t;
                }
            }
        }

        if (th != null) {
            if (th instanceof IOException) throw (IOException) th;
            if (th instanceof RuntimeException) throw (RuntimeException) th;
            if (th instanceof Error) throw (Error) th;
            throw new RuntimeException(th);
        }
    }

    /**
     * Closes all given <tt>Closeable</tt>s, suppressing all thrown exceptions.
     * Some of the <tt>Closeable</tt>s may be null, they are ignored.
     *
     * @param objects objects to call <tt>close()</tt> on
     */
    public static void closeWhileHandlingException(Closeable... objects) throws IOException {
        for (Closeable object : objects) {
            try {
                if (object != null) {
                    object.close();
                }
            } catch (Throwable t) {
            }
        }
    }

    /**
     * adds a Throwable to the list of suppressed Exceptions to the first Throwable (if Java 7 is detected)
     *
     * @param exception  this exception should get the suppressed one added
     * @param suppressed the suppressed exception
     */
    private static void addSuppressed(Throwable exception, Throwable suppressed) {
        if (SUPPRESS_METHOD != null && exception != null && suppressed != null) {
            try {
                SUPPRESS_METHOD.invoke(exception, suppressed);
            } catch (Exception e) {
                // ignore any exceptions caused by invoking (e.g. security constraints)
            }
        }
    }

}
