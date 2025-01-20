import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    
    private Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.tokenize();
    }

    private void error(String message) {
        throw new RuntimeException(message);
    }

    private void verifyGrammar(TokenType tokenType) {
        if(currentToken.type == tokenType) {
            currentToken = lexer.tokenize();
        } else {
            error("Expected token " + tokenType + " but found " + currentToken.type);
        }
    }

    public Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        verifyGrammar(TokenType.LBRACE);

        while(currentToken.type != TokenType.RBRACE) {
            String key = parseString();
            verifyGrammar(TokenType.COLON);
            Object value = parseValue();
            map.put(key, value);

            if(currentToken.type == TokenType.COMMA) {
                verifyGrammar(TokenType.COMMA);
            } else if(currentToken.type != TokenType.RBRACE) {
                error("Expected to find " + " , or } but found " + currentToken.type);
            }
        }

        verifyGrammar(TokenType.RBRACE);
        return map;
    }

    public String parseString() {
        String value = currentToken.value.toString();
        verifyGrammar(TokenType.STRING);
        return value;
    }

    private Number parseNumber() {
            Number value = (Number) currentToken.value;
            verifyGrammar(TokenType.NUMBER);
            return value;
    }

    private Object parseValue() {
        switch(currentToken.type) {
            case LBRACE:
               return parseObject();
            case STRING:
               return parseString();
            case LBRACKET:
               return parseArray();
            case NUMBER:
               return parseNumber();
            case TRUE:
               verifyGrammar(TokenType.TRUE);
               return true;
            case FALSE:
               verifyGrammar(TokenType.FALSE);
               return false;
            case NULL:
               verifyGrammar(TokenType.NULL);
               return null;
            default:
               error("Invalid token " + currentToken.type);
               return null;
        }
    }

    private List<Object> parseArray() {
        List<Object> array = new ArrayList<>();
        verifyGrammar(TokenType.LBRACKET);
        while(currentToken.type != TokenType.RBRACKET) {
            array.add(parseValue());
            if(currentToken.type == TokenType.COMMA) {
                verifyGrammar(TokenType.COMMA);
            } else if(currentToken.type != TokenType.RBRACKET) {
                error("The array is invalid");
            }
        }
        verifyGrammar(TokenType.RBRACKET);
        return array;
    }

    public Object parse() {
        return parseValue();
    }


    public static void main(String[] args) {
        String json = "{\"name\": \"John\", \"age\": 30, \"isStudent\": false, \"hobbies\": [\"coding\", \"reading\"]}";
        Lexer lexer = new Lexer(json);
        Parser parser = new Parser(lexer);

        Object result = parser.parse();
        System.out.println(result);
    }
}
