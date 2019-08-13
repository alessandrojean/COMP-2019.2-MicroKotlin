package br.edu.ufabc.microkotlin;

/**
 * Representa um token no código-fonte.
 */
public class Token {
  /**
   * Tipo do token.
   */
  final TokenType type;

  /**
   * Conteúdo do token.
   */
  final String lexeme;

  /**
   * Valor do token, para os literais.
   */
  final Object value;

  /**
   * Linha de origem.
   */
  final int line;

  public Token(TokenType type, String lexeme, Object value, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.value = value;
    this.line = line;
  }

  @Override
  public String toString() {
    return type + " " + lexeme + " " + value;
  }
}
