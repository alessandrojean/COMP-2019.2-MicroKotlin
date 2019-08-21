package br.edu.ufabc.microkotlin.stmt;

import java.util.List;

public class StmtBlock extends Stmt {

  public final List<Stmt> statements;

  public StmtBlock(List<Stmt> statements) {
    this.statements = statements;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitBlockStmt(this);
  }

}
