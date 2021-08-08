package com.study.kafka.threadFuture.consumerFuture;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 利用CountDownLatch实现future机制
 * @param <T>
 */
public class MyRequestFuture<T>  {

    private static final Object INCOMPLETE_SENTINEL = new Object();
    private final AtomicReference<Object> result = new AtomicReference<>(INCOMPLETE_SENTINEL);
    private final CountDownLatch completedLatch = new CountDownLatch(1);


    public boolean isDone() {
        return result.get() != INCOMPLETE_SENTINEL;
    }


    public boolean awaitDone() throws InterruptedException {
        return completedLatch.await(Integer.MAX_VALUE,TimeUnit.SECONDS);
    }

    public boolean awaitDone(long timeout, TimeUnit unit) throws InterruptedException {
        return completedLatch.await(timeout, unit);
    }


    /**
     * Check if the request failed.
     * @return true if the request completed with a failure
     */
    public boolean failed() {
        return result.get() instanceof RuntimeException;
    }


    /**
     * Get the value corresponding to this request (only available if the request succeeded)
     * @return the value set in {@link #complete(Object)}
     * @throws IllegalStateException if the future is not complete or failed
     */
    @SuppressWarnings("unchecked")
    public T value() {
        if (!succeeded())
            throw new IllegalStateException("Attempt to retrieve value from future which hasn't successfully completed");
        return (T) result.get();
    }

    /**
     * Check if the request succeeded;
     * @return true if the request completed and was successful
     */
    public boolean succeeded() {
        return isDone() && !failed();
    }

    /**
     * Get the exception from a failed result (only available if the request failed)
     * @return the exception set in {@link #raise(RuntimeException)}
     * @throws IllegalStateException if the future is not complete or completed successfully
     */
    public RuntimeException exception() {
        if (!failed())
            throw new IllegalStateException("Attempt to retrieve exception from future which hasn't failed");
        return (RuntimeException) result.get();
    }

    /**
     * Check if the request is retriable (convenience method for checking if
     * the exception is an instance of {@link RetriableException}.
     * @return true if it is retriable, false otherwise
     * @throws IllegalStateException if the future is not complete or completed successfully
     */
    /*public boolean isRetriable() {
        return exception() instanceof RetriableException;
    }*/

    /**
     * Complete the request successfully. After this call, {@link #succeeded()} will return true
     * and the value can be obtained through {@link #value()}.
     * @param value corresponding value (or null if there is none)
     * @throws IllegalStateException if the future has already been completed
     * @throws IllegalArgumentException if the argument is an instance of {@link RuntimeException}
     */
    public void complete(T value) {
        try {
            if (value instanceof RuntimeException)
                throw new IllegalArgumentException("The argument to complete can not be an instance of RuntimeException");

            if (!result.compareAndSet(INCOMPLETE_SENTINEL, value))
                throw new IllegalStateException("Invalid attempt to complete a request future which is already complete");

        } finally {
            completedLatch.countDown();
        }
    }


    /**
     * Raise an exception. The request will be marked as failed, and the caller can either
     * handle the exception or throw it.
     * @param e corresponding exception to be passed to caller
     * @throws IllegalStateException if the future has already been completed
     */
    public void raise(RuntimeException e) {
        try {
            if (e == null)
                throw new IllegalArgumentException("The exception passed to raise must not be null");

            if (!result.compareAndSet(INCOMPLETE_SENTINEL, e))
                throw new IllegalStateException("Invalid attempt to complete a request future which is already complete");

        } finally {
            completedLatch.countDown();
        }
    }

    /**
     * Raise an error. The request will be marked as failed.
     * @param error corresponding error to be passed to caller
     */
  /*  public void raise(Errors error) {
        raise(error.exception());
    }
*/

    public static <T> MyRequestFuture<T> failure(RuntimeException e) {
        MyRequestFuture<T> future = new MyRequestFuture<>();
        future.raise(e);
        return future;
    }

    public static MyRequestFuture<Void> voidSuccess() {
        MyRequestFuture<Void> future = new MyRequestFuture<>();
        future.complete(null);
        return future;
    }

   /* public static <T> MyRequestFuture<T> coordinatorNotAvailable() {
        return failure(Errors.COORDINATOR_NOT_AVAILABLE.exception());
    }*/

}
