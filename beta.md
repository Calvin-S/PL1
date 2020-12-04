# Vision
We attempting to implement a fully working language based off of our own designs. We will start from pretty much scratch and build a lexer, parser, and evaluater for this language with very little starter code (the exceptions are the tokenizer and look-ahead-buffer). Currently our language implementation is a true blend of imperative and functional programming languages. All expressions return some value, meaning variables can be assigned to expressions such as If-statements, While-loops (Note higher order functions do not exist as of the current implementation). Our language also acts imperatively in the sense that expressions and assignments do change the program's state as there is no scoping within loops (aka everything acts as a global variable).

## Core features:
    - For and While Loops
    - Variables - ints, strings, booleans, expressions
    - String Manipulation - substring, reverse, concatenation
    - Ability to call and define functions


### Right Associativity
Our parser parses binary operators right associatively if the order precedences are the same. Examples of how our language interprets is shown below:
```
not T or F --> not (T or F)

4 + 2 - 3 + 1 --> 4 + (2 - (3 + 1))
3 * 2 + 4 * 5 - 2 - 1 --> (3 * 2) + ((4 * 5) - (2 - 1))

```
In our language, T is for true and F for false.

# Status
Currently we have a basic lexer and parser working for integers and booleans, including basic operations. Some examples of how files are parsed (including incorrect grammar) are shown below. Note the parenthesis is merely a substitute for the actual AST representation:
```
T and (1 + 3) == 4 + 3 --> true and ((1 + 3) == (4 + 3))
```
```
3 * 4 + 2 - 1 + 0 --> 3 * (4 + (2 - (1 + 0)))
```
```
3 + T --> Parser.SyntaxError: Assigning Arithmetic Values failed on line 1
```
```
F or T) --> Parser.SyntaxError: Parenthesis Mismatch
```
# Next Steps
We plan to extend our lexer and parser to start taking in strings, variables, and functions. Once the lexer and parser works correctly through extensive testing, we will move on to evaluating the AST that we have generated. If we have time, we will develop a type checker for our language.