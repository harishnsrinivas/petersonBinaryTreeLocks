import java.util.concurrent.atomic.AtomicInteger;

class TreeNode {
    
	private boolean[] threadsCompeting;
	private AtomicInteger victim = new AtomicInteger();

	// Every node has these attributes
    TreeNode nodeParent;
    TreeNode childToLeft;
	TreeNode childToRight;

	public TreeNode(TreeNode node, int threadCount) {
	        threadsCompeting = new boolean[threadCount];
		nodeParent = node;
	}

    // Check if another thread already has the lock.
    private boolean isLockBusy(int thread_id) {
        for (int j = 0; j < threadsCompeting.length; j++) {
            if (threadsCompeting[j] && (j != thread_id))
                return true;
        }
        return false;
    }

    public void lock(int thread_id) {

        // Announce interest to acquire the lock

        threadsCompeting[thread_id] = true;
		victim.set(thread_id);

		/* Wait as long as there is another thread having the lock and
		    victim is the current thread trying to acquire the lock */

        while(isLockBusy(thread_id) && victim.get() == thread_id) {};
	}

	public void unlock(int thread_id) {
        // Release the lock by dropping out of contention
		threadsCompeting[thread_id] = false;
	}
}
