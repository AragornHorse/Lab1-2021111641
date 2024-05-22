
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/*
 *  some functions can be used by any class
 */
class Tool{

    /*
     * is `path` a path ? 
     */
    public static boolean isFilePath(String path) {
        File file = new File(path);
        return file.exists() && !file.isDirectory();
    }

    /*
     * print string[] lst
     */
    public static void print_string_array(String[] lst){
        int i;
        System.out.printf("[ ");
        for(i=0; i<lst.length-1; i++){
            System.out.printf("\"%s\", ", lst[i]);
        }
        System.out.printf("\"%s\" ]\n", lst[i]);
    }
}


/*
 *    i/o between main memory and disk, and some pretreatment
 *      1. read .gio file to this.txt
 *      2. split to tokens
 *    
 *    input:
 *        file path or the txt content
 */
public class gio {

    // pretreated txt
    // "I,   y" => "i y"
    String txt = new String();
    
    // tokens
    String[] tokens;

    /*
     *   read `path` to this.txt
     */
    private void read_gio(String path){
        try {
            File file = new File(path);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                this.txt += " " + line;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *  1. replace symbols
     *  2. to lower case
     *  3. split to this.tokens
     */
    private void parse_txt(){
        this.txt = this.txt.replaceAll("\\pP", " ");
        this.txt = this.txt.toLowerCase();
        this.tokens = this.txt.trim().split(" +");
    } 

    /*
     *   if init this class with no-parameters, do nothing
     */
    public gio(){}

    /*
     *   input a path or a txt
     */
    public gio(String path){
        if(Tool.isFilePath(path)){
            this.read_gio(path);
        }else{
            this.txt = path;
        }
        this.parse_txt();
    }

    /*
     *   for debugging
     */
    static public void main(String[] args) {
        gio g = new gio("who you are");
        Tool.print_string_array(g.tokens);
    }
}
