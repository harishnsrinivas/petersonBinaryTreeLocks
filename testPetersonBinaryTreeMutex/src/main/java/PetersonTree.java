import java.util.ArrayList;
import java.util.List;

public class PetersonTree {
	int num_of_threads;
	public TreeNode root;
	public List<TreeNode> leafNodes;

	public PetersonTree(int threads) {

        num_of_threads = threads;

        // Create the root node with parent NULL
		root = new TreeNode(null, num_of_threads);

		// Create an initial empty array of peterson nodes that would later form a binary tree
        List<TreeNode> currentTreeNodes = new ArrayList<>();

        // Put the root node into the list
        currentTreeNodes.add(root);

		leafNodes = ExpandLockTree(currentTreeNodes);
	}

    private List<TreeNode> ExpandLockTree(List<TreeNode> nodes) {


        // Stop expanding if all leaf nodes have been built
        if (nodes.size() == num_of_threads / 2)
            return nodes;

        // Keep an array to store the children to expand in next iteration
        List<TreeNode> currentLeaves = new ArrayList<>();

        // Loop through each node, add two children and store them as
        // the current leaves
        for (TreeNode node : new ArrayList<TreeNode>(nodes)) {
            node.childToLeft = new TreeNode(node, num_of_threads);
            node.childToRight = new TreeNode(node, num_of_threads);

            currentLeaves.add(node.childToLeft);
            currentLeaves.add(node.childToRight);
        }

        // Recurse, passing our current leaves back to the function
        return ExpandLockTree(currentLeaves);
    }

	// Try acquiring all the locks from the leaf to the root node of the tree.
	public void lock(int thread_id) {

        /* Get the leaf node for the thread trying to acquire
            the lock for the first time or to re-nter critical section */
        TreeNode currentNode = getCorrespondingLeafLock(thread_id);

		while (currentNode != null) {
			currentNode.lock(thread_id);
			currentNode = currentNode.nodeParent;
		}
	}

	public void unlock(int thread_id) {
		TreeNode currentNode = getCorrespondingLeafLock(thread_id);

		while (currentNode != null) {
			currentNode.unlock(thread_id);
			currentNode = currentNode.nodeParent;
		}
	}

	private TreeNode getCorrespondingLeafLock(int thread_id) {
        // Assign every 2 threads to a leaf node.
        // The quotient will give us the leaf node id to assign to.
		return leafNodes.get(thread_id / 2);
	}
}
