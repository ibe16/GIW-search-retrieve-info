package InformationRetrieval;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import java.awt.*;
import java.awt.event.*; 



public class App extends JFrame implements ActionListener{
    Indexer indexer;
    SearchEngine search_engine;
    ArrayList<String> results_title;
    ArrayList<String> results_text;
    TopDocs hits;

    private JFrame frame;
    private Container c; 
    private JLabel title; 
    private JLabel text; 
    private JTextField title_searched;
    private JTextField text_searched;
    private JButton search; 
    private JButton back_to_form;
    private JButton back_to_list;
    private JButton reset; 
    private JScrollPane scroll_panel;
    private JPanel results_panel;
    private JList results_list;
    private JTextArea text_area;

    public App(){
        super("Buscador");

        try {
            indexer = new Indexer("./indice");
            indexer.indexDocuments();
            search_engine = new SearchEngine("./indice");
        } catch (IOException e) {
            e.printStackTrace();
        }
        results_title = new ArrayList<String>() ;
        results_text = new ArrayList<String>() ;
        
    }


    public void SearchForm(){
        setBounds(300, 90, 500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
        setResizable(false); 

        c = getContentPane(); 
        c.setLayout(null);
        
        title = new JLabel("Buscador"); 
        title.setFont(new Font("Arial", Font.PLAIN, 30)); 
        title.setSize(300, 30); 
        title.setLocation(200, 30); 
        c.add(title); 

        title = new JLabel("Title"); 
        title.setFont(new Font("Arial", Font.PLAIN, 20)); 
        title.setSize(100, 20); 
        title.setLocation(100, 100); 
        c.add(title); 

        title_searched = new JTextField(); 
        title_searched.setFont(new Font("Arial", Font.PLAIN, 15)); 
        title_searched.setSize(190, 20); 
        title_searched.setLocation(200, 100); 
        c.add(title_searched);
        
        text = new JLabel("Abstract"); 
        text.setFont(new Font("Arial", Font.PLAIN, 20)); 
        text.setSize(190, 20); 
        text.setLocation(100, 150); 
        c.add(text); 

        text_searched = new JTextField(); 
        text_searched.setFont(new Font("Arial", Font.PLAIN, 15)); 
        text_searched.setSize(190, 20); 
        text_searched.setLocation(200, 150);  
        c.add(text_searched);

        search = new JButton("Search"); 
        search.setFont(new Font("Arial", Font.PLAIN, 15)); 
        search.setSize(200, 30); 
        search.setLocation(150, 450); 
        search.addActionListener(this); 
        c.add(search); 


        setVisible(true);
    }

    public void ScrollList() {
        setLayout(new BorderLayout());
        setResizable(true); 
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        results_list = new JList(results_title.toArray());
        this.addListenertoJList(this);

        scroll_panel = new JScrollPane(results_list);
    
        getContentPane().add(scroll_panel, BorderLayout.CENTER);

        back_to_form = new JButton("Back"); 
        back_to_form.setFont(new Font("Arial", Font.PLAIN, 15)); 
        back_to_form.addActionListener(this);
        
        getContentPane().add(back_to_form, BorderLayout.SOUTH);

        setVisible(true);
      }

      public void showText(int index){
        setLayout(new BorderLayout());
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        text_area = new JTextArea();
        text_area.setFont(new Font("Arial", Font.PLAIN, 15)); 
        text_area.setLineWrap(true);
        text_area.setText(results_text.get(index));

        scroll_panel = new JScrollPane(text_area);
        getContentPane().add(scroll_panel, BorderLayout.CENTER);


        back_to_list = new JButton("Back"); 
        back_to_list.setFont(new Font("Arial", Font.PLAIN, 15)); 
        back_to_list.addActionListener(this);
        
        getContentPane().add(back_to_list, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void addListenertoJList(final ActionListener al){
        results_list.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    al.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "ENTER"));
                }
            }
        });
    
        results_list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    al.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "ENTER"));
                }
            }
        });
    }

    public void actionPerformed(ActionEvent e){
        if (e.getSource() == search){
            if (!title_searched.getText().isEmpty()){
                try {
                    hits = search_engine.searchByTitle(title_searched.getText());
                    results_title = search_engine.getTitles(hits);
                    results_text = search_engine.getAbstracts(hits);
                } catch (ParseException | IOException e1) {
                    e1.printStackTrace();
                }

                getContentPane().removeAll();
                this.ScrollList();
                revalidate();
                repaint();
            }
            else if (!text_searched.getText().isEmpty()){
                try {
                    hits = search_engine.searchByAbstract(text_searched.getText());
                    results_title = search_engine.getTitles(hits);
                    results_text = search_engine.getAbstracts(hits);
                } catch (ParseException | IOException e1) {
                    e1.printStackTrace();
                }

                getContentPane().removeAll();
                this.ScrollList();
                revalidate();
                repaint();
            }
        }

        if (e.getSource() == results_list){
            int index = results_list.getSelectedIndex();
            
            getContentPane().removeAll();
            this.showText(index);
            revalidate();
            repaint();
        }

        if (e.getSource() == back_to_form){
            getContentPane().removeAll();
            this.SearchForm();
            revalidate();
            repaint();
        }

        if (e.getSource() == back_to_list){
            getContentPane().removeAll();
            this.ScrollList();
            revalidate();
            repaint();
        }

    }

    public void consoleInterface(){
        System.out.println("--------------------------------");
        System.out.println("--------------------------------");
        System.out.println("      BUSCADOR CON LUCENE       ");
        System.out.println("--------------------------------");
        System.out.println("--------------------------------");

        System.out.println("Selecciones para buscar");
        System.out.println("\t 1- Buscar por title");
        System.out.println("\t 2- Buscar por abstract");

        String input = System.console().readLine();
        
        System.out.println("Introduzca la búsqueda: ");

        String busqueda = System.console().readLine();

        if (input.equals("1")){
            try {
                hits = this.search_engine.searchByTitle(busqueda);
                results_title = this.search_engine.getTitles(hits);
                results_text = this.search_engine.getAbstracts(hits);
            } catch (ParseException | IOException e1) {
                e1.printStackTrace();
            }
        }
        else if (input.equals("2")){
            try {
                hits = this.search_engine.searchByAbstract(busqueda);
                results_title = this.search_engine.getTitles(hits);
                results_text = this.search_engine.getAbstracts(hits);
            } catch (ParseException | IOException e1) {
                e1.printStackTrace();
            }
        }
        else {
            System.out.println("Por favor, selecciones una de estás opciones");
            System.out.println("\t 1- Buscar por title");
            System.out.println("\t 2- Buscar por abstract");
        }

        System.out.println("Indique el número para mostrar el contenido");
        for (int i=0 ; i < results_title.size() ; i++){
            System.out.println(i + results_title.get(i));
        }

        int index = Integer.parseInt(System.console().readLine());
        System.out.println(results_text.get(index));

        return ;

    }

    public void graphicInterface(){
        this.SearchForm();
    }
    
    public static void main(String[] args) {
        App f = new App();
        f.consoleInterface(); 
    }
}
