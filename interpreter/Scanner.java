package interpreter;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
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

    private void scanToken() {
        char c = nextChar();
        // getting the single-char tokens
        switch(c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            //operators
            case '!': 
                addToken(match('=') ? NOT_EQUAL : NOT);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EVAL : ASSIGN);
                break;
            case '<':
                addToken(match('=') ? LESS_OR_EQUAL : LESS_THAN);
                break;
            case '>':
                addToken(match('=') ? GREATER_OR_EQUAL : GREATER_THAN);
                break;
            // for slash
            case '/':
                if(match('/')) {
                    // comment goes on until new line
                    while(lookAhead() != '\n' && !isAtEnd()) nextChar();
                } else {
                    addToken(SLASH);
                }
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
            case 'D':
                if(match('I') && match('S') && match('P') && match('L') && match('A') && match('Y')){
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
            default:
                if (isAlpha(c)) {
                    identifier();
                } else if (isDigit(c)){
                    identifier();
                } else {
                    Interpreter.error(line, "Unexpected character.");
                }
                break;
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
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
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

    private boolean isAlphaNumeric(char c){
        return isAlpha(c) || isDigit(c);
    }

    private void identifier() {
        if (isDigit(source.charAt(start))) {
            scanNumber();
        } else {
            while (isAlphaNumeric(lookAhead())) nextChar();
            String text = source.substring(start, current);
            TokenType type = keywords.get(text);
            if (type == null) {
                type = RESERVED_KEYWORD;
            }
            addToken(type);
        }
    }
}
