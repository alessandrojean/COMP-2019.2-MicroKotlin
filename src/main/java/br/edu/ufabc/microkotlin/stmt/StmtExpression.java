package br.edu.ufabc.microkotlin.stmt;

import br.edu.ufabc.microkotlin.expr.Expr;

public class StmtExpression extends Stmt {

  public final Expr expression;

  public StmtExpression(Expr expression) {
    this.expression = expression;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitExpressionStmt(this);
  }

}
