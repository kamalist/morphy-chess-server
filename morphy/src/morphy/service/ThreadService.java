package morphy.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import morphy.Morphy;
import morphy.properties.PreferenceKeys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadService implements Service {
	private static final Log LOG = LogFactory.getLog(ThreadService.class);
	public static final String THREAD_DUMP_FILE_PATH = Morphy.USER_DIRECTORY
			+ "/logs/threaddump_" + System.currentTimeMillis() + ".txt";

	private static final ThreadService instance = new ThreadService();

	protected static final class RunnableExceptionDecorator implements Runnable {
		protected Runnable runnable;

		public RunnableExceptionDecorator(Runnable runnable) {
			this.runnable = runnable;
		}

		public void run() {
			try {
				runnable.run();
			} catch (Throwable t) {
				Morphy.getInstance().onError(
						"Error in ThreadService Runnable.", t);
			}
		}

	}

	ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(200);

	protected boolean isDisposed = false;

	public static ThreadService getInstance() {
		return instance;
	}

	/**
	 * Dumps stack traces of all threads to threaddump.txt.
	 */
	public static void threadDump() {
		LOG
				.error("All threads are in use. Logging the thread stack trace to threaddump.txt and exiting.");
		final ThreadMXBean threads = ManagementFactory.getThreadMXBean();
		long[] threadIds = threads.getAllThreadIds();
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new FileWriter(THREAD_DUMP_FILE_PATH,
					false));
			printWriter.println("Morphy ThreadService initiated dump "
					+ new Date());
			for (long threadId : threadIds) {
				ThreadInfo threadInfo = threads.getThreadInfo(threadId, 10);
				printWriter.println("Thread " + threadInfo.getThreadName()
						+ " Block time:" + threadInfo.getBlockedTime()
						+ " Block count:" + threadInfo.getBlockedCount()
						+ " Lock name:" + threadInfo.getLockName()
						+ " Waited Count:" + threadInfo.getWaitedCount()
						+ " Waited Time:" + threadInfo.getWaitedTime()
						+ " Is Suspended:" + threadInfo.isSuspended());
				StackTraceElement[] stackTrace = threadInfo.getStackTrace();
				for (StackTraceElement element : stackTrace) {
					printWriter.println(element);
				}

			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (printWriter != null) {
				try {
					printWriter.flush();
					printWriter.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	private ThreadService() {
		executor.setCorePoolSize(PreferenceService.getInstance().getInt(
				PreferenceKeys.ThreadServiceCoreThreads));
		executor.setMaximumPoolSize(PreferenceService.getInstance().getInt(
				PreferenceKeys.ThreadServiceMaxThreads));
		executor.setKeepAliveTime(PreferenceService.getInstance().getInt(
				PreferenceKeys.ThreadServiceKeepAlive), TimeUnit.SECONDS);
		executor.prestartAllCoreThreads();
		if (LOG.isInfoEnabled()) {
			LOG.info("Initialized ThreadService");
		}
	}

	public void dispose() {
		executor.shutdownNow();
		isDisposed = true;
	}

	public ScheduledThreadPoolExecutor getExecutor() {
		return executor;
	}

	/**
	 * Executes a runnable asynch in a controlled way. Exceptions are monitored
	 * and displayed if they occur.
	 */
	public void run(Runnable runnable) {
		if (!Morphy.getInstance().isShutdown()) {
			try {
				executor.execute(new RunnableExceptionDecorator(runnable));
			} catch (RejectedExecutionException rej) {
				if (!Morphy.getInstance().isShutdown()) {
					LOG.error("Error executing runnable: ", rej);
					threadDump();
					Morphy.getInstance().onError(
							"ThreadServie has no more threads. A thread dump can be found at "
									+ THREAD_DUMP_FILE_PATH, rej);
				}
			}
		} else {
			LOG.info("Vetoing runnable in ThreadService, raptor is disposed. "
					+ runnable);
		}
	}

	/**
	 * Runs the runnable one time after a delay. Exceptions are monitored and
	 * displayed if they occur.
	 * 
	 * @param delay
	 *            Delay in milliseconds
	 * @param runnable
	 *            The runnable.
	 * @return The Future, may return null if there was an error scheduling the
	 *         Runnable or if execution was vetoed.
	 */
	@SuppressWarnings("unchecked")
	public Future scheduleOneShot(long delay, Runnable runnable) {
		if (!Morphy.getInstance().isShutdown()) {
			try {
				return executor.schedule(new RunnableExceptionDecorator(
						runnable), delay, TimeUnit.MILLISECONDS);
			} catch (RejectedExecutionException rej) {
				if (!Morphy.getInstance().isShutdown()) {
					LOG.error("Error executing runnable in scheduleOneShot: ",
							rej);
					threadDump();
					Morphy.getInstance().onError(
							"ThreadServie has no more threads. A thread dump can be found at "
									+ THREAD_DUMP_FILE_PATH);
				}
				return null;
			}
		} else {
			LOG.info("Veoting runnable " + runnable + " moprhy is shutdown.");
			return null;
		}
	}
}
