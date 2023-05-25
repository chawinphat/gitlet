package gitlet;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Chawinphat Tankuranand
 */
public class Main {
    /** variable. */
    private static String currentBranch;
    /** variable. */
    private static String initialized;
    /** variable. */
    private static ArrayList<Commit> allCommits;
    /** variable. */
    private static HashMap<String, Commit> branches;
    /** variable. */
    private static HashMap<String, Blob> stage;
    /** variable. */
    private static HashMap<String, Blob> rStage;
    /** variable. */
    private static HashMap<String, Blob> tracked;
    /** variable. */
    private static HashMap<String, Blob> untracked;
    /** variable. */
    private static HashMap<String, Commit> commits;
    /** variable cwd. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    /** pointer to previous commit. */
    private static String _head;
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> ....
     *  */
    @SuppressWarnings("unchecked")
    public static void main(String... args) {
        stage = new HashMap<String, Blob>();
        rStage = new HashMap<String, Blob>();
        tracked = new HashMap<String, Blob>();
        untracked = new HashMap<String, Blob>();
        commits = new HashMap<>();
        _head = null;
        allCommits = new ArrayList<>();
        currentBranch = null;
        branches = new HashMap<String, Commit>();

        File initializedLoc = Utils.join(CWD, ".gitlet/initialized");
        if (initializedLoc.exists()) {
            initialized = Utils.readContentsAsString(initializedLoc);
        } else {
            initialized = "false";
        }

        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }

        if (args[0].equals("init")) {
            if (args.length == 1) {
                init();
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else {
            if (initialized.equals("false")) {
                System.out.println("Not in an initialized Gitlet directory.");
                return;
            }

            File commitsLoc = Utils.join(CWD, ".gitlet/commits");
            commits = Utils.readObject(commitsLoc, HashMap.class);
            File headFile = Utils.join(CWD, ".gitlet/head");
            _head = Utils.readContentsAsString(headFile);
            File stageFile = Utils.join(CWD, ".gitlet/stage");
            stage = Utils.readObject(stageFile, HashMap.class);
            File rStageFile = Utils.join(CWD, ".gitlet/rStage");
            rStage = Utils.readObject(rStageFile, HashMap.class);
            File branchFile = Utils.join(CWD, ".gitlet/branches");
            branches = Utils.readObject(branchFile, HashMap.class);
            File currBranchFile = Utils.join(CWD, ".gitlet/currentBranch");
            currentBranch = Utils.readContentsAsString(currBranchFile);
            File trackedFile = Utils.join(CWD, ".gitlet/tracked");
            tracked = Utils.readObject(trackedFile, HashMap.class);
            File untrackedFile = Utils.join(CWD, ".gitlet/untracked");
            untracked = Utils.readObject(untrackedFile, HashMap.class);
            File allCommitLoc = Utils.join(CWD, ".gitlet/allCommits");
            allCommits = Utils.readObject(allCommitLoc, ArrayList.class);
            parse(args);
            setup2();
        }
    }

    public static void parse(String[] args) {
        if (args[0].equals("add")) {
            if (args.length == 2) {
                add(args[1]);
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("commit")) {
            if (args.length == 2) {
                commit(args[1], false, "", false);
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("log")) {
            if (args.length == 1) {
                log();
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("checkout")) {
            if (args.length == 3) {
                if (args[1].equals("--")) {
                    checkout1(args[2]);
                } else {
                    System.out.println("Incorrect Operands.");
                }
            } else if (args.length == 4) {
                if (args[2].equals("--")) {
                    checkout2(args[1], args[3]);
                } else {
                    System.out.println("Incorrect Operands.");
                }
            } else if (args.length == 2) {
                checkout3(args[1]);
            } else {
                System.out.println("Incorrect Operands.");
            }
        } else if (args[0].equals("rm")) {
            if (args.length == 2) {
                rm(args[1]);
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("branch")) {
            if (args.length == 2) {
                branch(args[1]);
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("status")) {
            if (args.length == 1) {
                status();
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else {
            parse2(args);
        }
    }

    public static void parse2(String[] args) {
        if (args[0].equals("global-log")) {
            if (args.length == 1) {
                globalLog();
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("find")) {
            if (args.length == 2) {
                find(args[1]);
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("rm-branch")) {
            if (args.length == 2) {
                rmBranch(args[1]);
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("reset")) {
            if (args.length == 2) {
                reset(args[1]);
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else if (args[0].equals("merge")) {
            if (args.length == 2) {
                merge(args[1]);
            } else {
                throw Utils.error("Incorrect Operands.");
            }
        } else {
            System.out.println("No command with that name exists.");
            return;
        }
    }

    public static void setup2() {
        File commitsLoc = Utils.join(CWD, ".gitlet/commits");
        File headFile = Utils.join(CWD, ".gitlet/head");
        File stageFile = Utils.join(CWD, ".gitlet/stage");
        File rStageFile = Utils.join(CWD, ".gitlet/rStage");
        File branchFile = Utils.join(CWD, ".gitlet/branches");
        File currBranchFile = Utils.join(CWD, ".gitlet/currentBranch");
        File trackedFile = Utils.join(CWD, ".gitlet/tracked");
        File untrackedFile = Utils.join(CWD, ".gitlet/untracked");
        File allCommitLoc = Utils.join(CWD, ".gitlet/allCommits");
        File initializedLoc = Utils.join(CWD, ".gitlet/initialized");

        Utils.writeObject(commitsLoc, commits);
        Utils.writeContents(headFile, _head);
        Utils.writeObject(stageFile, stage);
        Utils.writeObject(rStageFile, rStage);
        Utils.writeContents(initializedLoc, initialized);
        Utils.writeContents(currBranchFile, currentBranch);
        Utils.writeObject(branchFile, branches);
        Utils.writeObject(trackedFile, tracked);
        Utils.writeObject(untrackedFile, untracked);
        Utils.writeObject(allCommitLoc, allCommits);
    }


    /**
     * Init creates gitlet repository if one doesn't exist.
     * then creates a new commit to put in it
     */
    public static void init() {
        if (initialized.equals("true")) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
        } else {
            initialized = "false";
            new File(".gitlet").mkdirs();
            Commit commit0 = new Commit();
            String hash = commit0.getHash();
            allCommits.add(commit0);
            branches.put("master", commit0);
            currentBranch = "master";
            _head = hash;
            commits.put(hash, commit0);
            File commitsLoc = Utils.join(CWD, ".gitlet/commits");
            Utils.writeObject(commitsLoc, commits);
            File headFile = Utils.join(CWD, ".gitlet/head");
            Utils.writeContents(headFile, _head);
            File stageFile = Utils.join(CWD, ".gitlet/stage");
            Utils.writeObject(stageFile, stage);
            File rStageFile = Utils.join(CWD, ".gitlet/rStage");
            Utils.writeObject(rStageFile, rStage);
            File initializeFile = Utils.join(CWD, ".gitlet/initialized");
            Utils.writeContents(initializeFile, "true");
            File currentBranchLoc = Utils.join(CWD, ".gitlet/currentBranch");
            Utils.writeContents(currentBranchLoc, currentBranch);
            File branchesLoc = Utils.join(CWD, ".gitlet/branches");
            Utils.writeObject(branchesLoc, branches);
            File trackedFile = Utils.join(CWD, ".gitlet/tracked");
            Utils.writeObject(trackedFile, tracked);
            File untrackedFile = Utils.join(CWD, ".gitlet/untracked");
            Utils.writeObject(untrackedFile, untracked);
            File allCommitLoc = Utils.join(CWD, ".gitlet/allCommits");
            Utils.writeObject(allCommitLoc, allCommits);
        }
    }

    public static void add(String file) {
        File addFile = Utils.join(CWD, file);

        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if (rStage.containsKey(file)) {
            rStage.remove(file);
        }

        if (addFile.exists()) {
            Blob blob = new Blob(file);

            tracked.put(file, blob);
            if (untracked.containsKey(blob.getHash())) {
                untracked.remove(blob.getHash());
            }
            Commit prevCommit = commits.get(_head);
            if (stage.containsKey(blob.getName())) {
                stage.remove(blob.getName());
                stage.put(blob.getName(), blob);
            } else {
                stage.put(blob.getName(), blob);
            }
            if (prevCommit.containsBlob(blob.getName())) {
                if (prevCommit.getFiles().get(blob.getName()).
                        getHash().equals(blob.getHash())) {
                    stage.remove(blob.getName());
                }
            }
        } else {
            throw Utils.error("File does not exist");
        }

        return;

    }

    public static void commit(String message, boolean merge,
                              String mergeParent, boolean conflict) {
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        }

        if (stage.isEmpty() && rStage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        Commit parent = branches.get(currentBranch);
        HashMap<String, Blob> stageClone = parent.getFiles();
        stageClone.putAll(stage);
        for (String s : rStage.keySet()) {
            stageClone.remove(s);
        }

        stage = new HashMap<String, Blob>();
        rStage = new HashMap<String, Blob>();

        Commit newCommit;
        if (!merge) {
            newCommit = new Commit(message, stageClone, _head, currentBranch);
        } else {
            newCommit = new Commit(message, stageClone, _head,
                    currentBranch, mergeParent, conflict);
        }


        commits.put(newCommit.getHash(), newCommit);
        allCommits.add(newCommit);
        _head = newCommit.getHash();
        branches.put(currentBranch, newCommit);

        for (Blob b : stage.values()) {
            if (!tracked.containsKey(b.getName())) {
                tracked.put(b.getName(), b);
            }
        }
        return;
    }

    public static void log() {
        Commit current = branches.get(currentBranch);
        while (current != null) {
            System.out.println("===");
            System.out.println("commit " + current.getHash());

            if (current.getMerged()) {
                String first6 = commits.get(current.getParent())
                        .getHash().substring(0, 7);
                String second6 =  commits.get(current.getParent1())
                        .getHash().substring(0, 7);
                System.out.println("Merge: " + first6 + " " + second6);
            }

            System.out.println("Date: " + current.getTime());
            System.out.println(current.getMessage());
            System.out.println();
            current = commits.get(current.getParent());
        }
    }
    /** @param file file  uWu. */
    public static void checkout1(String file) {
        Commit receiving = commits.get(_head);
        HashMap<String, Blob> files = receiving.getFiles();
        if (files.containsKey(file)) {
            Blob b = files.get(file);
            Utils.writeContents(Utils.join(CWD, b.getName()),
                    b.getWordContent());
        } else {
            System.out.println("File does not exist in that commit.");
        }

    }

    /** @param file
     * @param id  */
    public static void checkout2(String id, String file) {
        for (String i : commits.keySet()) {
            if (i.contains(id)) {
                id = i;
            }
        }
        if (commits.containsKey(id)) {
            Commit receiving = commits.get(id);
            HashMap<String, Blob> files = receiving.getFiles();
            if (files.containsKey(file)) {
                Blob b = files.get(file);
                Utils.writeContents(Utils.join(CWD, b.getName()),
                        b.getWordContent());
            } else {
                System.out.println("File does not exist in that commit.");
            }
        } else {
            System.out.println("No commit with that id exists.");
        }
    }


    /** @param branch  */
    public static void checkout3(String branch) {
        if (!stage.isEmpty()) {
            System.out.println("There is an untracked file "
                    + "in the way; delete it, or add and commit it first.");
            return;
        }

        if (!branches.containsKey(branch)) {
            System.out.println("No such branch exists.");
            return;
        }

        if (currentBranch.equals(branch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        Commit receiving = branches.get(branch);
        HashMap<String, Blob> files = receiving.getFiles();

        Commit current = branches.get(currentBranch);
        HashMap<String, Blob> files2 = current.getFiles();

        for (Blob b : files2.values()) {
            if (!files.containsKey(b)) {
                File deleteBlob = Utils.join(CWD, b.getName());
                Utils.restrictedDelete(deleteBlob);
            }
        }

        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String s : cwdFiles) {
            if (!files2.containsKey(s) && files.containsKey(s)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                return;
            }
        }

        for (Blob b : files.values()) {
            File location = Utils.join(CWD, b.getName());
            Utils.writeContents(location, b.getWordContent());
        }

        if (branch != currentBranch) {
            stage = new HashMap<String, Blob>();
        }
        _head = receiving.getHash();
        currentBranch = branch;
    }

    public static void rm(String file) {
        Commit headCommit = branches.get(currentBranch);

        if (!(stage.containsKey(file)) && !(headCommit.containsBlob(file))) {
            System.out.println("No reason to remove the file.");
            return;
        }

        if (stage.containsKey(file) || headCommit.containsBlob(file)) {
            if (stage.containsKey(file)) {
                stage.remove(file);
            }
            if (headCommit.containsBlob(file)) {
                Blob removeBlob = headCommit.getFiles().get(file);
                rStage.put(file, removeBlob);

                File blobLoc = Utils.join(CWD, removeBlob.getName());
                if (blobLoc.exists()) {
                    Utils.restrictedDelete(blobLoc);
                }
            }
        }
    }

    public static void branch(String name) {
        if (branches.containsKey(name)) {
            System.out.println("A branch with that name already exists.");
        } else {
            Commit headCopy = commits.get(_head);
            branches.put(name, headCopy);
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        ArrayList<String> sorted = new ArrayList<>(branches.keySet());
        Collections.sort(sorted);
        for (String branch : sorted) {
            if (branch.equals(currentBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        ArrayList<String> sorted2 = new ArrayList<>(stage.keySet());
        Collections.sort(sorted2);
        for (String file : sorted2) {
            System.out.println(file);
        }

        System.out.println();
        System.out.println("=== Removed Files ===");
        ArrayList<String> sorted3 = new ArrayList<>(rStage.keySet());
        Collections.sort(sorted3);
        for (String file : sorted3) {
            System.out.println(file);
        }

        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");

        System.out.println();
        System.out.println("=== Untracked Files ===");
    }

    public static void globalLog() {
        for (Commit current : allCommits) {
            System.out.println("===");
            System.out.println("commit " + current.getHash());
            System.out.println("Date: " + current.getTime());
            System.out.println(current.getMessage());
            System.out.println();
        }
    }

    public static void find(String message) {
        boolean found = false;
        for (Commit c : allCommits) {
            if (c.getMessage().equals(message)) {
                System.out.println(c.getHash());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void rmBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
        } else if ((branches.get(branchName).getHash()).equals(_head)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            branches.remove(branchName);
        }
    }

    public static void reset(String commidID) {
        if (commits.containsKey(commidID)) {
            Commit commit = commits.get(commidID);
            ArrayList<Blob> files = new ArrayList<Blob>(commit.getFiles().
                    values());

            Commit current = branches.get(currentBranch);
            HashMap<String, Blob> files2 = current.getFiles();
            for (Blob b : files2.values()) {
                if (!files.contains(b)) {
                    File deleteBlob = Utils.join(CWD, b.getName());
                    Utils.restrictedDelete(deleteBlob);
                }
            }

            HashMap<String, Blob> files3 = commit.getFiles();
            List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
            for (String s : cwdFiles) {
                if (!files2.containsKey(s) && files3.containsKey(s)
                        && stage.isEmpty() && rStage.isEmpty()) {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                    return;
                }
            }

            for (Blob b : files) {
                Utils.writeContents(Utils.join(CWD, b.getName()),
                        b.getWordContent());
            }

            _head = commit.getHash();
            branches.remove(currentBranch);
            branches.put(currentBranch, commit);
            stage = new HashMap<>();
        } else {
            System.out.println("No commit with that id exists.");
        }
    }

    public static void merge(String mergeBranch) {
        if (mergeE1(mergeBranch)) {
            return;
        }
        Commit head = branches.get(currentBranch);
        Commit given = branches.get(mergeBranch);
        ArrayList<Commit> branchA = splitPoint(head);
        ArrayList<Commit> branchB = splitPoint(given);
        Commit splitPoint = null;
        boolean found = false;
        for (Commit com : branchA) {
            for (Commit com2 : branchB) {
                if (com.getMessage().equals(com2.getMessage())) {
                    splitPoint = com;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        if (mergeE2(splitPoint, given, head, mergeBranch)) {
            return;
        }
        HashMap<String, Blob> givenMP = given.getFiles();
        HashMap<String, Blob> splitMP = splitPoint.getFiles();
        HashMap<String, Blob> headMP = head.getFiles();
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD);
        for (String s : cwdFiles) {
            if (!headMP.containsKey(s) && (splitMP.containsKey(s)
                    || givenMP.containsKey(s))) {
                mergePrint2();
                return;
            }
        }
        Boolean conflict = false;
        HashMap<String, Blob> givenCopy = new
                HashMap<String, Blob>(given.getFiles());
        givenCopy.keySet().removeAll(head.getFiles().keySet());
        givenCopy.keySet().removeAll(splitPoint.getFiles().keySet());
        for (String s : givenCopy.keySet()) {
            Blob temp = givenCopy.get(s);
            File location = Utils.join(CWD, temp.getName());
            Utils.writeContents(location, temp.getWordContent());
            stage.put(temp.getName(), temp);
        }
        conflict = mergeConditionals2(mergeBranch, splitPoint,
                head, given, conflict);
        conflict = mergeConditionals(mergeBranch, splitPoint,
                head, given, conflict);
        String message = "Merged " + given.getBranch() + " into "
                + currentBranch + ".";
        if (!stage.isEmpty() || !rStage.isEmpty()) {
            commit(message, true, given.getHash(), true);
        }
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static void mergePrint2() {
        System.out.println("There is an untracked file in the way;"
                + " delete it, or add and commit it first.");
    }



    public static Boolean mergeE1(String mergeBranch) {
        if (mergeBranch.equals(currentBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        }

        if (!branches.containsKey(mergeBranch)) {
            System.out.println("A branch with that name does not exist.");
            return true;
        }

        if (!stage.isEmpty() || !rStage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return true;
        }
        return false;
    }

    public static boolean mergeE2(Commit splitPoint,
            Commit given, Commit head, String mergeBranch) {
        if (splitPoint.getMessage().equals(given.getMessage())) {
            System.out.println("Given branch is an ancestor of "
                    + "the current branch.");
            return true;
        } else if (splitPoint.getMessage().equals(head.getMessage())) {
            checkout3(mergeBranch);
            System.out.println("Current branch fast-forwarded.");
            return true;
        }
        return false;
    }

    public static String mergePrint(Commit head, Commit given, String s) {
        String contents = "<<<<<<< HEAD\n"
                + head.getFiles().get(s).getWordContent()
                + "=======\n" + given.getFiles().get(s).getWordContent()
                + ">>>>>>>\n";
        return contents;
    }

    public static Boolean mergeConditionals2(String mergeBranch,
            Commit splitPoint, Commit head, Commit given, Boolean conflict) {
        HashMap<String, Blob> givenCopy2 = new
                HashMap<String, Blob>(given.getFiles());
        givenCopy2.keySet().removeAll(splitPoint.getFiles().keySet());
        for (String s : givenCopy2.keySet()) {
            if (given.getFiles().containsKey(s)
                    && head.getFiles().containsKey(s)) {
                conflict = true;
                File location = Utils.join(CWD, s);
                String contents = mergePrint(head, given, s);
                Utils.writeContents(location, contents);
                Blob add = new Blob(s);
                stage.put(s, add);
            }
        }
        return conflict;
    }

    public static Boolean mergeConditionals(String mergeBranch,
             Commit splitPoint, Commit head, Commit given, Boolean conflict) {
        for (String s : splitPoint.getFiles().keySet()) {
            Blob splitB = null;
            Blob headB = null;
            Blob givenB = null;
            String splitS = "fuck";
            String headS = "fuck1";
            String givenS = "fuck2";
            if (splitPoint.getFiles().containsKey(s)) {
                splitB = splitPoint.getFiles().get(s);
                splitS = splitB.getWordContent();
            }
            if (head.getFiles().containsKey(s)) {
                headB = head.getFiles().get(s);
                headS = headB.getWordContent();
            }
            if (given.getFiles().containsKey(s)) {
                givenB = given.getFiles().get(s);
                givenS = givenB.getWordContent();
            }
            if (head.getFiles().containsKey(s)
                    && given.getFiles().containsKey(s)) {
                if (headS.equals(splitS) || givenS.equals(splitS)) {
                    if (headS.equals(splitS) && !givenS.equals(splitS)) {
                        File location = Utils.join(CWD, givenB.getName());
                        Utils.writeContents(location, givenS);
                        stage.put(givenB.getName(), givenB);
                    }
                } else {
                    conflict = true;
                    File location = Utils.join(CWD, s);
                    String contents = mergePrint(head, given, s);
                    Utils.writeContents(location, contents);
                    Blob add = new Blob(s);
                    stage.put(s, add);
                }
            } else if (!given.getFiles().containsKey(s)
                    && head.containsBlob(s)) {
                if (headS.equals(splitS)) {
                    Utils.restrictedDelete(Utils.join(CWD, headB.getName()));
                    rStage.put(headB.getName(), headB);
                } else if (!head.getMessage().equals(splitPoint.getMessage())) {
                    conflict = true;
                    File location = Utils.join(CWD, s);
                    String contents = "<<<<<<< HEAD\n" + headS
                            + "=======\n" + "" + ">>>>>>>\n";
                    Utils.writeContents(location, contents);
                    Blob add = new Blob(s);
                    stage.put(s, add);
                }
            } else if (given.containsBlob(s) && !head.containsBlob(s)) {
                if (!givenS.equals(splitS)) {
                    conflict = true;
                    mergelastcon(s, givenS);

                }
            }
        }
        return conflict;
    }

    public static void mergelastcon(String s, String givenS) {
        File location = Utils.join(CWD, s);
        String contents = "<<<<<<< HEAD\n" + ""
                + "=======\n" + givenS + ">>>>>>>\n";
        Utils.writeContents(location, contents);
        Blob add = new Blob(s);
        stage.put(s, add);
    }

    public static ArrayList<Commit> splitPoint(Commit commit) {
        ArrayDeque<Commit> work = new ArrayDeque<>();
        ArrayList<Commit> returning = new ArrayList<>();
        work.add(commit);
        while (!work.isEmpty()) {
            Commit node = work.remove();
            Commit newNode = commits.get(node.getParent());
            Commit newNode2 = commits.get(node.getParent1());
            if (newNode != null || newNode2 != null) {
                if (newNode != null) {
                    work.push(newNode);
                }
                if (newNode2 != null) {
                    work.push(newNode2);
                }
            }
            returning.add(node);
        }
        return returning;
    }
}


