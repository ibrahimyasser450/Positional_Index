import java.io.*;
import java.util.*;
class DictEntry {
    int doc_freq;// number of documents that contain the term
    int term_freq; //number of times the term is mentioned in the collection

    Posting pList;


    public DictEntry() {
        doc_freq = 0;
        term_freq = 0;
        pList = null;

    }
}