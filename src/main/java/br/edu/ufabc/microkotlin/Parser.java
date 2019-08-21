package br.edu.ufabc.microkotlin;

import java.util.List;
import br.edu.ufabc.microkotlin.expr.*;
import static br.edu.ufabc.microkotlin.TokenType.*;

/**
 * Classe responsável pela análise semântica.
 */
public class Parser {

  private static class ParseError extends RuntimeException {}

  /**
   * Tokens obtidos através do Scanner.
   */
  private final List<Token> tokens;

  /**
   * Índice de controle do token atual.
   */
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public Expr parse() {
    try {
      return expression();
    } catch (ParseError error) {
      return null;
    }
  }

  /**
   * Avalia a expressão.
   *
   * expression → assignment | or
   */
  private Expr expression() {
    return assignment();
  }

  /**
   * Avalia a atribuição.
   *
   * assignment → [identifier "="] expression
   */
  private Expr assignment() {
    Expr expr = or();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof ExprVariable) {
        Token name = ((ExprVariable) expr).name;
        return new ExprAssign(name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  /**
   * Avalia a operação ou.
   *
   * or → and ["||" and]
   */
  private Expr or() {
    Expr expr = and();

    while (match(OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new ExprLogical(expr, operator, right);
    }

    return expr;
  }

  /**
   * Avalia a operação e.
   *
   * and → equality ["&&" equality]
   */
  private Expr and() {
    Expr expr = equality();

    while (match(AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new ExprLogical(expr, operator, right);
    }

    return expr;
  }

  /**
   * Avalia a igualdade.
   *
   * equality → comparison [("!=" | "==") comparison]
   */
  private Expr equality() {
    Expr expr = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new ExprBinary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Avalia a comparação.
   *
   * comparison → addition [(">" | ">=" | "<" | "<=") addition]
   */
  private Expr comparison() {
    Expr expr = addition();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = addition();
      expr = new ExprBinary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Avalia a soma e subtração.
   *
   * addition → multiplication [("-" | "+") multiplication]
   */
  private Expr addition() {
    Expr expr = multiplication();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = multiplication();
      expr = new ExprBinary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Avalia a multiplicação, divisão e resto.
   *
   * multiplication → unary [("/" | "*" | "%") unary]
   */
  private Expr multiplication() {
    Expr expr = unary();

    while (match(SLASH, TIMES, REM)) {
      Token operator = previous();
      Expr right = unary();
      expr = new ExprBinary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Avalia a expressão unária.
   *
   * unary → [("!" | "-")] primary
   */
  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new ExprUnary(operator, right);
    }

    return primary();
  }

  /**
   * Avalia a expressão primária.
   *
   * primary → BOOLEAN | NUMBER | STRING | NULL | IDENTIFIER
   *         | "read" type "(" ")" | "(" expression ")"
   */
  private Expr primary() {
    if (match(FALSE)) return new ExprLiteral(false);
    if (match(TRUE)) return new ExprLiteral(true);
    if (match(NULL)) return new ExprLiteral(null);

    if (match(NUMBER, STRING)) {
      return new ExprLiteral(previous().value);
    }

    if (match(IDENTIFIER)) {
      return new ExprVariable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new ExprGrouping(expr);
    }

    // Produções com erro.
    if (match(OR)) {
      error(previous(), "Missing left-hand operand.");
      or();
      return null;
    }

    if (match(AND)) {
      error(previous(), "Missing left-hand operand.");
      and();
      return null;
    }

    if (match(BANG_EQUAL, EQUAL)) {
      error(previous(), "Missing left-hand operand.");
      equality();
      return null;
    }

    if (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      error(previous(), "Missing left-hand operand.");
      comparison();
      return null;
    }

    if (match(PLUS)) {
      error(previous(), "Missing left-hand operand.");
      addition();
      return null;
    }

    if (match(SLASH, TIMES, REM)) {
      error(previous(), "Missing left-hand operand.");
      multiplication();
      return null;
    }

    throw error(peek(), "Expect expression.");
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type))  {
        advance();
        return true;
      }
    }

    return false;
  }

  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private ParseError error(Token token, String message) {
    MicroKotlin.error(token, message);
    return new ParseError();
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) return;

      switch (peek().type) {
        case VAR:
        case VAL:
        case IF:
        case DO:
        case WHILE:
        case PRINT:
        case PRINTLN:
          return;
      }

      advance();
    }
  }
}
