package interpreter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Interpreter{
    static boolean hadError = false;
    public static void main(String[] args) throws IOException {
        //first step: configuring which way to run the script
        if(args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if(args.length == 1) {
            runFile(args[0]);
        } else{
            runPrompt();
        }
    }

    // runs the file given the path in command line
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError) System.exit(65);
    }

    // reads each line of code 
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for( ; ; ) {
            System.out.print("> ");
            String line = reader.readLine();
            if(line==null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for(Token token: tokens) {
            System.out.println(token);
        }
    }

    // error handling
    static void error(int line, String message) {
        report(line, "", message);
    }

    // error reporting
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error "+ where + ": " + message);
        hadError = true;
    }
}