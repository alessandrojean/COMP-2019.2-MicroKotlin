package br.edu.ufabc.microkotlin;

import java.util.ArrayList;
import java.util.List;
import br.edu.ufabc.microkotlin.expr.*;
import br.edu.ufabc.microkotlin.program.*;
import br.edu.ufabc.microkotlin.stmt.*;
import static br.edu.ufabc.microkotlin.TokenType.*;

/**
 * Classe responsável pela análise semântica.
 */
public class Parser {

  @SuppressWarnings("serial")
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

  public Program parse() {
    List<StmtVal> constants = new ArrayList<>();
    while (match(VAL)) {
      constants.add((StmtVal) valDeclaration());
    }

    List<Stmt> statements = main();
    return new Program(constants, statements);
  }

  public List<Stmt> main() {
    consume(FUN, "Expect 'fun' after constants.");
    Token main = consume(IDENTIFIER, "Expect function name.");

    if (!main.lexeme.equals("main")) {
      error(main, "Expect the function name to be 'main'.");
    }

    consume(LEFT_PAREN, "Expect '(' after function name.");
    consume(RIGHT_PAREN, "Expect ')' before block.");

    List<Stmt> statements = block();
    return statements;
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
   * Avalia a declaração de variáveis ou constantes.
   *
   * declaration → variable | statement
   */
  private Stmt declaration() {
    try {
      if (match(VAR)) return varDeclaration();

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  /**
   * Avalia o comando.
   *
   * statement → expression ";"
   *           | "print" ["Ln"] "(" expression ")" ";"
   *           | "if" "(" expression ")" block ["else" block]
   *           | "while" "(" expression ")" block
   *           | "do" block "while" "(" expression ")" ";"
   */
  private Stmt statement() {
    if (match(DO)) return doWhileStatement();
    if (match(IF)) return ifStatement();
    if (match(PRINT)) return printStatement();
    if (match(PRINTLN)) return printLnStatement();
    if (match(WHILE)) return whileStatement();
    if (match(LEFT_BRACE)) return new StmtBlock(block());

    return expressionStatement();
  }

  /**
   * Avalia o comando do do-while.
   *
   * doWhileStmt → "do" block "while" "(" expression ")" ";"
   */
  private Stmt doWhileStatement() {
    Stmt body = statement();
    consume(WHILE, "Expect 'while' after block.");
    consume(LEFT_PAREN, "Expect '(' after 'while'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after condition.");
    consume(SEMICOLON, "Expect ';' after do-while.");

    return new StmtDoWhile(condition, body);
  }

  /**
   * Avalia o comando do if.
   *
   * ifStmt → "if" "(" expression ")" block ["else" block]
   */
  private Stmt ifStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after if condition.");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }

    return new StmtIf(condition, thenBranch, elseBranch);
  }

  /**
   * Avalia o comando de impressão.
   *
   * printStmt → "print" ["Ln"] "(" expression ")" ";"
   */
  private Stmt printStatement() {
    consume(LEFT_PAREN, "Expect '(' before expression.");
    Expr value = expression();
    consume(RIGHT_PAREN, "Expect ')' after expression.");
    consume(SEMICOLON, "Expect ';' at end of line.");

    return new StmtPrint(value);
  }

  /**
   * Avalia o comando de impressão com quebra de linha.
   *
   * printStmt → "print" ["Ln"] "(" expression ")" ";"
   */
  private Stmt printLnStatement() {
    consume(LEFT_PAREN, "Expect '(' before expression.");
    Expr value = expression();
    consume(RIGHT_PAREN, "Expect ')' after expression.");
    consume(SEMICOLON, "Expect ';' at end of line.");

    return new StmtPrintLn(value);
  }

  /**
   * Avalia o comando do while.
   *
   * whileStmt → "while" "(" expression ")" block
   */
  private Stmt whileStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'while'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after condition.");
    Stmt body = statement();

    return new StmtWhile(condition, body);
  }

  /**
   * Avaliação da declaração de constante.
   *
   * constant → "val" identifier ":" type ["=" expr] ";"
   */
  private Stmt valDeclaration() {
    Token name = consume(IDENTIFIER, "Expect constant name.");
    consume(COLLON, "Expect ':' after constant name.");
    Token type = consume(IDENTIFIER, "Expect constant type.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new StmtVal(name, type, initializer);
  }

  /**
   * Avaliação da declaração de variável.
   *
   * variable → "var" identifier ":" type ["=" expr] ";"
   */
  private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");
    consume(COLLON, "Expect ':' after variable name.");
    Token type = consume(IDENTIFIER, "Expect variable type.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new StmtVar(name, type, initializer);
  }

  /**
   * Avalia o comando de expressão.
   *
   * exprStmt → expression ";"
   */
  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(SEMICOLON, "Expect ';' after expression.");
    return new StmtExpression(expr);
  }

  /**
   * Avalia o bloco de comandos.
   *
   * block → "{" {declaration} "}"
   */
  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while(!check(RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expect '}' after block.");
    return statements;
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
