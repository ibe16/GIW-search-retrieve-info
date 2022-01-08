// https://www.w3schools.com/java/java_files_read.asp
// https://howtodoinjava.com/lucene/lucene-index-search-examples/

package InformationRetrieval;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
    private EnglishAnalyzer analyzer;
    private Directory indexDirectory;
    private IndexWriter writer;

    public Indexer(String index_directory_path) throws IOException {
        analyzer = new EnglishAnalyzer();

        // Directorio donde se guarda el índice
        indexDirectory = FSDirectory.open(Paths.get(index_directory_path));

        // Indexador
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(indexDirectory, config);
    }

    public void close() throws IOException {
        writer.close();
    }

    public Document createDocument(String title, String text) {
        Document document = new Document();
        document.add(new TextField("title", title, Field.Store.YES));
        document.add(new TextField("abstract", text, Field.Store.YES));
        return document ;
    }

    public void indexDocuments() throws IOException {
        ArrayList<Document> documents = new ArrayList<>() ;

        Document document1 ;
        String title = "";
        String text = "";

        // Explorar todos los archivos de una carpeta
        File folder = new File ("../awd_1990_00");
        File[] files = folder.listFiles();
        for (File file : files){
            Scanner reader = new Scanner(file) ;
            while (reader.hasNext()){
                String data = reader.next() ;
                if (data.equals("Title")){
                    reader.next();
                    reader.next();
                    //reader.next();

                    data = reader.next();
                    while(!data.equals("Type")){
                        title = title + data + " ";
                        data = reader.next();
                    }
                }
                if (data.equals("Abstract")){
                    reader.next();
                    text = reader.next() + " ";
                    while(reader.hasNext()){
                        text = text + reader.next() + " ";
                    }
                    
                }
                
            }
            document1 = this.createDocument(title, text);
            documents.add(document1);
            text = " ";
            title = " ";
            reader.close();
        }

        

        // Eliminamos todos los documentos (solo para prueba)
        this.writer.deleteAll();

        this.writer.addDocuments(documents);
        this.writer.commit() ;
        this.close() ;

        System.out.println("Índice creado");
    }

    // public static void main(String[] args) {
    //     try {
    //         Indexer i = new Indexer("./indice");
    //         i.indexDocuments();
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }
}