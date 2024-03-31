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
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("func",    FUNC);
        keywords.put("if",     IF);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
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
            case 'A':
                if (match('N') && match('D')) {
                    addToken(AND);
                }
                break;
            case 'c':
                if (match('l') && match('l') && match('a')
                        && match('s') && match('s')) {
                    addToken(CLASS);
                }
                break;
            case 'E':
                if (match('L') && match('S') && match('E')) {
                    addToken(ELSE);
                }
                break;
            case 'f':
                if (match('a') && match('l') && match('s') && match('e')) {
                    addToken(FALSE);
                }
                if (match('u') && match('n') && match('c')) {
                    addToken(FUNC);
                }
                if (match('o') && match('r')) {
                    addToken(FOR);
                }
                break;
            case 't':
                if(match('r') && match('u') && match('e')){
                    addToken(TRUE);
                }
                if(match('h') && match('i') && match('s')) {
                    addToken(THIS);
                }
                break;
            case 'n':
                if(match('u') && match('l') && match('l')){
                    addToken(NULL);
                }
                break;
            case 'p':
                if(match('r') && match('i') && match('n') && match('t')){
                    addToken(PRINT);
                }
                break;
            case 's':
                if(match('u') && match('p') && match('e') && match('r')){
                    addToken(SUPER);
                }
                break;
            case 'W':
                if(match('H') && match('I') && match('L') && match('E')){
                    addToken(WHILE);
                }
                break;
            case 'v':
                if(match('a') && match('r')){
                    addToken(VAR);
                }
                break;
            case 'O':
                if(match('R')){
                    addToken(OR);
                }
                break;
            case 'r':
                if(match('e') && match('t') && match('u')
                        && match('r') && match('n')) {
                    addToken(RETURN);
                }
                break;
            case '"': string(); break;
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
        addToken(STRING, value);
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
