package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import java.util.Date;



public class Commit implements Serializable {

    /** intial empty commit method. */
    public Commit() {
        _parent = null;
        _merge = false;
        _mergeparent = "";
        _files = new HashMap<String, Blob>();
        _branch = "master";
        _time = "Thu Nov 9 20:00:05 2017 -0800";
        _message = "initial commit";
        _hash = makeHash();
    }

    /** regular commit.
     @param message **this is id of thing.
     @param files **this is id of thing.
     @param parent **this is id of thing.
     @param branch **this is id of thing.*/
    public Commit(String message, HashMap<String, Blob> files,
                  String parent, String branch) {
        _message = message;
        _files = files;
        _parent = parent;
        _mergeparent = "";
        _branch = branch;
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat(
                "E MMM dd HH:mm:ss yyyy Z");
        _time = format.format(d);
        _hash = makeHash();
    }
    /**
     * @param message **this is id of thing.**
     * @param files **this is id of thing.
     * @param parent **this is id of thing.
     * @param mergeParent **this is id of thing.
     * @param conflict **this is id of thing.
     * @param branch **this is id of thing.
     */
    public Commit(String message, HashMap<String, Blob> files, String parent,
                  String branch, String mergeParent, Boolean conflict) {
        _message = message;
        _files = files;
        _parent = parent;
        _mergeparent = mergeParent;
        _branch = branch;
        Date d = new Date();
        SimpleDateFormat format = new SimpleDateFormat(
                "E MMM dd HH:mm:ss yyyy Z");
        _time = format.format(d);
        _hash = makeHash();
        _conflict = conflict;
    }

    public Commit(Commit old) {
        _message = old.getMessage();
        _parent = old.getParent();
        _mergeparent = old.getParent();
        _files = old.getFiles();
        _branch = old.getBranch();
        _time = old.getTime();
    }

    private String makeHash() {
        String v = "commit";
        v += _time + _message + _branch + _mergeparent;

        if (_files.size() == 0) {
            return Utils.sha1(v);
        } else {
            for (Blob b: _files.values()) {
                v += b;
            }
        }

        String hash = Utils.sha1(v);
        return hash;
    }

    public boolean containsBlob(String file) {
        return _files.containsKey(file);
    }

    public String getMessage() {
        return _message;
    }

    public String getTime() {
        return _time;
    }

    public String getParent() {
        return _parent;
    }

    public String getParent1() {
        return _mergeparent;
    }

    public boolean getMerged() {
        return _conflict;
    }

    public void remove(String file) {
        _files.remove(file);
    }

    public String getHash() {
        return _hash;
    }

    public HashMap<String, Blob> getFiles() {
        return _files;
    }

    public String getBranch() {
        return _branch;
    }

    public Commit getHead() {
        return head;
    }

    /** hash. */
    private String _hash;

    /** commit message metadata. */
    private String _message;

    /** commit time metadata. */
    private String _time;

    /** commit branch. */
    private String _branch;

    /** commit merge branch. */
    private boolean _merge;

    /** commit merge parent. */
    private String _mergeparent;

    /** commit parent. */
    private String _parent;

    /** stores blobs inside Hashmap. */
    private HashMap<String, Blob> _files;

    /**pointer to head of branch. */
    private static Commit head;

    /** conflict. */
    private boolean _conflict;

}


