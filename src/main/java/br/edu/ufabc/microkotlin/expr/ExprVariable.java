package br.edu.ufabc.microkotlin.expr;

import br.edu.ufabc.microkotlin.Token;

public class ExprVariable extends Expr {

  public Token name;

  public ExprVariable(Token name) {
    this.name = name;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitVariableExpr(this);
  }

}
