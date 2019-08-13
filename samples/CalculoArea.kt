/**
 * Programa de exemplo para a linguagem MicroKotlin.
 * Em teoria, este exemplo não deve gerar nenhum erro.
 */

/**
 * Representa o valor da constante pi.
 */
val PI: Double = 3.14;

/**
 * Função principal da aplicação, que será
 * executada assim que o programa iniciar.
 */
fun main() {
  // Inicialização de variáveis.
  var raio: Int = 0;
  var maisUm: Boolean = true;

  // Título
  printLn("+----------------------------+");
  printLn("| Cálculo de área do círculo |");
  printLn("+----------------------------+");
  printLn();

  while (maisUm) {
    print("Digite o raio: ");
    raio = readInt();

    printLn("A área é " + (PI * raio * raio));
    printLn();

    printLn("Deseja continuar? (true/false) ");
    maisUm = readBoolean();
  }
}
