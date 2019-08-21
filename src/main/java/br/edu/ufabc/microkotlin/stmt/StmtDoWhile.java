package br.edu.ufabc.microkotlin.stmt;

import br.edu.ufabc.microkotlin.expr.Expr;

public class StmtDoWhile extends Stmt {

  public final Expr condition;
  public final Stmt body;

  public StmtDoWhile(Expr condition, Stmt body) {
    this.condition = condition;
    this.body = body;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitDoWhileStmt(this);
  }

}
