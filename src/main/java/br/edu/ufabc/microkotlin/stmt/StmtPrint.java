package br.edu.ufabc.microkotlin.stmt;

import br.edu.ufabc.microkotlin.expr.Expr;

public class StmtPrint extends Stmt {

  public final Expr expression;

  public StmtPrint(Expr expression) {
    this.expression = expression;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitPrintStmt(this);
  }

}
