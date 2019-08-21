package br.edu.ufabc.microkotlin.expr;

import br.edu.ufabc.microkotlin.Token;

public class ExprAssign extends Expr {

  public Token name;
  public Expr value;

  public ExprAssign(Token name, Expr value) {
    this.name = name;
    this.value = value;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitAssignExpr(this);
  }

}
