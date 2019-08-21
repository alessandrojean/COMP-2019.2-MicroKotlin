package br.edu.ufabc.microkotlin.expr;

public class ExprGrouping extends Expr {

  public Expr expression;

  public ExprGrouping(Expr expression) {
    this.expression = expression;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitGroupingExpr(this);
  }

}
