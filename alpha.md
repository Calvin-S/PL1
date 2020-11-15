# Vision
We attempting to implement a fully working language based off of our own designs. We will start from pretty much scratch and build a lexer, parser, and evaluater for this language. General expressions such as If-statements, While-loops, basic arithmetic operations, string manipulation, etc. will be included in our language. 

## Core features:
    For and While Loops
    Variables - ints, strings, booleans
    String Manipulation - substring, reverse, concatenation
    Ability to call and define functions

One interesting feature of our language is that expressions will go all the way to the right like it does in lambda calculus. An example of how our language interprets is shown below:
```
not T or F --> not (T or F)
4 + 2 - 3 + 1 --> 4 + (2 - (3 + 1))
```
T is for true and F for false.

# Status
Currently we have a basic lexer and parser working for integers and booleans, including basic operations. Some examples of how files are parsed (including incorrect grammar) are shown below. Note the bracket is merely a substitute for the actual AST representation:
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
