# Gramática da linguagem

```
program        → {constant} main
constant       → "val" identifier ":" type ["=" expr] ";"
variable       → "var" identifier ":" type ["=" expr] ";"
type           → "Int" | "String" | "Double" | "Boolean"
   
main           → "fun" "main" "("  ")" "{" {declaration} "}"
   
block          → "{" {declaration} "}"
declaration    → variable
               | statement
statement      → expression ";"
               | "print" ["Ln"] "(" expression ")" ";"
               | "if" "(" expression ")" block ["else" block]
               | "while" "(" expression ")" block
               | "do" block "while" "(" expression ")" ";"
   
expression     → [identifier "="] expression
               | or
   
or             → and ["||" and]
and            → equality ["&&" equality]
equality       → comparison [("!=" | "==") comparison]
comparison     → addition [(">" | ">=" | "<" | "<=") addition]
addition       → multiplication [("-" | "+") multiplication]
multiplication → unary [("/" | "*" | "%") unary]

unary          → [("!" | "-")] primary
primary        → BOOLEAN
               | NUMBER
               | STRING
               | NULL
               | identifier
               | "read" type "(" ")"
               | "(" expression ")"
```

## Estrutura léxica

### Classes de caracteres

```
digit          → '0' .. '9'
letter         → 'A' .. 'Z' | 'a' .. 'z'
alpha          → letter | digit | '_'
```

### Classes terminais

```
identifier     → letter {alpha}

BOOLEAN        → "true" | "false"
NUMBER         → digit {digit} ["." digit {digit}]
STRING         → """ {alpha} """
NULL           → "null"
```

## Comentários

```
// Até o fim da linha.

/*
 * Até encontrar o par fechador.
 * Pode utilizar várias linhas.
 */
```
