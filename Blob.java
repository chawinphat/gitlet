package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    /** variable. */
    private String _name;
    /** variable. */
    private String _hash;
    /** variable. */
    private String _wordContent;
    /** variable. */
    private byte[] _byteContent;

    public Blob(String name) {
        _name = name;
        try {
            File blobFile = new File(_name);
            _wordContent = Utils.readContentsAsString(blobFile);
            _byteContent = Utils.readContents(blobFile);
            _hash = makeHash();
        } catch (IllegalArgumentException expr) {
            System.out.println(_name);
            throw Utils.error("File Does Not Exist");
        }
    }

    private String makeHash() {
        String v = "blob";
        v += _name + _wordContent + _byteContent;
        return Utils.sha1(v);
    }

    public String getName() {
        return _name;
    }

    public String getHash() {
        return _hash;
    }

    public String getWordContent() {
        return _wordContent;
    }

    public byte[] getByteContent() {
        return _byteContent;
    }
}
