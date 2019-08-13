package br.edu.ufabc.microkotlin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.edu.ufabc.microkotlin.TokenType.*;

/**
 * Classe responsável pela análise léxica do código-fonte.
 */
public class Scanner {

  /**
   * Código-fonte de entrada.
   */
  private final String sourceCode;

  /**
   * Lista de tokens criados.
   */
  private final List<Token> tokens = new ArrayList<>();

  /**
   * Índice de ínicio do lexema atual.
   */
  private int start = 0;

  /**
   * Índice do caractere atual.
   */
  private int current = 0;

  /**
   * Linha atual.
   */
  private int line = 1;

  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("break", BREAK);
    keywords.put("do", DO);
    keywords.put("else", ELSE);
    keywords.put("false", FALSE);
    keywords.put("fun", FUN);
    keywords.put("if", IF);
    keywords.put("null", NULL);
    keywords.put("print", PRINT);
    keywords.put("printLn", PRINTLN);
    keywords.put("return", RETURN);
    keywords.put("true", TRUE);
    keywords.put("val", VAL);
    keywords.put("var", VAR);
    keywords.put("while", WHILE);
  }

  public Scanner(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  /**
   * Efetua a criação dos tokens do código-fonte.
   *
   * @return lista de todos os tokens
   */
  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  /**
   * Cria o próximo token.
   */
  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case '*': addToken(TIMES); break;
      case '%': addToken(REM); break;
      case ':': addToken(COLLON); break;
      case ';': addToken(SEMICOLON); break;

      case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
      case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
      case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
      case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

      case '/':
        if (match('/')) {
          while (peek() != '\n' && !isAtEnd()) advance();
        } else if (match('*')) {
          skipBlockComment();
        } else {
          addToken(SLASH);
        }
        break;

      case ' ':
      case '\r':
      case '\t':
        break;

      case '\n':
        line++;
        break;

      case '"': string(); break;

      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          MicroKotlin.error(line, "Unexpected character");
        }
        break;
    }
  }

  /**
   * Efetua a leitura de um identificador.
   */
  private void identifier() {
    while (isAlphaNumeric(peek())) advance();

    String text = sourceCode.substring(start, current);

    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;

    addToken(type);
  }

  /**
   * Efetua a leitura de um número.
   */
  private void number() {
    while (isDigit(peek())) advance();

    if (peek() == '.' && isDigit(peekNext())) {
      advance();
      while (isDigit(peek())) advance();
    }

    double value = Double.parseDouble(sourceCode.substring(start, current));
    addToken(NUMBER, value);
  }

  /**
   * Efetua a leitura de uma string.
   */
  private void string() {
    while ((peek() != '"' || peekPrevious() == '\\') && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    if (isAtEnd()) {
      MicroKotlin.error(line, "Unterminated string.");
      return;
    }

    // O caractere " que fecha a string.
    advance();

    String value = sourceCode.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  /**
   * Pula um bloco de comentários.
   */
  private void skipBlockComment() {
    int level = 1;
    while (level > 0) {
      if (peek() == '\0') {
        MicroKotlin.error(line, "Unterminated block comment.");
        return;
      }

      if (peek() == '/' && peekNext() == '*') {
        advance();
        advance();
        level++;
        continue;
      }

      if (peek() == '*' && peekNext() == '/') {
        advance();
        advance();
        level--;
        continue;
      }

      if (peek() == '\n') {
        advance();
        line++;
        continue;
      }

      advance();
    }
  }

  /**
   * Verifica se o caractere atual é o esperado.
   * Se sim, incrementa a posição atual.
   *
   * @param expected caractere esperado
   * @return verdadeiro se é o esperado
   */
  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (sourceCode.charAt(current) != expected) return false;

    current++;
    return true;
  }

  /**
   * Retorna o caractere atual.
   *
   * @return o caractere atual
   */
  private char peek() {
    if (isAtEnd()) return '\0';
    return sourceCode.charAt(current);
  }

  /**
   * Retorna o próximo caractere.
   *
   * @return o próximo caractere.
   */
  private char peekNext() {
    if (current + 1 >= sourceCode.length()) return '\0';
    return sourceCode.charAt(current + 1);
  }

  /**
   * Retorna o caractere anterior.
   *
   * @return o caractere anterior.
   */
  private char peekPrevious() {
    return sourceCode.charAt(current - 1);
  }

  /**
   * Checa se o caractere é uma letra ou underscore.
   *
   * @return verdadeiro se é uma letra ou underscore
   */
  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  /**
   * Checa se um caractere é uma letra ou dígito.
   *
   * @return verdadeiro se é uma letra ou digito
   */
  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  /**
   * Checa se o caractere é um dígito.
   *
   * @return verdadeiro se é um dígito
   */
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  /**
   * Checa se o arquivo foi completamente lido.
   *
   * @return verdadeiro se foi completamente lido
   */
  private boolean isAtEnd() {
    return current >= sourceCode.length();
  }

  /**
   * Avança a leitura em um caractere.
   *
   * @return o caractere atual
   */
  private char advance() {
    current++;
    return sourceCode.charAt(current - 1);
  }

  /**
   * Adiciona um token na lista.
   *
   * @param type tipo do token
   */
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  /**
   * Adiciona um token na lista com valor literal.
   *
   * @param type tipo do token
   * @param value valor do literal
   */
  private void addToken(TokenType type, Object value) {
    String text = sourceCode.substring(start, current);
    tokens.add(new Token(type, text, value, line));
  }
}
