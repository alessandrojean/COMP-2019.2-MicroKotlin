# Guia de contribuição

Por favor, siga este guia quando for efetuar contribuições ao repositório.

## Ambiente de desenvolvimento Java

O projeto utiliza o [Gradle] como gerenciador de compilação e tarefas,
portanto, certifique-se de que você instalou corretamente o mesmo e configurou
a variável de ambiente `GRADLE_HOME` corretamente. No Linux, isso pode ser
feito editando o `~/.bashrc` e adicionando a seguinte linha:

```bash
export GRADLE_HOME=~/.gradle
```

Após isso, reinicie a sessão para as alterações surgirem efeito.

[Gradle]: https://gradle.org/

## Utilizando o VSCode

Para melhor produtividade, **utilize** o [Visual Studio Code]
com as extensões [Java Extension Pack] e [EditorConfig].

[Visual Studio Code]: https://code.visualstudio.com/
[Java Extension Pack]: https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack
[EditorConfig]: https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig

## Formatando o código

**Sempre** formate o código. Isto pode ser feito através do atalho
<kbd>Ctrl + Shift + I</kbd> no VSCode com a extensão do Java executando
corretamente. Também evite ultrapassar mais de 80 caracteres por linha.
Para facilitar saber disto, pode-se editar as configurações do VSCode
para mostrar uma régua visual para orientação através da seguinte
propriedade.

```json
"editor.rulers": [80],
```

Em um geral, **siga** o estilo de código do [Google] para Java que é bem
abrangente em vários casos. Isto pode ser feito configurando o formatador
através da seguinte propriedade.

```json
"java.format.settings.url": "https://google.github.io/styleguide/eclipse-java-google-style.xml",
"java.format.settings.profile": "GoogleStyle",
```

[Google]: https://google.github.io/styleguide/javaguide.html

## Gerenciamento do Git

Em geral, siga este [guia] para efetuar os *commits* e criar os *Pull Requests*.

[guia]: https://gist.github.com/alessandrojean/4c4cc7e36f48d45cfdc508ae31bf870c
