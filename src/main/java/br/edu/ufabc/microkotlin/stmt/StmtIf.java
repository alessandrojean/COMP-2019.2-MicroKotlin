package br.edu.ufabc.microkotlin.stmt;

import br.edu.ufabc.microkotlin.expr.Expr;

public class StmtIf extends Stmt {

  public final Expr condition;
  public final Stmt thenBranch;
  public final Stmt elseBranch;

  public StmtIf(Expr condition, Stmt thenBranch, Stmt elseBranch) {
    this.condition = condition;
    this.thenBranch = thenBranch;
    this.elseBranch = elseBranch;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitIfStmt(this);
  }

}
