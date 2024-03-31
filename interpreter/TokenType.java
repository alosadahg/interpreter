package interpreter;
enum TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACKET, RIGHT_BRACKET, COMMA,
    DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, NEW_LINE, CONCAT,

    //One or two character token pair
    NOT, NOT_EQUAL, ASSIGN, EQUAL_EVAL, GREATER_THAN,
    GREATER_OR_EQUAL, LESS_THAN, LESS_OR_EQUAL, MODULO,

    //Keywords
    AND, CLASS, ELSE, FALSE, FUNC, FOR, IF, 
    NULL, OR, PRINT, SUPER, THIS, TRUE, VAR, WHILE, RETURN,

    //Code Block Start

    RESERVED_KEYWORD,

    //data types
    STRING, INT, FLOAT,

    EOF
}