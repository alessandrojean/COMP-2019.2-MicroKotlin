package br.edu.ufabc.microkotlin.expr;

import br.edu.ufabc.microkotlin.Token;

public class ExprLogical extends Expr {

  public Expr left;
  public Token operator;
  public Expr right;

  public ExprLogical(Expr left, Token operator, Expr right) {
    this.left = left;
    this.operator = operator;
    this.right = right;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitLogicalExpr(this);
  }

}
