package br.edu.ufabc.microkotlin;

/**
 * Representa uma variável ou constante.
 */
public class Variable {

  /**
   * Possíveis tipos de variáveis.
   */
  public static enum VariableProperty {
    VARIABLE, CONSTANT
  }

  /**
   * Propriedade da variável.
   */
  public VariableProperty property;

  /**
   * Valor da variável.
   */
  public Object value;

  public Variable(VariableProperty property, Object value) {
    this.property = property;
    this.value = value;
  }

}
