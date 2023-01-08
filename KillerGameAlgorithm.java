import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class KillerGameAlgorithm {
    ArrayList<String> players = new ArrayList<>();
    HashMap<String, String> targets = new HashMap<>();
    HashMap<String, String> playerCodes = new HashMap<>();
    String[] weapons;
    String[] places;
    int amountOfPlayers = 0;
    String folder;

    public KillerGameAlgorithm(String[] args){
        folder = args[0];
        for (int i = 1; i < args.length; i++) {
            players.add(args[i]);
            amountOfPlayers++;
        }
        Collections.shuffle(players);
        
        targets.put(players.get(amountOfPlayers-1), players.get(0));
        for (int i = 0; i < amountOfPlayers - 1; i++) {
            targets.put(players.get(i), players.get(i+1));
        }

        weapons = assign("files/weapons.txt");
        places = assign("files/places.txt");

        for (int i = 0; i < args.length -1 ; i++) {
            System.out.println(players.get(i) + " --> " + targets.get(players.get(i)) + ": " + weapons[i] + ", " + places[i]);
        }

        createDocs();
    }

    public String[] assign(String filename){
        ArrayList<String> alternatives = new ArrayList<>();
        try{
            BufferedReader file = new BufferedReader(new FileReader(filename));
            String line = file.readLine();
            while (line != null){
                alternatives.add(line);
                line = file.readLine();
            }
            file.close();
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            // Exit the program    
            System.exit(1);

        }
        
        String[] result = new String[amountOfPlayers];

        Random random = new Random();
        for (int i = 0; i < amountOfPlayers; i++) {
            result[i] = alternatives.get(random.nextInt(alternatives.size()));

            if(alternatives.isEmpty()){
                System.out.println("To few options in: " + filename);
                System.exit(0);
            }

            alternatives.remove(result[i]);
        }
        return result;
    }


    public void createDocs(){
        Random random = new Random();
        for (String player : players) {
            playerCodes.put(player, Integer.toHexString(random.nextInt(1<<20)));
        }
        createDirectory(folder);
        writePages("files/pageTemplate.html");
        writeJavascript();
        
    }

    public String getPageTemplate(String filename){
        String text = "";
        try{
            BufferedReader file = new BufferedReader(new FileReader(filename));
            String line = file.readLine();
            while (line != null){
                text = text + "\n" + line;
                line = file.readLine();
            }
            file.close();
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            // Exit the program    
            System.exit(1);

        }
        return text;
    }

    public void writePages(String filename){
        String text = getPageTemplate(filename);
        FileWriter writer;
        for (int i = 0; i < players.size(); i++) {
            try{
                writer = new FileWriter(folder+"\\"+playerCodes.get(players.get(i))+".html");
                writer.write(text.replaceAll("\\[TARGET\\]", targets.get(players.get(i)))
                                .replaceAll("\\[WEAPON\\]", weapons[i])
                                .replaceAll("\\[PLACE\\]", places[i]));
                writer.close();
            } catch (IOException e){
                    System.out.println("Roor" + e);
            }
            
        }
    }

    public void createDirectory(String folderName){
        try {
            File dir = new File(folderName);
            dir.mkdirs();
            File tmp = new File(dir, "README.txt");
            tmp.createNewFile();
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            // Exit the program    
            System.exit(1);

        }
    }

    public void writeJavascript(){
        String text = getPageTemplate("files/code.js");
        String array = "const codes = \\[";
        for (String player : players) {
            array += "\"" +playerCodes.get(player) + "\", ";
        }
        array += "HEJ";
        array = array.replaceFirst(", HEJ", "\\]");

        text = text.replaceAll("\\[CODES\\]", array);


        FileWriter writer;

        try{
            writer = new FileWriter("code.js");
            writer.write(text);
            writer.close();
        } catch (IOException e){                
            System.out.println("Roor" + e);
        }
            
    }


    public static void main(String args[]){
        new KillerGameAlgorithm(args);
    }
}
