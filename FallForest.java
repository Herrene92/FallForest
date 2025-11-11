import java.util.Random;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * FallForest
 * Simulates an autumn forest where leaves fall recursively from branches,
 * and then regrow in the spring.
 */
class Branch {
    private int leaves;
    private Branch[] subBranches;
    private int windStrength;                // controls how many leaves can fall per step
    private static final Random rand = new Random();

    /**
     * Construct a branch with a certain number of leaves, sub-branches,
     * and a windStrength value.
     */
    public Branch(int leaves, int subCount, int windStrength) {
        this.leaves = leaves;
        this.windStrength = Math.max(1, windStrength);

        subBranches = new Branch[subCount];

        // Each sub-branch is a smaller version of the same pattern.
        for (int i = 0; i < subCount; i++) {
            int childLeaves = rand.nextInt(3) + 1;  // 1–3 leaves
            int childSubs = rand.nextInt(2);        // 0 or 1 sub-branch
            subBranches[i] = new Branch(childLeaves, childSubs, this.windStrength);
        }
    }

    /**
     * Recursively simulate leaves falling from this branch and all its sub-branches.
     * @param level indentation/visual depth (0 = trunk)
     */
    public void fallLeaves(int level, PrintWriter log) throws InterruptedException {
        // Base case: no leaves and no children = nothing left to do.
        if (leaves <= 0 && subBranches.length == 0) {
            return;
        }

        // If this branch still has leaves, let up to windStrength fall.
        if (leaves > 0) {
            int leavesToDrop = Math.min(leaves, windStrength);

            for (int i = 0; i < leavesToDrop; i++) {
                if (leaves == 0) break;

                leaves--;

                String message = " ".repeat(level * 2)
                        + " A leaf falls from branch level " + level
                        + " (remaining on this branch: " + leaves + ")";
                System.out.println(message);
                log.println(message);

                Thread.sleep(500); // gentle animation
            }

            // Recursive case: keep processing this branch until its leaves are gone.
            if (leaves > 0) {
                fallLeaves(level, log);
                return; // wait to process children until this branch is bare
            }
        }

        // Once this branch is bare, recurse into each sub-branch.
        for (Branch b : subBranches) {
            b.fallLeaves(level + 1, log);
        }
    }

    /**
     * Recursively regrow leaves on this branch and all sub-branches for spring.
     * @param level depth in the tree (for indentation)
     * @param maxLeavesPerBranch target leaves per branch
     */
    public void growLeaves(int level, int maxLeavesPerBranch, PrintWriter log) throws InterruptedException {
        // Grow this branch up to maxLeavesPerBranch.
        if (leaves < maxLeavesPerBranch) {
            leaves++;

            String message = " ".repeat(level * 2)
                    + "  A new leaf grows on branch level " + level
                    + " (total on this branch: " + leaves + ")";
            System.out.println(message);
            log.println(message);

            Thread.sleep(500);

            // Recursive case: continue growing this same branch.
            growLeaves(level, maxLeavesPerBranch, log);
            return;
        }

        // Once this branch is full, regrow all its children.
        for (Branch b : subBranches) {
            b.growLeaves(level + 1, maxLeavesPerBranch, log);
        }
    }
}

public class FallForest {
    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter("leaf_fall_log.txt"))) {

            String intro = " The forest prepares for autumn...";
            System.out.println(intro);
            log.println(intro);

            // Root tree setup (can tweak to experiment)
            int rootLeaves = 3;
            int rootSubBranches = 2;
            int windStrength = 2; // stronger wind -> more leaves per step

            Branch tree = new Branch(rootLeaves, rootSubBranches, windStrength);

            // AUTUMN: recursive leaf fall
            tree.fallLeaves(0, log);

            String springIntro = "\n Spring breezes return. The forest begins to regrow...";
            System.out.println(springIntro);
            log.println(springIntro);

            // SPRING: recursive regeneration
            tree.growLeaves(0, 3, log);

            String outro = "\n The cycle completes; the forest rests, ready for another year.";
            System.out.println(outro);
            log.println(outro);

        } catch (IOException e) {
            System.out.println("Error writing to leaf_fall_log.txt: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Animation interrupted: " + e.getMessage());
        }

    }
}
