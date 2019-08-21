package br.edu.ufabc.microkotlin.stmt;

import br.edu.ufabc.microkotlin.expr.Expr;

public class StmtWhile extends Stmt {

  public final Expr condition;
  public final Stmt body;

  public StmtWhile(Expr condition, Stmt body) {
    this.condition = condition;
    this.body = body;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitWhileStmt(this);
  }

}
