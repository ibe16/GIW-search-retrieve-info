package InformationRetrieval;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchEngine {
    private EnglishAnalyzer analyzer;
    private Directory indexDirectory;
    private IndexReader reader;
    IndexSearcher searcher;

    public SearchEngine(String index_path) throws IOException {
        indexDirectory = FSDirectory.open(Paths.get(index_path));
        analyzer = new EnglishAnalyzer();
        reader = DirectoryReader.open(indexDirectory);
        searcher = new IndexSearcher(reader);
    }

    public TopDocs searchByTitle(String title) throws ParseException, IOException {
        QueryParser queryParser= new QueryParser("title", this.analyzer);
        Query titleQuery = queryParser.parse(title);
        TopDocs results = this.searcher.search(titleQuery, 5);
        return results ;
    }

    public ArrayList<String> getTitles (TopDocs results) throws IOException {
        ArrayList<String> titles = new ArrayList<String>();
        for (ScoreDoc sd : results.scoreDocs) 
                {
                    Document d = this.searcher.doc(sd.doc);
                    titles.add(String.format(d.get("title")));
                }
        return titles;
    }

    public TopDocs searchByAbstract(String text) throws ParseException, IOException {
        QueryParser queryParser= new QueryParser("abstract", this.analyzer);
        Query titleQuery = queryParser.parse(text);
        TopDocs results = this.searcher.search(titleQuery, 5);
        return results ;
    }

    public ArrayList<String> getAbstracts (TopDocs results) throws IOException {
        ArrayList<String> texts = new ArrayList<String>();
        for (ScoreDoc sd : results.scoreDocs) 
                {
                    Document d = this.searcher.doc(sd.doc);
                    texts.add(String.format(d.get("abstract")));
                }
        return texts;
    }

    // public static void main(String[] args) {
    //     try {
    //         SearchEngine se = new SearchEngine("./indice");
    //         try {
    //             TopDocs hits = se.searchByAbstract("Commercial exploitation");
    //             System.out.println("Resultados: " + hits.totalHits);
    //             for (ScoreDoc sd : hits.scoreDocs) 
    //             {
    //                 Document d = se.searcher.doc(sd.doc);
    //                 System.out.println(String.format(d.get("abstract")));
    //             }

    //         } catch (ParseException e) {
    //             // TODO Auto-generated catch block
    //             e.printStackTrace();
    //         }
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    //}

    
}