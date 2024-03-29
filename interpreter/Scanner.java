package interpreter;
import java.util.ArrayList;
import java.util.List;

import static interpreter.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
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

            default:
                Interpreter.error(line, "Unexpected character.");
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
}
