import java.io.*;
import java.sql.SQLOutput;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.jsoup.Jsoup;
public class PositionalIndex
{

    private final HashSet<String> links = new HashSet<>();
    private HashMap<String, DictEntry> index;
    private HashMap<Integer, Float>Sim;
    private int docCount;
    private int[] Nt=new int[11]; // number of terms

    private  float[] Score=new float[11];


    public PositionalIndex() {
        index = new HashMap<String, DictEntry>();
        docCount = 1; // to begin from document 1 
        Sim = new HashMap<Integer, Float>();

    }

    public void buildIndex(String fileName) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = in.readLine()) != null) { //the line empty or contains words
            StringTokenizer st = new StringTokenizer(line); // take the words at the line  
            int position = -1; // to begin from index 0 
                while (st.hasMoreTokens()) {  // true while contains woeds
                    String token = st.nextToken(); // take the word 
                    position++;   
                    if (!index.containsKey(token)) { // if the index(hashmap) contains the word 
                        index.put(token, new DictEntry()); // if not => puts the word 
                    }
                    DictEntry entry = index.get(token);
                    if (entry.pList != null && entry.pList.docId == docCount) {
                        entry.pList.dtf++;
                        entry.pList.positions.add(position); // add the postion at the  ArrayList(positios)

                    } else {
                        Posting p = new Posting(docCount);
                        p.positions.add(position);

                        p.next = entry.pList;
                        entry.pList = p;
                        entry.doc_freq++;
                    }
                    entry.term_freq++;
                    Nt[docCount-1]++;
                }
            }
            docCount++;
        } catch (IOException e) {
            System.out.println("Error reading file " + fileName);
        }
    }
     // get guery and split it ( set of a number of words) 
        public void getquery(String query) {
        String[] words = query.split("\\W+")   ;
        for (String word : words) {
            StringTokenizer st = new StringTokenizer(word);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                if (!index.containsKey(token)) {
                    index.put(token, new DictEntry());
                }
                DictEntry entry = index.get(token);
                if (entry.pList != null && entry.pList.docId == docCount) {
                    entry.pList.dtf++;
                } else {
                    Posting p = new Posting(docCount);
                    p.next = entry.pList;
                    entry.pList = p;
                    entry.doc_freq++;
                }
                entry.term_freq++;
                Nt[10]++;

            }
        }
        docCount++;
    }
    // Calculate the cosine similarity between each file and the query
    public void cossim(String query){
        int nQ=0;
        String[] words = query.split("\\W+")   ;
        for (String word : words) {
            StringTokenizer st = new StringTokenizer(word);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                DictEntry entry = index.get(token);
                if (entry == null) {
                    System.out.println("No documents contain the word " + query);}
                else{
                Posting p = entry.pList; // posting list of documents contains this word 
                while (p != null  ) {
                    if(p.docId==11){
                        nQ=  p.dtf;
                        p=p.next;
                        continue;
                    }
                        Score[p.docId - 1] += (p.dtf*nQ)/Math.sqrt(Nt[p.docId - 1] * Nt[10]) ; 
                        p = p.next;
                }
                }

            }
        }
    }
    // rank the 10 files according to the value of the cosin similarity and print them
    public void print() {

        for (int i = 0; i <10; i++) {
            Sim.put((i+1),Score[i]);}

            List<Map.Entry<Integer,Float>> entries = new ArrayList<>(Sim.entrySet());
            // takes two parameters  arraylist(entries to store the values) and class coparator 
            Collections.sort(entries, new Comparator<Map.Entry<Integer,Float>>()
            // function compare takes two parameters element1 and element2  
            {   
                public int compare(Map.Entry<Integer,Float> e1, Map.Entry<Integer,Float> e2) {
                 // return the largest value and puts that into entries
                    return e2.getValue().compareTo(e1.getValue());
                }
            });
            // Put the sorted entries into a new LinkedHashMap(sordedMap)
            LinkedHashMap<Integer,Float> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<Integer,Float> entry : entries) {
                sortedMap.put(entry.getKey(), entry.getValue()); 
            }
            // Print the sorted map
            for (Map.Entry<Integer,Float> entry : sortedMap.entrySet()){
                System.out.println("Document " +  entry.getKey() + "  has cosine similarity : " + entry.getValue());
            }

    }
    public void search(String word) {
        DictEntry entry = index.get(word);
        if (entry == null) {
            System.out.println("No documents contain the word " + word);
        } else {
            Posting p = entry.pList; // posting list of documents contains this word 
            while (p != null) {
                    int dnum = p.docId;  // number of document
                    float  tf=((float) p.dtf) / Nt[dnum -1]; 
                    double idf =  Math.log10((float) (10.0/ entry.doc_freq  ));
                    double tf_idf =tf*idf;
                    System.out.println("Document " + (dnum) + " contains the word " + "[ " + word + " ]" + " => " + p.dtf + " times" +"\n"+ "Positions: " + p.positions);
                System.out.println("idf = log10(10/"+ entry.doc_freq + ") = " + idf + "\n" + "tf = "+ p.dtf+"/"+Nt[dnum -1]+" = "+tf +"\n"+"TF-IDF Weight = " + tf_idf);
                    p = p.next;
            }
            System.out.println("Number of documents = " +entry.term_freq+"      Number of documents that contain the word = "+entry.doc_freq+"\n");
        }
    }
     // web crawler 
    public void getPageLinks(String URL, int depth) {
        if (depth <= 0 || links.contains(URL)) {
            return;
        }
        
        try {
            if (links.add(URL)) {
                System.out.println(URL);
            }
            Document document = Jsoup.connect(URL).get();
            Elements linksOnPage = document.select("a[href]");
            for (Element page : linksOnPage) {
                getPageLinks(page.attr("abs:href"), depth - 1);
            }
        } catch (IOException e) {
            System.err.println("For '" + URL + "': " + e.getMessage());
        }
    }
}