import java.io.IOException;
import java.util.Scanner;
public class main {
    public static void main(String[] args) throws IOException {
        PositionalIndex index = new PositionalIndex();
    for (int i = 1; i <= 10; i++) {
            index.buildIndex( i + ".txt");}
        Scanner scanner = new Scanner(System.in);
        Scanner in = new Scanner(System.in);
        int f=0;
    while(f!=4){
        System.out.println("Enter 1-to Search ");
        System.out.println("Enter 2-to cosine similarity ");
        System.out.println("Enter 3-to Web crawler ");
        System.out.println("Enter 4-to Finish ");
        f =in.nextInt();
      if (f==1){
          while (true) {
                System.out.print("Enter a word to search for: ");
                String s = scanner.nextLine();
                if (s.equals("exit")) break;
                index.search(s);
                System.out.print("Enter (exit) to finish or ");
             }
      } else if (f==2) {
          System.out.print("Enter a words to search for cosine similarity: ");
          String query = scanner.nextLine();
          index.getquery(query);
          index.cossim(query);
          index.print();
      } else if (f==3) {
          index.getPageLinks("https://www.premierleague.com/",2);
      }

    }

    }

}