package br.edu.ufabc.microkotlin.expr;

import br.edu.ufabc.microkotlin.Token;

public class ExprUnary extends Expr {

  public Token operator;
  public Expr right;

  public ExprUnary(Token operator, Expr right) {
    this.operator = operator;
    this.right = right;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitUnaryExpr(this);
  }

}
