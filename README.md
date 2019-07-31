# MicroKotlin

Projeto final da disciplina de Compiladores realizada em 2019.2 na UFABC.

## Enunciado

Cada grupo deve definir a sua própria gramática e os *tokens* necessários.
Os requisitos **mínimos** são:

- Deve ter 2 tipos de variáveis;
- Deve ter a estrutura de controle `if ... else`;
- Deve ter a estrutura de repetição `while` e `do ... while`;
- A parte de expressões envolvendo os operadores matemáticos deve ser
  realizada de maneira correta, respeitado a precedência;
- As atribuições também devem ser realizadas. Não é necessário verificar
  se é possível realizar as operações, devido aos tipos das variáveis;
- Os comandos de leitura do teclado e de impressão na tela devem ser
  disponibilizados.
- O compilador tem que aceitar números decimais.
- A cada utilização de uma variável, é necessário verificar se a mesma
  já foi declarada.

O compilador deve fazer a conversão de um programa desenvolvido na
linguagem definida pelo grupo para a linguagem C ou Java.

A verificação da corretude do programa será realizada compilando o
arquivo gerado pelo compilador desenvolvido.

Seu compilador deverá receber como entrada um arquivo contendo um
programa escrito na linguagem definida pelo grupo e gerar uma forma
equivalente em C/C++ ou Java, que deverá ser compilada em um
compilador qualquer (Turbo C, MS-Visual C, Gcc, javac) e não deverá
conter erros.

**OBS:** A gramática não pode conter recursividade à esquerda e produções
vazias (que porventura venham a surgir). Caso seja necessário, efetue
sua fatoração à esquerda.

## Executando

Certifique-se de possuir o [Gradle] instalado na sua máquina primeiramente.
Para executar, utilize os comandos abaixo.

```console
Para executar o transpilador.
$ gradle transpile --args='<arquivo-de-entrada>'
```

[Gradle]: https://gradle.org/

## Contribuindo

Este repositório possui um guia de contribuição disponível [aqui].
Por favor, **siga-o estritamente**.

[aqui]: CONTRIBUTING.md

## Licença

> Você pode checar a licença completa [aqui](LICENSE).

Este repositório está licenciado pelos termos da licença **MIT**.
