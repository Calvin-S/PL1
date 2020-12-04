# Vision
We attempting to implement a fully working language based off of our own designs. We will start from pretty much scratch and build a lexer, parser, and evaluater for this language with very little starter code (the exceptions are the tokenizer and look-ahead-buffer). Currently our language implementation is a true blend of imperative and functional programming languages. All expressions return some value, meaning variables can be assigned to expressions such as If-statements, While-loops (Note higher order functions do not exist as of the current implementation). Our language also acts imperatively in the sense that expressions and assignments do change the program's state as there is no scoping within loops (aka everything acts as a global variable). 

## Core features:
    - While Loops
    - Variables - ints, strings, booleans, expressions
    - String Manipulation - substring, reverse, concatenation
    - Ability to call and define functions
    - Dynamically typed variables
### Grammar Rules
It should be noted that the grammar is not accurate when it comes to var, as the interpeter will type check valid operations. E.g. a variable could be 3, and a BExpr such as 3 or T makes no sense. It should be noted that the syntax roughly follows this grammar tree. 

Types
```
| Var (var)
| Int (int)
| String (str)
| Bool (b)
| Null
```
Seq
```
| Expr
| Seq Expr
```
Expr
```
| if (BExpr) {Seq} 
| if (BExpr) {Seq} Elif
| while (BExpr) {Seq}
| $[var_name]
| $[var_name] = Expr
```
Elif
```
| elif (BExpr) {Seq} Elif
| else {Seq}
```
BExpr
```
| T
| F
| AExpr
| $[var_name]
| not BExpr
| BExpr or BExpr
| BExpr andBExpr
| AExpr > AExpr
| AExpr < AExpr
| AExpr == AExpr
| AExpr != AExpr
```
AExpr
```
| int
| $[var_name]
| AExpr * AExpr
| AExpr / AExpr
| AExpr + AExpr
| AExpr - AExpr
```

### Expression as functions
Because every expression acts like a function (aka returns some value), we can even assign expressions to variables. So we will first cover variable syntax:
Variable declaration (this variable will automatically be equal to null)
```
$[var_name];
```
Variable instantation
```
$[var_name] = Expr
```
Getting Variable value
```
$[var_name]
```

Examples shown below with the arrow denoting the interpreted returned value:
```
$a = 0 * 2 + 3 --> 1
$b = if (T) {2 + 3}  --> 5
$c = if (F) {T} --> null
$d = while ($a > 0) {$a = $a - 1} --> 0
$e = while (F) {$a = $a + 1} --> null
``` 


### Right Associativity
Our parser parses binary operators right associatively if the order precedences are the same. Boolean operators have no order precedence yet. Multiplication and division have higher precedence than addition and subtraction.

Examples of how our language interprets is shown below:
```
not T or F --> not (T or F)
T and (F or T) and F --> T and ((F or T) and F)
4 + 2 - 3 + 1 --> 4 + (2 - (3 + 1))
3 * 2 + 4 * 5 - 2 - 1 --> (3 * 2) + ((4 * 5) - (2 - 1))
```
In our language, T is for true and F for false.

# Status
Some examples of how files are parsed (including incorrect grammar) are shown below. Note the parenthesis is merely a substitute for the actual AST representation. Parsing and Interpreting for basic operations and basic errors:
```
T and (1 + 3) == 4 + 3 --> true and ((1 + 3) == (4 + 3))
```
```
3 * 4 + 2 - 1 + 0 --> 3 * (4 + (2 - (1 + 0)))
```
```
2 * 2 * 2 + 2 * 2 * 2 --> ((2 * (2 * 2)) + (2 * (2 * 2))
```
```
3 + T --> Parser.SyntaxError: Assigning Arithmetic Values failed on line 1
```
```
F or T) --> Parser.SyntaxError: Parenthesis Mismatch
```

Since project alpha, we have expanded our lexer and parser quite a bit, and if statements, while loops, basic expressions all work as well. More examples are listed below:
## Example 1:
```
$a = T
$b = if ($a) {3}
$c = "ASDF"
```
The Store evaluates to
```
a : true
b : 3
c : ASDF
```
The Program evaluates to

    ASDF
## Example 2:
Since expressions return values, the last line really evaluates as $c = 2 first, which results in $a = $b = 2. This repeats leaving $a = 2 and finally returning 2.
```
$c;
$d;
$c = 1
$a = $b = $c = 2
```
The Store evaluates to 
```
a : 2
b : 2
c : 2
d : NULL
```
The Program evaluates to 

    2
## Example 3:
Because our grammar is very structured, newlines, spaces, semicolons don't actually affect our parsing and interpreting. Thus the program:
```
34 + 20
$c = 1
$a = $b = F or ($c == 2)
T or F
```
is equivalent to
```
34 + 20;
$c = 1;;;
$a = $b = F or ($c == 2);;;
T or F;;;;;
```
which is equivalent to
```
34 + 20 $c = 1;$a = $b = F or ($c == 2)
T or F
```
This Store evaluates to 
```
a : false
b : false
c : 1
```
The program evaluates to

    true
## Example 4: 
Note that currently if statement branches can return different types:
```
$a = T
$b = if (3 < 1) {F}
elif (not $a) {0}
elif (F) {2}
elif ((1 != 2) and 2 == 2) {"hi"}
else {"no"}
```
The Store evaluates to:
```
a : true
b : hi
```
The Program evaulates to: 

    hi

## Example 5: 
Programs with multiple expressions will evaluate to the last expression:
```
$a = $b = $d = 2
$c;
while ($a < 10) {
$b = 0 - $a 
$a = $a + 1}
$c
```
The Store evaluates to: 
```
a : 10
b : -9
c : NULL
d : 2
```
The Program evaluates to:

    null

## Error (Types Mismatch):
Since our variables are dynamically typed, there are type errors when interpeting types that don't match:
```
$a = T
3 + $a
```
Throws the error

    trying to add one or more things that are not ints
Similarly,
```
$a = T
$c = 2
$d = $b = $c
$e = $a and $d
```
Throws the error
    trying to and one or more things that are not bools
## Error (Syntax on If/While):
If statements all need parenthesis for the guard and brackets for the body:
```
$a = 30
if (T) {$a
```
Throws the error

    If statement body needs a closing bracket
and the program
```
if (T or F {"hi"}
```
Throws the error

    If guard missing closing parenthesis
Similarly for while loops:
```
$a = 30
while ($a < 0) {$a = $a - 1
```
Throws the error

    While statement body needs closing brackets
and
```
$a = 30
while (F {$a}
```
Throws the error

    While statement guard needs closing parenthesis


# Next Steps
Our language is still missing out a few major features such as being able to call functions possibly with parameters to pass in, so that will probably be our next main goal. Some other small things we have not implemented is string manipulation operators such as concatenation or substrings. After those two extensions, we will try to attempt more complicated topics such as polymorphism or perhaps higher ordered functions, although specifics will have to depend on how functions are implemented.