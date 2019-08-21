package br.edu.ufabc.microkotlin.stmt;

import br.edu.ufabc.microkotlin.expr.Expr;

public class StmtPrintLn extends Stmt {

  public final Expr expression;

  public StmtPrintLn(Expr expression) {
    this.expression = expression;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitPrintLnStmt(this);
  }

}
