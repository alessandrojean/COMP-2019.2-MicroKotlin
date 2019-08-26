/**
 * Calcula o fatorial de um número.
 */

fun main() {
  print("Digite um número: ");
  var n: Int = readInt();

  if (n < 0) {
    printLn("Não pode número negativo");
  } else {
    var acm: Int = 1;
    var i: Int = 1;
    while (i <= n) {
      acm = acm * i;
      i = i + 1;
    }
    printLn(n + "! = " + acm);
  }
}
