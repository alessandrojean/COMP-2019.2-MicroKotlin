/**
 * Programa que exibe os n primeiros números da sequência de Fibonacci.
 */

fun main() {
  print("Digite quantos números quer: ");
  var n: Int = readInt();

  if (n == 1) {
    print("1");
  } else if (n == 2) {
    print("1, 1");
  } else {
    var a: Int = 1;
    var b: Int = 1;
    var i: Int = 0;

    while (i < n) {
      if (i == n - 1) {
        print(a);
      } else {
        print(a + ", ");
      }
      var c: Int = a + b;
      a = b;
      b = c;
      i = i + 1;
    }
  }

  printLn("");
}
