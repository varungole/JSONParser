public class Lexer {
    private final String text;
    private int pos = 0;
    private char currentChar;

    public Lexer(String text) {
        this.text = text;
        this.currentChar = text.charAt(pos);
    }

    private void advance() {
        pos++;
        currentChar = pos >= text.length() ? '\0' : text.charAt(pos);
    }

    private void skipWhitespace() {
        while(currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private String parseString() {
        StringBuilder result = new StringBuilder();
        advance();
        while(currentChar != '\0' && currentChar != '"') {
            result.append(currentChar);
            advance();
        }
        advance();
        return result.toString();
    }

    private Number parseNumber() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isDigit(currentChar) || currentChar == '.')) {
            result.append(currentChar);
            advance();
        }
        return result.toString().contains(".") ? Double.parseDouble(result.toString()) : Integer.parseInt(result.toString());
    }

    private Token tokenize() {
        while(currentChar != '\0') {
           if(Character.isWhitespace(currentChar)) {
            skipWhitespace();
            continue;
           }
           if(currentChar == '"') return new Token(TokenType.STRING, parseString());
           if(currentChar == '[') { advance(); return new Token(TokenType.LBRACKET, "["); }
           if(currentChar == ']') { advance(); return new Token(TokenType.RBRACKET, "]");}
           if(currentChar == '{') { advance(); return new Token(TokenType.LBRACE, "{");}
           if(currentChar == '}') { advance(); return new Token(TokenType.RBRACE, "}");}
           if(currentChar == ':') { advance(); return new Token(TokenType.COLON, ":");}
           if(currentChar == ',') { advance(); return new Token(TokenType.COMMA, ",");}
           if(Character.isDigit(currentChar) || currentChar == '-') return new Token(TokenType.NUMBER, parseNumber());
           if(text.startsWith("true", pos))
            { 
                pos+=4;
                currentChar = pos >= text.length() ? '\0' : text.charAt(pos); // Update currentChar
                return new Token(TokenType.TRUE, "true");}
           if(text.startsWith("false", pos))
            { 
                pos+=5;
                currentChar = pos >= text.length() ? '\0' : text.charAt(pos); // Update currentChar
                return new Token(TokenType.FALSE, "false");
            }
           if(text.startsWith("null", pos))
            {
                pos+=4;
                currentChar = pos >= text.length() ? '\0' : text.charAt(pos); // Update currentChar
                return new Token(TokenType.NULL, "null");
            }
           throw new RuntimeException("Invalid character");
        }
        return new Token(TokenType.EOF, null);
    }

    public static void main(String[] args) {
        String json = "{\"name\": \"John\", \"age\": 30, \"isStudent\": false, \"hobbies\": [\"coding\", \"reading\"]}";
        Lexer lexer = new Lexer(json);
    
        Token token;
        do {
            token = lexer.tokenize();
            System.out.println(token);
        } while (token.type != TokenType.EOF);
    }
    
}
