package br.edu.ufabc.microkotlin.program;

import java.util.List;
import br.edu.ufabc.microkotlin.stmt.Stmt;
import br.edu.ufabc.microkotlin.stmt.StmtVal;

public class Program {

  public static interface Visitor<R> {
    R visitProgram(Program program);
  }

  public final List<StmtVal> constants;
  public final List<Stmt> statements;

  public Program(List<StmtVal> constants, List<Stmt> statements) {
    this.constants = constants;
    this.statements = statements;
  }

  public <R> R accept(Visitor<R> visitor) {
    return visitor.visitProgram(this);
  }

}
