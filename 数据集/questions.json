[
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "No keywords in Java start with a capital letter.",
        "id": "1",
        "line": "All Java keywords are written in lower case."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "Method and variable names can't start with a number, although they can contain numbers. myMethod2() is legal; 2myMethod() is not.",
        "id": "2",
        "line": "A method name can start with a number."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Methods and variables can start with a $ character, but its not recommended.",
        "id": "3",
        "line": "A variable name can start with $."
    },
    {
        "answer": "true, false",
        "bogus": [
            "true, false, 1,0",
            "TRUE, FALSE",
            "TRUE, FALSE, true, false",
            "1,0"
        ],
        "explanation": "Unlike C/C++, booleans in Java are represented with the literals true and false. You can't substitute integers such as 1 and 0 where a boolean value is expected.",
        "id": "4",
        "line": "Which of these are legal values for a boolean?"
    },
    {
        "answer": "16 bits",
        "bogus": [
            "8 bits",
            "7 bits",
            "32 bits"
        ],
        "explanation": "Chars in Java are all 16-bit Unicode.",
        "id": "5",
        "line": "What's the bit-depth of a char?"
    },
    {
        "answer": "8 bits",
        "bogus": [
            "7 bits"
        ],
        "explanation": "Bytes are always 8 bits. The left-most bit is used to represent the sign.",
        "id": "6",
        "line": "What is the bit-depth of a byte?"
    },
    {
        "answer": "double",
        "bogus": [
            "float",
            "Double",
            "Float"
        ],
        "explanation": "Floating point literals (example: 2.27) in Java are automatically typed as doubles unless you add an \"f\" (example: 2.27f).",
        "id": "7",
        "line": "A floating point literal in Java is treated as a:"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "An array is an object, and Java always passes a copy of the reference to the original object. Both references now point to the same array object.",
        "id": "8",
        "line": "When you pass a reference to an array as an argument to a method call, you pass a new reference to the original array."
    },
    {
        "answer": "Not always",
        "bogus": [
            "Yes always"
        ],
        "explanation": "Think of a call to System.gc( ) as a... suggestion. Garbage collection may or may not actually occur.",
        "id": "9",
        "line": "Does a call to System.gc() force garbage collection to take place? (assuming there are objects to be collected)"
    },
    {
        "answer": "~",
        "bogus": [
            "!",
            "-"
        ],
        "explanation": "The ~ operator performs a bitwise inversion (flips the bits!).",
        "id": "10",
        "line": "Which of these operators would cause the bit pattern 00111100 to become 11000011?"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "This test asks if the expression (y is greater than 2) is NOT true... and that's false. y IS greater than 2, so the result is false.",
        "id": "11",
        "line": "Assume x=2 and y=4. What is the result of (!(y>x))"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "The test asks if the primitive values of y and x are NOT equal in value, which is true.",
        "id": "12",
        "line": "Assume x=6 and y=7. What is the result of (!(y==x))"
    },
    {
        "answer": "compiler error",
        "bogus": [
            "c == 5",
            "runtime exception ",
            "c = 3 "
        ],
        "explanation": "The result of arithmetic is always at LEAST an int, so the compiler will ask for an explicit cast: byte c = (byte) a + b; even though a and b are bytes.",
        "id": "13",
        "line": "Assume: byte a=3; byte b=2; What is the result of: byte c=a+b;"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Not in Java. A boolean has a value of true or false, not 1 and 0, or 0 and anything else. You just can't do it (watch out C folks)",
        "id": "14",
        "line": "Can you compare a boolean to an integer?"
    },
    {
        "answer": "an operator",
        "bogus": [
            "a method in Object"
        ],
        "explanation": "The \"instanceof\" operator -- if ( A instanceof B) -- tests if an object A is either an instance of B, an instance of a subclass of B, or implements the B interface.",
        "id": "15",
        "line": "What is \"instanceof\" ?"
    },
    {
        "answer": "false",
        "bogus": [
            "true",
            "compiler error",
            "runtime exception"
        ],
        "explanation": "The == operator looks ONLY for a match of bits. For two different objects, the only way == can be true is if both reference variables point to the SAME object.",
        "id": "16",
        "line": "Integer a = new Integer(5); Integer b = new Integer(5); What is the result of running: if (a==b)"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "If a class was private... who could use it? For a normal, top-level class, the only access modifiers that make sense are public and default (package). Only nested inner classes can be private.",
        "id": "17",
        "line": "Can a top-level class be marked as private?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "A top-level class can ONLY be marked public or default (package). The protected access is just for member variables and methods, and allows subclasses outside the superclass package to inherit the protected members. ",
        "id": "18",
        "line": "Can a top-level class be marked as protected?"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Protected members (methods and data) can be inherited by subclasses, including those outside the package. The only difference between default (no modifier) and protected, is that protected extends visibility outside the package... but ONLY to subclasses.",
        "id": "19",
        "line": "Methods which are marked protected can be called on any subclass of the class in which the method is declared."
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Trick question! Private methods can NOT be overriden, but they CAN be re-declared/redefined in the subclass, and called directly on the subclass object. But polymorphism will not apply.",
        "id": "20",
        "line": "Can a private method of a superclass be declared within a subclass?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Unlike interfaces, abstract classes CAN provide implementations of methods.",
        "id": "21",
        "line": "Can an abstract class have non-abstract methods?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Even a single abstract method tells the compiler that the whole class MUST be declared abstract.",
        "id": "22",
        "line": "Can a non-abstract class have abstract methods?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Any concrete (non-abstract) class MUST provide bodies (implementations) for the abstract methods it inherits. Otherwise, the compiler will insist that the new subclass itself be marked as abstract.",
        "id": "23",
        "line": "If a non-abstract class X inherits abstract methods from its abstract parent class Y, is the X class required to implement the inherited abstract methods?"
    },
    {
        "answer": "Unless A is abstract",
        "bogus": [
            "Under ALL circumstances"
        ],
        "explanation": "If a class is abstract, and it implements an interface, it can leave the interface implementations up to a concrete (non-abstract) subclass.",
        "id": "24",
        "line": "If a class declares that it implements an interface (class A implements B), must class A implement the methods of interface B?"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The result of an arithmetic operation on any two primitive integer operands will be at least an int -- even if the operands are byte and short. ",
        "id": "25",
        "line": "TRUE or FALSE - during arithmetic, when the operands are of different types, the resulting type is ALWAYS the widest of the two types."
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "You can never have a reference type of a subclass refer to an actual instance of the superclass. Because an object of type X (the superclass) wouldn't have the new methods that an object of type Y (the subclass) is supposed to have.",
        "id": "26",
        "line": "Class Y is a subclass of class X. Will this compile? Y myY = new X( );"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "A reference variable can always refer to any object which is a subclass of the reference type, since the instance methods of the reference variable type (X) are guaranteed to be callable on the actual subclass object (class Y).",
        "id": "27",
        "line": "Class Y is a subclass of class X. Will this compile? X myX = new Y( );"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "If you do not use curly braces for an if block, then only the first statement following the if ( ) will run if the if condition is true.",
        "id": "28",
        "line": "Can you compile an if construct without using curly braces?"
    },
    {
        "answer": "init statement",
        "bogus": [
            "boolean test",
            "iteration expression"
        ],
        "explanation": "The init statement must be the first part of the for declaration -- example: for (int i = 7;;)",
        "id": "29",
        "line": "What goes in the first part of a for loop?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "You can declare or initialize more than one variable in a for loop, but you can't mix declarations with initializations.",
        "id": "30",
        "line": "Is this legal? int i; for (i = 4, int t = 6;i < 0; i++)"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "Continue really means \"continue with the next iteration\", so execution of the current iteration stops, and execution continues at the next iteration.",
        "id": "31",
        "line": "In a for loop, the continue statement will cause execution to jump out of the loop and continue on at the first statement below the loop."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "An inner class can be defined inside a method, or even inside a parameter to a method! (with anonymous inner classes)",
        "id": "32",
        "line": "An inner class must be defined within the opening and closing braces of a class, but outside any method -- just like data members."
    },
    {
        "answer": "anonymous",
        "bogus": [
            "static",
            "illegal"
        ],
        "explanation": "A class defined within a method parameter (typically used with event listener classes) is considered anonymous. The object is created and alive, but the new class type has no name, so you can't create more instances of it except by going through that same method.",
        "id": "33",
        "line": "A class defined within a parameter to a method is:"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Remember the rule: the parents must exist before the children, and this goes all the way up the ancestral tree.",
        "id": "34",
        "line": "When an object is created using new (), its constructor will not complete until all constructors higher on the inheritance tree have completed."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The compiler will ONLY insert a default constructor if you don't provide any constructor at all.",
        "id": "35",
        "line": "The compiler will always insert a default, no-argument constructor into any class definition."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "This rule only applies to \"checked\" exceptions -- the exceptions which are subclasses of Exception but not subclasses of RuntimeException.",
        "id": "36",
        "line": "When you override a method, the overriding method in the subclass must not declare ANY new exceptions not declared by the overridden method."
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Without the 'f\", Java will consider a floating point number to be a double, and the compiler will force you to do an explicit cast.",
        "id": "37",
        "line": "Will this compile? float f = 27.24f;"
    },
    {
        "answer": "a double",
        "bogus": [
            "a float"
        ],
        "explanation": "All floating point literals are assumed to be doubles, not floats!",
        "id": "38",
        "line": "As a literal, what is 27.24?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "In Java you must create the array using: int[]x = new int[27] or by explicitly filling in the values: int[ ]={4,2,7,3,9,8};",
        "id": "39",
        "line": "Will this compile? int[ ]x=[27];"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "All array elements are ALWAYS given default values, whether the array is a class, instance, or local variable. Primitives initialize to zero, booleans to false, chars to '\u0000' and object references to null.",
        "id": "40",
        "line": "When an array is constructed, are its elements automatically initialized?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "This instantiation/initialization is allowed as a shortcut to creating a new array and assigning a value for each index.",
        "id": "41",
        "line": "Is this legal? int x[ ] = {2,7,4,9};"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "You can call the command-line argument variable whatever you like! And you can put the brackets [] before or after the variable name.",
        "id": "42",
        "line": "Is this legal? public static void main (String [ ] fred) "
    },
    {
        "answer": "3",
        "bogus": [
            "4",
            "7"
        ],
        "explanation": "X gets the value of y, THEN y is incremented. The result would have been 4 if the expression was: x = ++y;",
        "id": "43",
        "line": "Assume x=0 and y=3. What is the value of x after: x=y++;"
    },
    {
        "answer": "x > y",
        "bogus": [
            "x < y",
            "x == y"
        ],
        "explanation": "Y is assigned to x and THEN y is decremented",
        "id": "44",
        "line": "Given x=y--; Which of these will be true AFTER execution?"
    },
    {
        "answer": "int - 32 bits",
        "bogus": [
            "short - 16 bits",
            "int - 16 bits",
            "byte - 8 bits",
            "int - 64 bits"
        ],
        "explanation": "integer literals are always treated as 32-bit ints",
        "id": "45",
        "line": "Integer literals are always what size?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "Never",
            "With explicit cast"
        ],
        "explanation": "You don't need an explicit cast to use the increment unary operator.",
        "id": "46",
        "line": "Can you use an increment unary operator on a byte? (example: byte b=4; b++;)"
    },
    {
        "answer": "Runtime exception",
        "bogus": [
            "Compiler error",
            "Compiles and runs"
        ],
        "explanation": "At runtime you will get an ArithmeticException which is not checked for at compile time. Floats and doubles will NOT throw an exception if divided by 0.",
        "id": "47",
        "line": "What happens when you divide an integer by a variable with a value of 0?"
    },
    {
        "answer": "Runtime exception",
        "bogus": [
            "Compiler error",
            "Compiles and runs"
        ],
        "explanation": "The modulo operator (%) is performing a divide, so you get a divide-by-zero ArithmeticException at runtime. Even using a literal 0 (y = 4%0;) instead of a variable would not cause a compiler error.",
        "id": "48",
        "line": "What happens when you put this expression in your code: int x=0; int y=4%x;"
    },
    {
        "answer": "Compiles and runs",
        "bogus": [
            "Runtime exception",
            "Compiler error"
        ],
        "explanation": "Floating point numbers don't produce a divide-by-zero ArithmeticException. They will give a result which is a Not a Number value.",
        "id": "49",
        "line": "What happens when you have this in your code: double x; x=24.0/0;"
    },
    {
        "answer": "You get 270>>1",
        "bogus": [
            "You get 0",
            "You get -1"
        ],
        "explanation": "Shifting bits by a number greater than or equal to the number of bits in the result (eg. 32 bits for an int) will cause the value to be shifted by the number modulo the number of bits in the value (you shift by 33%32 which is 1).",
        "id": "50",
        "line": "What happens when you bit shift by a number greater than or equal to the number of bits in the result? (eg. int c=270; c>>33)"
    },
    {
        "answer": "Smaller type is promoted",
        "bogus": [
            "Must explicitly cast",
            "Compiler error"
        ],
        "explanation": "Java promotes the smaller type to the wider of the types being compared. So when comparing a byte to an int, the byte is first promoted to an int.",
        "id": "51",
        "line": "What happens when you compare two primitives of different numerical types?"
    },
    {
        "answer": "^",
        "bogus": [
            " is the regular OR operator.",
            "~",
            "&"
        ],
        "explanation": "The ^ is the Exclusive-OR operator. ",
        "id": "52",
        "line": "Which of these is the Exclusive-OR (XOR) operator?"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "\"&&\" is the short-circuit AND operator. If both values are true, the result is true, but Java won't bother to check the second condition if the first one is false.",
        "id": "53",
        "line": "x=false; y=true; What is the result of x&&y?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Methods CAN have more than one modifier, but not more than one ACCESS modifier. Access modifiers are public, protected, private (and of course, default/package). Early versions of the JDK allowed a private protected access, but current versions prohibit this.",
        "id": "54",
        "line": "Can a method have more than one access modifier?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Private means \"private to the class\". Period. Kids (subclasses) get no special treatment.",
        "id": "55",
        "line": "Can a subclass access the private member variables of its own superclass?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Overriden methods are allowed to have LESS restriction, and since protected is less restricted than default (package), this is allowed.",
        "id": "56",
        "line": "Can a method with no access modifier be overriden by a method marked protected?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Although early versions of the JDK enforced this, current versions let you initialize a final variable in the constructor, but no later. ",
        "id": "57",
        "line": "Does a final member variable have to be initialized at the time it's declared?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "An reference variable marked final does NOT mean that the object itself can not be changed... final means that the reference can never be changed to point to some other object.",
        "id": "58",
        "line": "If a final variable is a reference to an object, can that object's data members be modified while the program is running?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Final methods can't be overriden... they're FINAL! The compiler will catch you.",
        "id": "59",
        "line": "Can a final method be overriden?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "A long has 64 bits while an int has 32. You can't implicitly squish a *potentially* big thing into a little thing. The compiler doesn't care if the long variable is holding a tiny value. It just looks for the possibility of trouble.",
        "id": "60",
        "line": "Can you automatically convert a long to an int if the long value is small enough to fit into an int?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "They're the same bit-depth, but since chars are unsigned, they might have a higher positive value than a short can accept.",
        "id": "61",
        "line": "Can you automatically/implicitly convert a char to a short?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Since a short will definitely fit into an int, the short is implicitly promoted. No cast needed.",
        "id": "62",
        "line": "Can you pass a short into a method call when an int is expected?"
    },
    {
        "answer": "\"yes\" prints",
        "bogus": [
            "nothing prints"
        ],
        "explanation": "\"Yes\" prints because the expression (b = true) uses the assignment operator =, not the equality operator ==. So b is set to true, and the result is true so the if condition runs the code.",
        "id": "63",
        "line": "Given this code, what prints? boolean b = false; if (b = true) {System.out.println(\"yes\");}"
    },
    {
        "answer": "First statement after while ( )",
        "bogus": [
            "Nothing"
        ],
        "explanation": "If there are no curly braces to set off a loop block, then just the first statement is executed as part of the condition and any statements following that are executed no matter what.",
        "id": "64",
        "line": "If you do not use curly braces for a while loop body, what will execute if the while condition is true?"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The break statements means \"break out of this structure completely!\". So the current iteration and any other remaining iterations of the loop will not run. Execution moves to the first statement below the loop.",
        "id": "65",
        "line": "Using a break in a for loop causes the loop to break out of the current iteration and jump to the next iteration of the loop."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "If there is no catch(ExceptionArgument) which matches the thrown exception, then an argument which is a superclass of the exception will catch it.",
        "id": "66",
        "line": "An exception can be caught by a catch block with an argument type that is a superclass of the thrown exception."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "Its perfectly legal to catch the exception but do nothing about it.",
        "id": "67",
        "line": "In a try/catch, an exception will not be considered caught if the catch(Exception) block has no code within the braces { }"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "The finally block will always run if an exception is thrown, and then the exception is immediately passed to the calling method.",
        "id": "68",
        "line": "If an exception is not caught, the finally block will run and the rest of the method is skipped."
    },
    {
        "answer": "Argument list",
        "bogus": [
            "Return type",
            "Return type AND arg list"
        ],
        "explanation": "An overloaded method must change the argument list, but the return type is optional.",
        "id": "69",
        "line": "An overloaded method MUST change:"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "As long as the argument list is different, the overloaded method is also free to change both the return type and the access visibility.",
        "id": "70",
        "line": "Overloaded methods are free to change the access modifier."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "Constructors can be overloaded to allow the calling code to provide more (or less) information when the object is created.",
        "id": "71",
        "line": "Constructors can not be overloaded."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Inner classes have a special relationship with the their outer (enclosing) classes, including access to private data of the outer class. For event listeners implemented as inner classes, this means you do NOT have to pass a reference to the outer class in to the constructor of the inner class. The inner class has something like an \"outer this\" (\"outer this\" is not a keyword, just the way inner classes behave).",
        "id": "72",
        "line": "An inner class has free access to private member data of the outer classes."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Member variables are always given a default value -- numerical primitives get zero, chars get '\u0000', and object references get null.",
        "id": "73",
        "line": "Member (instance) variables are always assigned a default value if not explicitly initialized."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "You don't have to initialize a local variable, but as soon as you try to use it, you will get a compiler error because local variables aren't given default values.",
        "id": "74",
        "line": "Automatic (local) variables are always assigned a default value if not explicitly initialized."
    },
    {
        "answer": "a copy of the value",
        "bogus": [
            "the actual value"
        ],
        "explanation": "You always get a copy of whatever is in the variable -- either a primitive or a reference. So for objects, you get a copy of the reference.",
        "id": "75",
        "line": "When you pass a variable as an argument to a method call, what are you passing?"
    },
    {
        "answer": "a copy of the reference",
        "bogus": [
            "the original reference",
            "the object itself",
            "a reference to a copy"
        ],
        "explanation": "You always get a copy of whatever is in the variable -- either a primitive or a reference. So for objects, you get a copy of the reference.",
        "id": "76",
        "line": "When you pass an object reference as an argument to a method call, what gets passed?"
    },
    {
        "answer": "They'e gone",
        "bogus": [
            "They wrap around",
            "They're added to result"
        ],
        "explanation": "Bits moved off the end of any shift operation are just gone. History. Vaporized.",
        "id": "77",
        "line": "When using the right-shift operator (>>), what happens to the bits which move off the right end?"
    },
    {
        "answer": "false",
        "bogus": [
            "true",
            "compiler error"
        ],
        "explanation": "\" ^ \" is the Exclusive-OR operator -- it only returns true if just ONE of the values is true.",
        "id": "78",
        "line": "x=true; y=true; What is the result of x ^ y?"
    },
    {
        "answer": "false",
        "bogus": [
            "true",
            "compiler error"
        ],
        "explanation": "\"&\" is the AND operator. For a logical expression, the result will be true only if BOTH values are true. (For bitwise AND, both bits must be 1)",
        "id": "79",
        "line": "x=true; y=false; What is the result of x&y?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Private means \"private to the class\", NOT \"private to the object\". So two objects of the same class could access each other's private data. ",
        "id": "80",
        "line": "Can one object access a private variable of another object of the same class?"
    },
    {
        "answer": "In the same package as y",
        "bogus": [
            "declared public",
            "a subclass of y",
            "the same class as y"
        ],
        "explanation": "No access modifier really means \"default\" access which really means \"only to others in the package\". But you'll never see a keyword/modifier \"default\".",
        "id": "81",
        "line": "For object x to access a method of object y, when the method has no access modifier, object x MUST be an instance of a class which is:"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "Static variables CAN be marked final, which makes them essentialy constants, but they certainly don't have to be. Static just means \"one per class... not one per instance\"",
        "id": "82",
        "line": "static variables are implicitly final."
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Its legal to say anObject.aStaticMethod() but the preferred way is to use the class name - MyClass.aStaticMethod(), to make it obvious in your code that you're calling a static method.",
        "id": "83",
        "line": "Is it legal to access a static method using an instance of the class?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Within a static method, there is no 'this'! Since this refers to the current object -- the object whose method is executing -- 'this' makes no sense in a static method, since it doesn't belong to any object. ",
        "id": "84",
        "line": "Are static methods allowed to access instance variables using this.var?"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "Besides instance variables and inner classes, you can also define static code blocks. The compiler combines static blocks and static variable initialization into one big method which runs when the class is loaded.",
        "id": "85",
        "line": "The only things which can be defined inside a class and outside a method are instance variables and inner classes."
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "So how do you make an object...? Obviously you'd need a less restrictive method (say, public) which can be called to create and return a new object. ",
        "id": "86",
        "line": "Can a constructor be declared private?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Since a constructor is used solely to create a new instance, and an abstract class can NEVER be instanced, it makes no sense.",
        "id": "87",
        "line": "Can a constructor be declared abstract?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Constructors are for creating new instance objects. Static methods are for non-instance code, so it makes no sense to have a static constructor.",
        "id": "88",
        "line": "Can a constructor be declared static?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "The 2 is treated as an int literal, and since an int fits in a float, no explicit cast or 'f' is needed. If the literal was 2.0 (instead of 2), it would be treated as a double and would not compile.",
        "id": "89",
        "line": "Is this legal? float f = 2;"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Any int can fit into a double-sized variable, so no cast is needed.",
        "id": "90",
        "line": "Can you automatically convert an int to a double?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Regardless of the double value, a double-sized variable is too big to fit in a float, so it can't be implicitly cast.",
        "id": "91",
        "line": "Can you pass a double to a method when the method's argument is a float?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "Any object can always be referenced by a variable which is of a superclass type... because the child/subclass object is guaranteed to have the methods of the superclass.",
        "id": "92",
        "line": "Class Y is a subclass of class X. Will this compile? Y myY = new Y(); X myX = myY;"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The break statements means \"break out of this structure completely!\". So the current iteration and any other remaining iterations of the loop will not run. Execution moves to the first statement below the loop.",
        "id": "93",
        "line": "Using a break in a for loop causes the loop to break out of the current iteration and jump to the next iteration of the loop."
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "The only thing that can be an if (expression) is a boolean expression, and since in Java ints cannot be cast to booleans, if (anInteger) will not compile.",
        "id": "94",
        "line": "Is this legal? int i = 0; if (i) {}"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The case argument must be either an int literal, or an int-compatible variable which is a constant (i.e. static final).",
        "id": "95",
        "line": "In a switch statement, the argument to the case label (case: argument) can be any variable which can fit within an int."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Code executing as part of a case statement will just keep on running through other cases until/unless it reaches a break statement. This is called \"fall through\".",
        "id": "96",
        "line": "In a switch construct, once a matching case value is found, execution will continue through other case labels until it reaches a break statement."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The default can be placed anywhere.",
        "id": "97",
        "line": "In a switch construct, the default statement must be placed after all the case statements."
    },
    {
        "answer": "java.lang.Throwable",
        "bogus": [
            "java.lang.Error",
            "java.lang.Exception"
        ],
        "explanation": "The Exception hierarchy begins at java.lang.Throwable.",
        "id": "98",
        "line": "All exceptions inherit from:"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "Method B does not have to declare the exception if it handles it (by providing an appropriate catch(theException), OR if the exception declared by A is a runtime exception.",
        "id": "99",
        "line": "If method A declares that it throws an exception and method B calls method A, then method B must ALWAYS declare the same exception."
    },
    {
        "answer": "Overriding",
        "bogus": [
            "Overloading"
        ],
        "explanation": "An overriding method must have the same name, return type, and arguments as the method in the superclass. It can't declare new or broader checked exceptions, and it also can't have more restrictive access.",
        "id": "100",
        "line": "Class A inherits from B. B includes a method with the same name, return type, and arguments as a method in A. This is an example of:"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "An overloaded method can change the return type, but return type alone is not enough -- it also must change the argument list.",
        "id": "101",
        "line": "A class can define two methods with the same name as long as the return types are different."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "ArithmeticException is a RuntimeException, so the rules about overriding methods not throwing new or broader exceptions does not apply.",
        "id": "102",
        "line": "An overriding method of a subclass can throw an Arithmetic Exception not thrown by the original (overriden) method."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "This would be true for overriding methods, but overloaded methods are free to throw new checked exceptions.",
        "id": "103",
        "line": "Overloaded methods must not throw new checked exceptions not thrown by the original method."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "An inner class has a special relationship and can see even private member data of the outer class. ",
        "id": "104",
        "line": "An inner class has free access to ALL member data of its enclosing (outer) class."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "A non-static inner class can ONLY exist as part of a relationship with an instance of its enclosing class.",
        "id": "105",
        "line": "It is not possible to have an instance of a non-static inner class before any instances of the outer class have been created."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "It may look strange, but that syntax is how you can create an instance of an inner class when you don't yet have an instance of the outer class. An inner class must ALWAYS be tied to an instance of the outer class.",
        "id": "106",
        "line": "This is a legal way to create an instance of an inner class: OuterClass.InnerClass x = new OuterClass().new InnerClass();"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "A static inner class is treated as a top-level nested class, and has no reference -- no special relationship -- to any instance of the enclosing class.",
        "id": "107",
        "line": "A static inner class (considered a top-level nested class) can NOT access non-static variables of the outer class."
    },
    {
        "answer": "doStuff() in B",
        "bogus": [
            "doStuff() in A",
            "compiler error ",
            "runtime error "
        ],
        "explanation": "Java's polymorphism causes instance methods to be bound (chosen) at runtime, not during compilation. At runtime, the overriding method of the 'actual' object (which is a B) is dynamically chosen, even though the reference type is A.",
        "id": "108",
        "line": "Class B extends A. B overrides the doStuff() method inherited from A. Which methods gets called if you say: A myA = new B(); myA.doStuff();"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "You can have as many top-level classes in one source file as you like, but only one can be declared public and the source file name must match the public class name.",
        "id": "109",
        "line": "Are you allowed to have more than one top-level (non-inner) class definition per source file?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "You don't have to have any public classes in your source files.",
        "id": "110",
        "line": "Are you required to have a public class in each source file?"
    },
    {
        "answer": "00001010",
        "bogus": [
            "11001010",
            "10101100"
        ],
        "explanation": "The right shift operator fills from the left with the value of the sign bit (in this case, 0).",
        "id": "111",
        "line": "Assume the bit value of byte x is 00101011. What is the result of x>>2;"
    },
    {
        "answer": "negative",
        "bogus": [
            "positive"
        ],
        "explanation": "The right-shift operator fills all bits from the left with the same value as the original sign bit -- in this case 1.",
        "id": "112",
        "line": "Assume the bit pattern of byte x is: 10110001. What will the sign of x be after x>>2;"
    },
    {
        "answer": "change its sign",
        "bogus": [
            "keep it negative"
        ],
        "explanation": "The unsigned right-shift operator always fills from the left with zero, so the result will always become positive.",
        "id": "113",
        "line": "What does the unsigned right-shift operator do to a negative number?"
    },
    {
        "answer": "00110000",
        "bogus": [
            "11110011",
            "00000000"
        ],
        "explanation": "The bitwise AND operator, &, will result in bit values of 1 only if BOTH bits being compared are 1.",
        "id": "114",
        "line": "What is the result of 00110011 & 11110000 ?"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Short-circuit operators work only on logical expressions; they make boolean tests more efficient by not testing both operands unless its necessary. ",
        "id": "115",
        "line": "The short-circuit operators work on boolean types, not bits."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "No access modifier means \"default\" which means \"only to others in the package\".",
        "id": "116",
        "line": "Methods which have no access modifier can only be accessed by classes within the same package. TRUE or FALSE:"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "Protected means \"any subclass, regardless of package, can inherit these members -- methods or data\"",
        "id": "117",
        "line": "Methods which are marked protected can be accessed only by classes within the same package."
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Overriding methods can NOT be more restrictive than the original method. ",
        "id": "118",
        "line": "Can a public method be overriden by a protected method?"
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "A final class is a class which can NOT be subclassed. Its the end-of-the-line in the inheritance tree.",
        "id": "119",
        "line": "Methods and variables can be declared as final, but a class can not be declared final."
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "A final class means the end-of-the-line for the inhertance tree. No subclasses can be made from a final class, only instances.",
        "id": "120",
        "line": "Can a final class be subclassed?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "final and abstract are opposites: a final class can NOT be subclassed, while an abstract class MUST be subclassed... so final abstract makes no sense.",
        "id": "121",
        "line": "Can you have a final abstract class?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Only methods and classes can be declared abstract.",
        "id": "122",
        "line": "Can a member variable be declared abstract?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "An abstract method has no choice! It MUST be overridden.",
        "id": "123",
        "line": "Can an abstract method be overridden?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Methods can be native, not classes or variables.",
        "id": "124",
        "line": "Can a class be declared native?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Only methods can be native.",
        "id": "125",
        "line": "Can a member variable be declared native?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "The transient modifier is used to keep variable data from being saved if an object is \"serialized\" (saved as an object ). Since methods don't change their state for an individual object, methods are never serialized, so transient is not needed for methods.",
        "id": "126",
        "line": "Can a method be declared transient?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "Only data members can be declared as transient... meaning their value won't be saved when an object is \"serialized\" (saved).",
        "id": "127",
        "line": "Can a class be declared transient?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "The word synchronized marks method code so that it can't be run by more than one thread at a time. ",
        "id": "128",
        "line": "Can a member variable be declared synchronized?"
    },
    {
        "answer": "No",
        "bogus": [
            "Yes"
        ],
        "explanation": "The keyword synchronized marks method code so that only one thread can access it at a time.",
        "id": "129",
        "line": "Can a class be declared synchronized?"
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "The synchronized modifier means that only one thread at a time can run the synchronized code.",
        "id": "130",
        "line": "Can a method be declared synchronized?"
    },
    {
        "answer": "Compiles and runs",
        "bogus": [
            "Won't compile without cast",
            "Runtime error"
        ],
        "explanation": "The ++operator increments a as a byte, so there's no problem. You WOULD have a problem with a = a + 1; because the a + 1 would result in an int which would then have to be cast back to the byte variable a.",
        "id": "131",
        "line": "What happens when you compile this: byte a=2; a++; "
    },
    {
        "answer": "Yes",
        "bogus": [
            "No"
        ],
        "explanation": "It will compile, because the myX object might really have been a Y object all along (as far as the compiler knows), but at runtime it will fail.",
        "id": "132",
        "line": "Class Y is a subclass of class X. Will this compile? X myX = new X(); Y myY = (Y) myX;"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "The switch can obviously hold a variable, because that's what you're testing! But the variable cannot be anything except an int-compatible primitive.",
        "id": "133",
        "line": "In a switch statement, the argument to the switch ( ) can be a variable as long as it is compatible with an int."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The default statement will only execute if no matching case is found, OR if there is \"fall through\" from a case statement executed above.",
        "id": "134",
        "line": "In a switch construct, the default statement will always execute."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "If there are no matching cases found, the default will run.",
        "id": "135",
        "line": "In a switch construct, the default statement will execute if no case values match the switch() argument."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The catch blocks must be written in order from most specific to least (e.g. IOException comes above Exception) or the compiler will complain.",
        "id": "136",
        "line": "In a try/catch construct, the catch blocks may be written in any order and the most narrow/specific catch argument is chosen at runtime."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "The finally block will ALWAYS run... unless the try/catch calls System.exit().",
        "id": "137",
        "line": "A finally block will only be executed if the exception is not caught."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "A MalformedURLException is checked by the compiler to be sure that the \"handle or declare\" rule is followed.",
        "id": "138",
        "line": "A MalformedURLException is a checked exception."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "ArithmeticExceptions are considered programmer problems, and are NOT checked by the compiler. A divide-by-zero error, for example, should be checked for by your own code.",
        "id": "139",
        "line": "An ArithmeticException is a checked exception."
    },
    {
        "answer": "false",
        "bogus": [
            "true"
        ],
        "explanation": "System.exit() means stop the program now! Even a finally block can't stop that from happening.",
        "id": "140",
        "line": "If a try/catch construct calls System.exit(), the finally block will execute first."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Since the protected modifier is less restrictive than default, an overriding method is allowed to make that change.",
        "id": "141",
        "line": "An overriding method can change the access modifier from default to protected."
    },
    {
        "answer": "super.theMethod();",
        "bogus": [
            "this.theMethod();"
        ],
        "explanation": "The super keyword is like a reference to the superclass.",
        "id": "142",
        "line": "To invoke an overriden method from the method which overrides it, use:"
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "Constructors are different from methods in several ways, and inheritance is one of them. Subclasses do NOT get a constructor from the superclass.",
        "id": "143",
        "line": "Constructors are not inherited."
    },
    {
        "answer": "true",
        "bogus": [
            "false"
        ],
        "explanation": "One of the best uses of polymorphism in Java is to declare an array using an interface type, then add objects which can be from any class, as long as they implement the interface type of the array.",
        "id": "144",
        "line": "It is legal to declare an array with an interface as the type (instead of a class or primitive) -- example: PetInterface []p;"
    }
]
