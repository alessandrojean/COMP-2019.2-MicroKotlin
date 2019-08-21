package br.edu.ufabc.microkotlin.expr;

public class ExprLiteral extends Expr {

  public Object value;

  public ExprLiteral(Object value) {
    this.value = value;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitLiteralExpr(this);
  }

}
