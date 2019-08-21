package br.edu.ufabc.microkotlin.expr;

public class ExprRead extends Expr {

  public String type;

  public ExprRead(String type) {
    this.type = type;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitReadExpr(this);
  }

}
