package interpreter;
enum TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA,
    DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    //One or two character token pair
    NOT, NOT_EQUAL, ASSIGN, EQUAL_EVAL, GREATER_THAN,
    GREATER_OR_EQUAL, LESS_THAN, LESS_OR_EQUAL,

    //Keywords
    AND, CLASS, ELSE, FALSE, FUNC, FOR, IF, 
    NULL, OR, PRINT, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}