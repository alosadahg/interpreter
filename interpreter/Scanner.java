package interpreter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static interpreter.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String, TokenType> keywords;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
    }

    static {
        keywords = new HashMap<>();
        keywords.put("ELSE",   ELSE);
        keywords.put("IF",     IF);
        keywords.put("WHILE",  WHILE);
        keywords.put("BEGIN", BEGIN);
        keywords.put("END", END);
        keywords.put("DISPLAY", DISPLAY);
        keywords.put("SCAN", SCAN);
        keywords.put("CODE", CODE);
        keywords.put("AND", AND);
        keywords.put("OR", OR);
        keywords.put("NOT", NOT);
    }

    List<Token> scanTokens() {
        while(!isAtEnd()) {
            // start of lexeme
            start = current;
            scanToken();
        }

        // EOF -> End of File 
        // tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void string() {
        while (lookAhead() != '"' && !isAtEnd()) {
            if(lookAhead() == '\n') line++;
            nextChar();
        }

        if(isAtEnd()) {
            Interpreter.error(line, "Unterminated String");
            return;
        }

        // the closing "
        nextChar();

        // trim surrounding quotes
        String value = source.substring(start + 1, current - 1);
        if(value.equals("TRUE")) {
            addToken(BOOL, TRUE);
        } else if(value.equals("FALSE")) {
            addToken(BOOL, FALSE);
        } else {
            addToken(STRING, value);
        }
    }

    private char lookAhead() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char nextChar() {
        return source.charAt(current++);
    }

    private void scanNumber(){
        while(isDigit(lookAhead())) nextChar();

        
        if(lookAhead() == '.' && isDigit(lookAheadNext())){
            nextChar();

            while (isDigit(lookAhead())) nextChar();
            addToken(FLOAT, Double.parseDouble(source.substring(start, current)));
        } else {
            addToken(INT, Integer.parseInt(source.substring(start, current)));
        }
    }
    
    private char lookAheadNext(){
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean match(char expectedChar) {
        if(isAtEnd()) return false;
        if(source.charAt(current) != expectedChar) return false;
        
        current++;
        return true;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isSymbol(char c) {
        return c == '_';
    }

    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c) || isSymbol(c) ;
    }

    private void identifier() {
        if (isDigit(source.charAt(start))) {
            scanNumber();
        } else {
            while (isAlphaNumeric(lookAhead())) nextChar();
            String text = source.substring(start, current);
            TokenType type = keywords.get(text);
            if (type == null) {
                type = VARIABLE;
            }
            addToken(type);
        }
    }

    private void scanToken() {
        char c = nextChar();
        if (isAlphaNumeric(c)){
            identifier();
        }
        // getting the single-char tokens
        switch(c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '[': addToken(LEFT_BRACKET); break;
            case ']': addToken(RIGHT_BRACKET); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '+': addToken(PLUS); break;
            case '*': addToken(STAR); break;
            case '$': addToken(NEW_LINE); break;
            case '&': addToken(CONCAT); break;
            case '%': addToken(MODULO); break;

            //operators
            case '=':
                addToken(match('=') ? EQUAL_EVAL : ASSIGN);
                break;
            case '<':
                if(match('=')) {
                    addToken(LESS_OR_EQUAL);
                } else if(match('>')) {
                    addToken(NOT_EQUAL);
                } else {
                    addToken(LESS_THAN);
                }
                break;
            case '>':
                addToken(match('=') ? GREATER_OR_EQUAL : GREATER_THAN);
                break;
            // for slash
            case '/':
                addToken(SLASH);
                break;
            //comment
            case '#':
                while(lookAhead() != '\n' && !isAtEnd()) nextChar();
                break;
            //whitespaces
            case ' ':
            case  '\t':
                break;
            case '\n':
                line++;
                break;
            //reserved keyword
            case 'I':
                if (match('F')) {
                    addToken(IF);
                }
                break;
            case 'E':
                if (match('L') && match('S') && match('E')) {
                    addToken(ELSE);
                }
                break;
            case 'S':
                if (match('C') && match('A') && match('N') && match(':')){
                    addToken(SCAN);
                }
                break;
            case 'D':
                if(match('I') && match('S') && match('P') && match('L') && match('A') && match('Y') && match(':')){
                    addToken(DISPLAY);
                }
                break;
            case 'W':
                if(match('H') && match('I') && match('L') && match('E')){
                    addToken(WHILE);
                }
                break;
            case 'C':
                if(match('O') && match('D') && match('E')){
                    addToken(CODE);
                }
                break;
            case 'O':
                if(match('R')){
                    addToken(OR);
                }
                break;
            case 'A':
                if (match('N') && match('D')){
                    addToken(AND);
                }

                break;
            case 'N':
                if (match('O') && match('T')){
                    addToken(NOT);
                }
                break;
            case '"': string(); break;
            default:
                Interpreter.error(line, "Unexpected character.");
                break;
        }
    }
}
