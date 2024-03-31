package interpreter;
enum TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA,
    DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    //One or two character token pair
    NOT, NOT_EQUAL, ASSIGN, EQUAL_EVAL, GREATER_THAN,
    GREATER_OR_EQUAL, LESS_THAN, LESS_OR_EQUAL,

    //Keywords
    BEGIN, END, DISPLAY, SCAN, IF, ELSE, WHILE, CODE,

    //Code Block Start

    RESERVED_KEYWORD,

    NUMBER,
    EOF
}