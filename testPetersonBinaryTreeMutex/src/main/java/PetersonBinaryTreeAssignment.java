import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.InputStream;
import java.util.Properties;

public class PetersonBinaryTreeAssignment implements Runnable {

    public static int attempts_to_cs;
	public static int trials;
    public static int num_of_threads=0;
    public int current_thread_id;
    public static volatile AtomicInteger critical_section_counter = new AtomicInteger(0);
	public static PetersonTree lock;

	public PetersonBinaryTreeAssignment(int thread_id) {
		current_thread_id = thread_id;
	}

	public void run() {
		int local_count = 0;
		while (local_count < attempts_to_cs) {
			lock.lock(current_thread_id);
			local_count++;
            // Increment the counter that keeps track of the number attempts to critical section in total
            critical_section_counter.incrementAndGet();
			lock.unlock(current_thread_id);
		}
	}

    // runThread Method throws exception if any thread is interrupted

    public static void runThreads() throws InterruptedException {
        // Store all the threads in an array
		Thread[] threads = new Thread[num_of_threads];

        // Loop through each thread in the list and start it
		for (int i = 0; i < num_of_threads; i++) {
			threads[i] = new Thread(new PetersonBinaryTreeAssignment(i));
			//System.out.print(threads);
			threads[i].start();
		}

        //  Wait for all threads to finish running
		for (int i=0; i<threads.length; i++) {
            threads[i].join();
		}

        // Display info related to how many times each thread attempted to enter critical section and total running duration
	}

	public static void main(String args[]) {

		// Get the global parameters from the configuration properties file
        Properties prop = new Properties();
        InputStream input = null;

        try {
            String filename = "config.properties";
            input = PetersonBinaryTreeAssignment.class.getResourceAsStream(filename);
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return;
            }
            prop.load(input);
        }catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if(input!=null){
                attempts_to_cs = Integer.parseInt(prop.getProperty("attempts_to_cs"));
                trials = Integer.parseInt(prop.getProperty("trials"));
                num_of_threads = Integer.parseInt(prop.getProperty("num_of_threads"));

                // Check if the number of threads is a power of two
                if ((num_of_threads & -num_of_threads) != num_of_threads) {
                    throw new IllegalArgumentException("Number of threads must be a power of 2");
                }

                lock = new PetersonTree(num_of_threads);
            }
        }

        long executionStartTime = System.nanoTime();
		for (int i = 0; i < trials; i++) {
            try {
                runThreads();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
		long executionEndtTime = System.nanoTime();

        // Display total execution time
        long totalExecutionTime = (executionEndtTime - executionStartTime)/1000000;
		System.out.println("Total execution time : " + totalExecutionTime + " milliseconds");
        System.out.println("Number of threads competing for critical section : " + num_of_threads);
        System.out.println("Number of times each thread attempts to run critical section : " + attempts_to_cs);
        System.out.println("Number of times access granted to critical section : " + critical_section_counter.get());
	}
}
