# Our language
We attempting to implement a fully working language based off of our own designs. We will start from pretty much scratch and build a lexer, parser, and evaluater for this language. Our language implementation is a true blend of imperative and functional programming languages. All statements such as If-statements and While-loops are considered expressions, and thus these statements can be assigned to variables. 

## Core features:
    - Dynamically typed variables
    - Types: ints, strings, booleans, lists
    - Conditional expressions: type matching, while loops, if-statements, etc.
    - Ability to call and define functions
# Demo Code
## Fibonacci Sequence:
```
// Hi, this is a single-line comment. Fibonacci is cool!
fun fibb ($a)
   {if ($a < 1) {0} 
   elif ($a <= 2) {1} 
   else { @fibb($a - 1) + @fibb($a - 2)}}
$a = 13
@fibb($a)
```
This programs evaluates to:
```
233
```
which is the correct value for the 13th number in the Fibonacci sequence.

## Palindrome:
```
fun isPalindrome($s) 
{$s == (~$s)} 
$e = "Hello World!"
$e = $e ^ (~$e) 
// ^ is the symbol for concatenation. ~ is the symbol for string reversal
$d = [@isPalindrome("noon"), @isPalindrome("hii"), @isPalindrome($e)]
```
This program evaluates to:
```
[true, false, true]
```

## 
# Syntax
 Note the parenthesis is merely a substitute for the actual AST representation. It should also be noted that parser parses binary operators right associatively if order precedences are the same. Boolean operators have no order precedence. Basic arithmetic and boolean expression along with common errors during parsing and then evaluation shown below.
```
not T or F --> not (true or false) --> false
```
```
T and (1 + 3) == 3 --> true and ((1 + 3) == 3) --> false
```
```
3 * 4 + 2 - 1 + 0 --> 3 * (4 + (2 - (1 + 0))) --> 13
```
```
2 * 2 * 2 + 2 * 2 * 2 --> ((2 * (2 * 2)) + (2 * (2 * 2)) --> 16
```
```
3 + T --> Parser.SyntaxError: Assigning Arithmetic Values failed on line 1
```
```
F or T) --> Parser.SyntaxError: Parenthesis Mismatch
```

A final note should be made that the syntax follows a rigid grammar, and thus separators for expressions can simply be whitespace or semicolon. For example all the below lines parse equivalently:
```
$a = 1 $b = 2
```
```
$a=1;$b=2;
```
```
$a=1
$b=2
```
## Variable Assignment:
```
$a = T
$b = if ($a) {3}
var c = "ASDF"
var d = 3 + 2
$e = $f = 0
```
The Store evaluates to
```
a : true
b : 3
c : ASDF
d : 5
e : 0
f : 0
```
The Program evaluates to

    0
## If statements:
### Example 1
```
$c = if (T or F) {$a = 3 $b = 5}
```
The Store evaluates to 
```
a : 3
b : 5
c : 5
```
The Program evaluates to 

    5
### Example 2
```
$a = 42
if (T and F) {$a = 3 $b = 5}
elif (F) {$d = "hi"}
elif (T) {"I am here"}
else {$a}
```
The Store evaluates to 
```
a : 42
```
The Program evaluates to 

    I am here

## While loops:
### Example 1
```
$a = "hi"
$c = while ($a == "b") {$a = 3 $b = 5}
```
The Store evaluates to 
```
a : hi
c : NULL
```
The Program evaluates to 

    NULL
### Example 2
```
$a = 0
$c = "hi"
while ($a < 3) {
$c = $c ^ $c
$a = $a + 1
// ^ is string concatenation
}
$c
```
The Store evaluates to 
```
a : 3
c : hihihihihihihihi
```
The Program evaluates to 

    hihihihihihihihi
## Functions and Calling Functions:
### Example 1
```
fun double ($a) {$a = $a * 2}
fun mul ($a, $b) {$a = $a * $b}
$a = 2
$b = @double($a)
$c = @mul($a,$b)
@double(@mul($a, $b))
```
The Store evaluates to 
```
a : 2
b : 4
c : 8
```
The Program evaluates to 

    16
### Example 2
```
fun doubleList ($list) { 
	$a = 0
	while ($a < size($list)) {
		$b = 2*get($list,$a)
		replace($list, $b, $a)
		$a = $a + 1
		$list
	}
}
@doubleList ([1,2,3,4,5,6])
```
The Store is empty.

The Program evaluates to 

    [2, 4, 6, 8, 10, 12]
## Global Variables:
To declare a global variable 'x'. We can do as shown below (both are syntactically equivalent), we can have a global variable even in functions:
```
$x.
```
or
```
var x.
```
### Example 1
```
fun add($b) {$x = $x + $b}
var x.
$x = 0
@add(3)
$x
```
The (Global) Store evaluates to 
```
x : 3
```
The Program evaluates to 

    3
### Example 2
```
fun doubleList ($list) { 
	$a = 0
	while ($a < size($list)) {
		$b = 2*get($list,$a)
		replace($list, $b, $a)
		$a = $a + 1
		$list
	}
}
@doubleList ([1,2,3,4,5,6])
```
The Store is empty.

The Program evaluates to 

    [2, 4, 6, 8, 10, 12]
### Example 3
Our language does not support function arguments sharing names with global variables.
```
fun add($x) {$x = $x + $b}
var x.
$x = 0
@add(3)
$x
```
This will throw the error:
```
interpreter.EvaluationError: The function's parameters overlap with existing global variables
```
Similarly:
```
fun add($x) {$x. $x = $x + $b}
$x = 0
@add(3)
$x
```
Throws the same error:
```
interpreter.EvaluationError: The function's parameters overlap with existing global variables
```

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