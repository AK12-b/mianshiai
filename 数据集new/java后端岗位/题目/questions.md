<roundup>
  
  <question id="1">
    <line>All Java keywords are written in lower case.</line>
    <answer>true</answer>
    <explanation>No keywords in Java start with a capital letter.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="2">
    <line>A method name can start with a number.</line>
    <answer>false</answer>
    <explanation>Method and variable names can't start with a number, although they can contain numbers. myMethod2() is legal; 2myMethod() is not.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="3">
    <line>A variable name can start with $.</line>
    <answer>true</answer>
    <explanation>Methods and variables can start with a $ character, but its not recommended.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="4">
    <line>Which of these are legal values for a boolean?</line>
    <answer>true, false</answer>
    <explanation>Unlike C/C++, booleans in Java are represented with the literals true and false. You can't substitute integers such as 1 and 0 where a boolean value is expected.</explanation>
    <bogusanswer>true, false, 1,0</bogusanswer>
    <bogusanswer>TRUE, FALSE</bogusanswer>
    <bogusanswer>TRUE, FALSE, true, false</bogusanswer>
    <bogusanswer>1,0</bogusanswer>
  </question>
 
  <question id="5">
    <line>What's the bit-depth of a char?</line>
    <answer>16 bits</answer>
    <explanation>Chars in Java are all 16-bit Unicode.</explanation>
    <bogusanswer>8 bits</bogusanswer>
    <bogusanswer>7 bits</bogusanswer>
    <bogusanswer>32 bits</bogusanswer>
  </question>
 
  <question id="6">
    <line>What is the bit-depth of a byte?</line>
    <answer>8 bits</answer>
    <explanation>Bytes are always 8 bits. The left-most bit is used to represent the sign.</explanation>
    <bogusanswer>7 bits</bogusanswer>
  </question>
  
  <question id="7">
    <line>A floating point literal in Java is treated as a:</line>
    <answer>double</answer>
    <explanation>Floating point literals (example: 2.27) in Java are automatically typed as doubles unless you add an &quot;f&quot; (example: 2.27f).</explanation>
    <bogusanswer>float</bogusanswer>
    <bogusanswer>Double</bogusanswer>
    <bogusanswer>Float</bogusanswer>
  </question>
 
  <question id="8">
    <line>When you pass a reference to an array as an argument to a</line>
    <line>method call, you pass a new reference to the original array.</line>
    <answer>true</answer>
    <explanation>An array is an object, and Java always passes a copy of the reference to the original object. Both references now point to the same array object.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="9">
    <line>Does a call to System.gc() force garbage collection to take</line>
    <line>place? (assuming there are objects to be collected)</line>
    <answer>Not always</answer>
    <explanation>Think of a call to System.gc( ) as a... suggestion. Garbage collection may or may not actually occur.</explanation>
    <bogusanswer>Yes always</bogusanswer>
  </question>
  
  <question id="10">
    <line>Which of these operators would cause the bit pattern</line>
    <line>00111100 to become 11000011?</line>
    <answer>~</answer>
    <explanation>The ~ operator performs a bitwise inversion (flips the bits!).</explanation>
    <bogusanswer>!</bogusanswer>
    <bogusanswer>-</bogusanswer>
  </question>
  
  <question id="11">
    <line>Assume x=2 and y=4. What is the result of (!(y>x))</line>
    <answer>false</answer>
    <explanation>This test asks if the expression (y is greater than 2) is NOT true... and that's false. y IS greater than 2, so the result is false.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="12">
    <line>Assume x=6 and y=7. What is the result of (!(y==x))</line>
    <answer>true</answer>
    <explanation>The test asks if the primitive values of y and x are NOT equal in value, which is true.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="13">
    <line>Assume:  byte a=3; byte b=2;  What is the result of: byte c=a+b;</line>
    <answer>compiler error</answer>
    <explanation>The result of arithmetic is always at LEAST an int, so the compiler will ask for an explicit cast:  byte c = (byte) a + b;  even though a and b are bytes.</explanation>
    <bogusanswer>c == 5</bogusanswer>
    <bogusanswer>runtime exception </bogusanswer>
    <bogusanswer>c = 3 </bogusanswer>
  </question>
 
  <question id="14">
    <line>Can you compare a boolean to an integer?</line>
    <answer>No</answer>
    <explanation>Not in Java. A boolean has a value of true or false, not 1 and 0, or 0 and anything else. You just can't do it (watch out C folks)</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="15">
    <line>What is &quot;instanceof&quot; ?</line>
    <answer>an operator</answer>
    <explanation>The &quot;instanceof&quot; operator --  if ( A instanceof B) -- tests if an object A is either an instance of B, an instance of a subclass of B, or implements the B interface.</explanation>
    <bogusanswer>a method in Object</bogusanswer>
  </question>
  
  <question id="16">
    <line>Integer a = new Integer(5);  Integer b = new Integer(5); What is the result of running:  if (a==b)</line>
    <answer>false</answer>
    <explanation>The == operator looks ONLY for a match of bits. For two different objects, the only way == can be true is if both reference variables point to the SAME object.</explanation>
    <bogusanswer>true</bogusanswer>
    <bogusanswer>compiler error</bogusanswer>
    <bogusanswer>runtime exception</bogusanswer>
  </question>
 
  <question id="17">
    <line>Can a top-level class be marked as private?</line>
    <answer>No</answer>
    <explanation>If a class was private... who could use it? For a normal, top-level class, the only access modifiers that make sense are public and default (package). Only nested inner classes can be private.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="18">
    <line>Can a top-level class be marked as protected?</line>
    <answer>No</answer>
    <explanation>A top-level class can ONLY be marked public or default (package). The protected access is just for member variables and methods, and allows subclasses outside the superclass package to inherit the protected members. </explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="19">
    <line>methods which are marked protected can be called on any subclass of the class in which the method is declared.</line>
    <answer>true</answer>
    <explanation>Protected members (methods and data) can be inherited by subclasses, including those outside the package. The only difference between default (no modifier) and protected, is that protected extends visibility outside the package... but  ONLY to subclasses.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="20">
    <line>Can a private method of a superclass be declared within a subclass?</line>
    <answer>Yes</answer>
    <explanation>Trick question! Private methods can NOT be overriden, but they CAN be re-declared/redefined in the subclass, and called directly on the subclass object. But polymorphism will not apply.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="21">
    <line>Can an abstract class have non-abstract methods?</line>
    <answer>Yes</answer>
    <explanation>Unlike interfaces, abstract classes CAN provide implementations of methods.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="22">
    <line>Can a non-abstract class have abstract methods?</line>
    <answer>No</answer>
    <explanation>Even a single abstract method tells the compiler that the whole class MUST be declared abstract.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="23">
    <line>If a non-abstract class X inherits abstract methods from its abstract parent class Y, is the X class required to implement the inherited abstract methods?</line>
    <answer>Yes</answer>
    <explanation>Any concrete (non-abstract) class MUST provide bodies (implementations) for the abstract methods it inherits. Otherwise, the compiler will insist that the new subclass itself be marked as abstract.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="24">
    <line>If a class declares that it implements an interface (class A implements B), must class A implement the methods of interface B?</line>
    <answer>Unless A is abstract</answer>
    <explanation>If a class is abstract, and it implements an interface, it can leave the interface implementations up to a concrete (non-abstract) subclass.</explanation>
    <bogusanswer>Under ALL circumstances</bogusanswer>
  </question>
  
  <question id="25">
    <line>TRUE or FALSE - during arithmetic, when the operands are of different types, the resulting type is ALWAYS the widest of the two types.</line>
    <answer>false</answer>
    <explanation>The result of an arithmetic operation on any two primitive integer operands will be at least an int -- even if the operands are byte and short. </explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="26">
    <line>class Y is a subclass of class X. Will this compile? Y myY = new X( );</line>
    <answer>No</answer>
    <explanation>You can never have a reference type of a subclass refer to an actual instance of the superclass. Because an object of type X (the superclass) wouldn't have the new methods that an object of type Y (the subclass) is supposed to have.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="27">
    <line>Class Y is a subclass of class X.  Will this compile?  X myX = new Y( );</line>
    <answer>Yes</answer>
    <explanation>A reference variable can always refer to any object which is a subclass of the reference type, since the instance methods of the reference variable type (X) are guaranteed to be callable on the actual subclass object (class Y).</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="28">
    <line>Can you compile an if construct without using curly braces?</line>
    <answer>Yes</answer>
    <explanation>If you do not use curly braces for an if block, then only the first statement following the if ( ) will run if the if condition is true.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="29">
    <line>What goes in the first part of a for loop?</line>
    <answer>init statement</answer>
    <explanation>The init statement must be the first part of the for declaration -- example: for (int i = 7;;)</explanation>
    <bogusanswer>boolean test</bogusanswer>
    <bogusanswer>iteration expression</bogusanswer>
  </question>
  
  <question id="30">
    <line>Is this legal? int i; for (i = 4, int t = 6;i &lt; 0; i++)</line>
    <answer>No</answer>
    <explanation>You can declare or initialize more than one variable in a for loop, but you can't mix declarations with initializations.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="31">
    <line>in a for loop, the continue statement will cause execution to jump out of the loop and continue on at the first statement below the loop.</line>
    <answer>false</answer>
    <explanation>Continue really means &quot;continue with the next iteration&quot;, so execution of the current iteration stops, and execution continues at the next iteration.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="32">
    <line>an inner class must be defined within the opening and closing braces of a class, but outside any method -- just like data members.</line>
    <answer>false</answer>
    <explanation>An inner class can be defined inside a method, or even inside a parameter to a method! (with anonymous inner classes)</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="33">
    <line>A class defined within a parameter to a method is:</line>
    <answer>anonymous</answer>
    <explanation>A class defined within a method parameter (typically used with event listener classes) is considered anonymous. The object is created and alive, but the new class type has no name, so you can't create more instances of it except by going through that same method.</explanation>
    <bogusanswer>static</bogusanswer>
    <bogusanswer>illegal</bogusanswer>
  </question>
  
  <question id="34">
    <line>when an object is created using new (), its constructor will not complete until all constructors higher on the inheritance tree have completed.</line>
    <answer>true</answer>
    <explanation>Remember the rule: the parents must exist before the children, and this goes all the way up the ancestral tree.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="35">
    <line>the compiler will always insert a default, no-argument constructor into any class definition.</line>
    <answer>false</answer>
    <explanation>The compiler will ONLY insert a default constructor if you don't provide any constructor at all.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="36">
    <line>when you override a method, the overriding method in the subclass  must not declare ANY new exceptions not declared by the overridden method.</line>
    <answer>false</answer>
    <explanation>This rule only applies to &quot;checked&quot; exceptions -- the exceptions which are subclasses of Exception but not subclasses of RuntimeException.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="37">
    <line>Will this compile?  float f = 27.24f;</line>
    <answer>Yes</answer>
    <explanation>Without the 'f&quot;, Java will consider a floating point number to be a double, and the compiler will force you to do an explicit cast.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="38">
    <line>As a literal, what is 27.24?</line>
    <answer>a double</answer>
    <explanation>All floating point literals are assumed to be doubles, not floats!</explanation>
    <bogusanswer>a float</bogusanswer>
  </question>
  
  <question id="39">
    <line>Will this compile?  int[ ]x=[27];</line>
    <answer>No</answer>
    <explanation>In Java you must create the array using: int[]x = new int[27] or by explicitly filling in the values:  int[ ]={4,2,7,3,9,8};</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="40">
    <line>When an array is constructed, are its elements automatically initialized?</line>
    <answer>Yes</answer>
    <explanation>All array elements are ALWAYS given default values, whether the array is a class, instance, or local variable. Primitives initialize to zero, booleans to false, chars to '\u0000' and object references to null.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="41">
    <line>Is this legal?  int x[ ] = {2,7,4,9};</line>
    <answer>Yes</answer>
    <explanation>This instantiation/initialization is allowed as a shortcut to creating a new array and assigning a value for each index.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="42">
    <line>Is this legal?  public static void main (String [ ] fred) </line>
    <answer>Yes</answer>
    <explanation>You can call the command-line argument variable whatever you like! And you can put the brackets [] before or after the variable name.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="43">
    <line>Assume x=0 and y=3. What is the value of x after:  x=y++;</line>
    <answer>3</answer>
    <explanation>X gets the value of y, THEN y is incremented. The result would have been 4 if the expression was:  x = ++y;</explanation>
    <bogusanswer>4</bogusanswer>
    <bogusanswer>7</bogusanswer>
  </question>
  
  <question id="44">
    <line>Given x=y--;   Which of these will be true AFTER execution?</line>
    <answer>x > y</answer>
    <explanation>y is assigned to x and THEN y is decremented</explanation>
    <bogusanswer>x &lt; y</bogusanswer>
    <bogusanswer>x == y</bogusanswer>
  </question>
  
  <question id="45">
    <line>Integer literals are always what size?</line>
    <answer>int - 32 bits</answer>
    <explanation>integer literals are always treated as 32-bit ints</explanation>
    <bogusanswer>short - 16 bits</bogusanswer>
    <bogusanswer>int - 16 bits</bogusanswer>
    <bogusanswer>byte - 8 bits</bogusanswer>
    <bogusanswer>int - 64 bits</bogusanswer>
  </question>
 
  <question id="46">
    <line>Can you use an increment unary operator on a byte? (example: byte b=4; b++;)</line>
    <answer>Yes</answer>
    <explanation>You don't need an explicit cast to use the increment unary operator.</explanation>
    <bogusanswer>Never</bogusanswer>
    <bogusanswer>With explicit cast</bogusanswer>
  </question>
  
  <question id="47">
    <line>What happens when you divide an integer by a variable with a value of 0?</line>
    <answer>Runtime exception</answer>
    <explanation>At runtime you will get an ArithmeticException which is not checked for at compile time. Floats and doubles will NOT throw an exception if divided by 0.</explanation>
    <bogusanswer>Compiler error</bogusanswer>
    <bogusanswer>Compiles and runs</bogusanswer>
  </question>
  
  <question id="48">
    <line>What happens when you put this expression in your code:  int x=0; int y=4%x;</line>
    <answer>Runtime exception</answer>
    <explanation>The modulo operator (%) is performing a divide, so you get a divide-by-zero ArithmeticException at runtime. Even using a literal 0 (y = 4%0;) instead of a variable would not cause a compiler error.</explanation>
    <bogusanswer>Compiler error</bogusanswer>
    <bogusanswer>Compiles and runs</bogusanswer>
  </question>
  
  <question id="49">
    <line>What happens when you have this in your code:  double x; x=24.0/0;</line>
    <answer>Compiles and runs</answer>
    <explanation>Floating point numbers don't produce a divide-by-zero ArithmeticException. They will give a result which is a Not a Number value.</explanation>
    <bogusanswer>Runtime exception</bogusanswer>
    <bogusanswer>Compiler error</bogusanswer>
  </question>
  
  <question id="50">
    <line>What happens when you bit shift by a number greater than or equal to the number of bits in the result? (eg. int c=270; c>>33)</line>
    <answer>You get 270>>1</answer>
    <explanation>Shifting bits by a number greater than or equal to the number of bits in the result (eg. 32 bits for an int) will cause the value to be shifted by the number modulo the number of bits in the value (you shift by 33%32 which is 1).</explanation>
    <bogusanswer>You get 0</bogusanswer>
    <bogusanswer>You get -1</bogusanswer>
  </question>
  
  <question id="51">
    <line>What happens when you compare two primitives of different numerical types?</line>
    <answer>Smaller type is promoted</answer>
    <explanation>Java promotes the smaller type to the wider of the types being compared. So when comparing a byte to an int, the byte is first promoted to an int.</explanation>
    <bogusanswer>Must explicitly cast</bogusanswer>
    <bogusanswer>Compiler error</bogusanswer>
  </question>
 
  <question id="52">
    <line>Which of these is the Exclusive-OR (XOR) operator?</line>
    <answer>^</answer>
    <explanation>The ^ is the Exclusive-OR operator.  </explanation>
    <bogusanswer> is the regular OR operator.</bogusanswer>
    <bogusanswer>~</bogusanswer>
    <bogusanswer>&amp;</bogusanswer>
  </question>
 
  <question id="53">
    <line>x=false; y=true;   What is the result of x&amp;&amp;y?</line>
    <answer>false</answer>
    <explanation>&quot;&amp;&amp;&quot; is the short-circuit AND operator. If both values are true, the result is true, but Java won't bother to check the second condition if the first  one is false.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="54">
    <line>Can a method have more than one access modifier?</line>
    <answer>No</answer>
    <explanation>Methods CAN have more than one modifier, but not more than one ACCESS modifier. Access modifiers are public, protected, private (and of course, default/package). Early versions of the JDK allowed a private protected access, but current versions prohibit this.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="55">
    <line>Can a subclass access the  private member variables of its own superclass?</line>
    <answer>No</answer>
    <explanation>Private means &quot;private to the class&quot;. Period. Kids (subclasses) get no special treatment.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="56">
    <line>Can a method with no access modifier be overriden by a method marked protected?</line>
    <answer>Yes</answer>
    <explanation>Overriden methods are allowed to have LESS restriction, and since protected is less restricted than default (package), this is allowed.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="57">
    <line>Does a final member variable have to be initialized at the time it's declared?</line>
    <answer>No</answer>
    <explanation>Although early versions of the JDK enforced this, current versions let you initialize a final variable in the constructor, but no later. </explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="58">
    <line>If a final variable is a reference to an object, can that object's data members be modified while the program is running?</line>
    <answer>Yes</answer>
    <explanation>An reference variable marked final does NOT mean that the object itself can not be changed... final means that the reference can never be changed to point to some other object.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="59">
    <line>Can a final method be overriden?</line>
    <answer>No</answer>
    <explanation>Final methods can't be overriden... they're FINAL! The compiler will catch you.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="60">
    <line>Can you automatically convert a long to an int if the long value is small enough to fit into an int?</line>
    <answer>No</answer>
    <explanation>A long has 64 bits while an int has 32. You can't implicitly squish a *potentially* big thing into a little thing. The compiler doesn't care if the long variable is holding a tiny value. It just looks for the possibility of trouble.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="61">
    <line>Can you automatically/implicitly convert a char to a short?</line>
    <answer>No</answer>
    <explanation>They're the same bit-depth, but since chars are unsigned, they might have a higher positive value than a short can accept.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="62">
    <line>Can you pass a short into a method call when an int is expected?</line>
    <answer>Yes</answer>
    <explanation>Since a short will definitely fit into an int, the short is implicitly promoted. No cast needed.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="63">
    <line>Given this code, what prints? boolean b = false; if (b = true) {System.out.println(&quot;yes&quot;);}</line>
    <answer>&quot;yes&quot; prints</answer>
    <explanation>&quot;Yes&quot; prints because the expression (b = true) uses the assignment operator =, not the equality operator ==.  So b is set to true, and the result is true so the if condition runs the code.</explanation>
    <bogusanswer>nothing prints</bogusanswer>
  </question>
  
  <question id="64">
    <line>If you do not use curly braces for a while loop body, what will execute if the while condition is true?</line>
    <answer>First statement after while ( )</answer>
    <explanation>If there are no curly braces to set off a loop block, then just the first statement is executed as part of the condition and any statements following that are executed no matter what.</explanation>
    <bogusanswer>Nothing</bogusanswer>
  </question>
  
  <question id="65">
    <line>using a break in a for loop causes the loop to break out of the current iteration and jump to the next iteration of the loop.</line>
    <answer>false</answer>
    <explanation>The break statements means &quot;break out of this structure completely!&quot;. So the current iteration and any other remaining iterations of the loop will not run. Execution moves to the first statement below the loop.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="66">
    <line>an exception can be caught by a catch block with an argument type that is a superclass of the thrown exception.</line>
    <answer>true</answer>
    <explanation>If there is no catch(ExceptionArgument) which matches the thrown exception, then an argument which is a superclass of the exception will catch it.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="67">
    <line>in a try/catch, an exception will not be considered caught if the catch(Exception) block has no code within the braces { }</line>
    <answer>false</answer>
    <explanation>Its perfectly legal to catch the exception but do nothing about it.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="68">
    <line>if an exception is not caught, the finally block will run and the rest of the method is skipped.</line>
    <answer>true</answer>
    <explanation>The finally block will always run if an exception is thrown, and then the exception is immediately passed to the calling method.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="69">
    <line>An overloaded method MUST change:</line>
    <answer>Argument list</answer>
    <explanation>An overloaded method must change the argument list, but the return type is optional.</explanation>
    <bogusanswer>Return type</bogusanswer>
    <bogusanswer>Return type AND arg list</bogusanswer>
  </question>
  
  <question id="70">
    <line>overloaded methods are free to change the access modifier.</line>
    <answer>true</answer>
    <explanation>As long as the argument list is different, the overloaded method is also free to change both the return type and the access visibility.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="71">
    <line>constructors can not be overloaded.</line>
    <answer>false</answer>
    <explanation>Constructors can be overloaded to allow the calling code to provide more  (or less) information when the object is created.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="72">
    <line>An inner class has free access to private member data of the outer classes.</line>
    <answer>true</answer>
    <explanation>Inner classes have a special relationship with the their outer (enclosing) classes, including access to private data of the outer class. For event listeners implemented as inner classes, this means you do NOT have to pass a reference to the outer class in to the constructor of the inner class. The inner class has something like an &quot;outer this&quot; (&quot;outer this&quot; is not a keyword, just the way inner classes behave).</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="73">
    <line>Member (instance) variables are always assigned a default value if not explicitly initialized.</line>
    <answer>true</answer>
    <explanation>Member variables are always given a default value -- numerical primitives get zero, chars get '\u0000', and object references get null.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="74">
    <line>Automatic (local) variables are always assigned a default value if not explicitly initialized.</line>
    <answer>false</answer>
    <explanation>You don't have to initialize a local variable, but as soon as you try to use it, you will get a compiler error because local variables aren't given default values.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="75">
    <line>When you pass a variable as an argument to a method call, what are you passing?</line>
    <answer>a copy of the value</answer>
    <explanation>You always get a copy of whatever is in the variable -- either a primitive or a reference. So for objects, you get a copy of the reference.</explanation>
    <bogusanswer>the actual value</bogusanswer>
  </question>
  
  <question id="76">
    <line>When you pass an object reference as an argument to a method call, what gets passed?</line>
    <answer>a copy of the reference</answer>
    <explanation>You always get a copy of whatever is in the variable -- either a primitive or a reference. So for objects, you get a copy of the reference.</explanation>
    <bogusanswer>the original reference</bogusanswer>
    <bogusanswer>the object itself</bogusanswer>
    <bogusanswer>a reference to a copy</bogusanswer>
  </question>
 
  <question id="77">
    <line>When using the right-shift operator (>>), what happens to the bits which move off the right end?</line>
    <answer>They'e gone</answer>
    <explanation>Bits moved off the end of any shift operation are just gone. History.  Vaporized.</explanation>
    <bogusanswer>They wrap around</bogusanswer>
    <bogusanswer>They're added to result</bogusanswer>
  </question>
  
  <question id="78">
    <line>x=true; y=true;    What is the result of x ^ y?</line>
    <answer>false</answer>
    <explanation>&quot; ^ &quot; is the Exclusive-OR operator -- it only returns true if just ONE of the values is true.</explanation>
    <bogusanswer>true</bogusanswer>
    <bogusanswer>compiler error</bogusanswer>
  </question>
  
  <question id="79">
    <line>x=true; y=false;   What is the result of x&amp;y?</line>
    <answer>false</answer>
    <explanation>&quot;&amp;&quot; is the AND operator. For a logical expression, the result will be true only if BOTH values are true. (For bitwise AND, both bits must be 1)</explanation>
    <bogusanswer>true</bogusanswer>
    <bogusanswer>compiler error</bogusanswer>
  </question>
  
  <question id="80">
    <line>Can one object access a private variable of another object of the same class?</line>
    <answer>Yes</answer>
    <explanation>Private means &quot;private to the class&quot;, NOT &quot;private to the object&quot;. So two objects of the same class could access each other's private data. </explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="81">
    <line>For object x to access a method of object y, when the method has no access modifier, object x MUST be an instance of a class which is:</line>
    <answer>In the same package as y</answer>
    <explanation>No access modifier really means &quot;default&quot; access which really means &quot;only to others in the package&quot;.  But you'll never see a keyword/modifier &quot;default&quot;.</explanation>
    <bogusanswer>declared public</bogusanswer>
    <bogusanswer>a subclass of y</bogusanswer>
    <bogusanswer>the same class as y</bogusanswer>
  </question>
 
  <question id="82">
    <line>static variables are implicitly final.</line>
    <answer>false</answer>
    <explanation>Static variables CAN be marked final, which makes them essentialy constants, but they certainly don't have to be. Static just means &quot;one per class... not one per instance&quot;</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="83">
    <line>Is it legal to access a static method using an instance of the class?</line>
    <answer>Yes</answer>
    <explanation>Its legal to say anObject.aStaticMethod() but the preferred way is to use the class name - MyClass.aStaticMethod(), to make it obvious in your code that you're calling a static method.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="84">
    <line>Are static methods allowed to access instance variables using this.var?</line>
    <answer>No</answer>
    <explanation>Within a static method, there is no 'this'! Since this refers to the current object -- the object whose method is executing -- 'this' makes no sense in a static method, since it doesn't belong to any object. </explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="85">
    <line>The only things which can be defined inside a class and outside a method are instance variables and inner classes.</line>
    <answer>false</answer>
    <explanation>Besides instance variables and inner classes, you can also define static code blocks. The compiler combines static blocks and  static variable initialization into one big method which runs when the class is loaded.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="86">
    <line>Can a constructor be declared private?</line>
    <answer>Yes</answer>
    <explanation>So how do you make an object...? Obviously you'd need a less restrictive method (say, public) which can be called to create and return a new object. </explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="87">
    <line>Can a constructor be declared abstract?</line>
    <answer>No</answer>
    <explanation>Since a constructor is used solely to create a new instance, and an abstract class can NEVER be instanced, it makes no sense.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="88">
    <line>Can a constructor be declared static?</line>
    <answer>No</answer>
    <explanation>Constructors are for creating new instance objects. Static methods are for non-instance code, so it makes no sense to have a static constructor.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="89">
    <line>Is this legal?  float f = 2;</line>
    <answer>Yes</answer>
    <explanation>The 2 is treated as an int literal, and since an int fits in a float, no explicit cast or 'f' is needed. If the literal was 2.0 (instead of 2), it would be treated as a double and would not compile.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="90">
    <line>Can you automatically convert an int to a double?</line>
    <answer>Yes</answer>
    <explanation>Any int can fit into a double-sized variable, so no cast is needed.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="91">
    <line>Can you pass a double to a method when the method's argument is a float?</line>
    <answer>No</answer>
    <explanation>Regardless of the double value, a double-sized variable is too big to fit in a float, so it can't be implicitly cast.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="92">
    <line>Class Y is a subclass of class X. Will this compile? Y myY = new Y(); X myX = myY;</line>
    <answer>Yes</answer>
    <explanation>Any object can always be referenced by a variable which is of a superclass type... because the child/subclass object is guaranteed to have the methods of the superclass.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="93">
    <line>using a break in a for loop causes the loop to break out of the current iteration and jump to the next iteration of the loop.</line>
    <answer>false</answer>
    <explanation>The break statements means &quot;break out of this structure completely!&quot;. So the current iteration and any other remaining iterations of the loop will not run. Execution moves to the first statement below the loop.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="94">
    <line>Is this legal?  int i = 0; if (i) {}</line>
    <answer>No</answer>
    <explanation>The only thing that can be an if (expression) is a boolean expression, and since in Java ints cannot be cast to booleans, if (anInteger) will not compile.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="95">
    <line>in a switch statement, the argument to the case label (case: argument) can be any variable which can fit within an int.</line>
    <answer>false</answer>
    <explanation>The case argument must be either an int literal, or an int-compatible variable which is a constant (i.e. static final).</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="96">
    <line>in a switch construct, once a matching case value is found, execution will continue through other case labels until it reaches a break statement.</line>
    <answer>true</answer>
    <explanation>Code executing as part of a case statement will just keep on running through other cases until/unless it reaches a break statement. This is called &quot;fall through&quot;.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="97">
    <line>in a switch construct, the default statement must be placed after all the case statements.</line>
    <answer>false</answer>
    <explanation>The default can be placed anywhere.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="98">
    <line>All exceptions inherit from:</line>
    <answer>java.lang.Throwable</answer>
    <explanation>The Exception hierarchy begins at java.lang.Throwable.</explanation>
    <bogusanswer>java.lang.Error</bogusanswer>
    <bogusanswer>java.lang.Exception</bogusanswer>
  </question>
  
  <question id="99">
    <line>If method A declares that it throws an exception and method B calls method A, then method B must ALWAYS declare the same exception.</line>
    <answer>false</answer>
    <explanation>Method B does not have to declare the exception if it handles it (by providing an appropriate catch(theException), OR if the exception declared by A is a runtime exception.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="100">
    <line>Class A inherits from B. B includes a method with the same name, return type, and arguments as a method in A. This is an example of:</line>
    <answer>Overriding</answer>
    <explanation>An overriding method must have the same name, return type, and arguments as the method in the superclass. It can't declare new or broader checked exceptions, and it also can't have more restrictive access.</explanation>
    <bogusanswer>Overloading</bogusanswer>
  </question>
  
  <question id="101">
    <line>a class can define two methods with the same name as long as the return types are different.</line>
    <answer>false</answer>
    <explanation>An overloaded method can change the return type, but return type alone is not enough -- it also must change the argument list.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="102">
    <line>an overriding method of a subclass can throw an Arithmetic Exception not thrown by the original (overriden) method.</line>
    <answer>true</answer>
    <explanation>ArithmeticException is a RuntimeException, so the rules about overriding methods not throwing new or broader exceptions does not apply.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="103">
    <line>overloaded methods must not throw new checked exceptions not thrown by the original method.</line>
    <answer>false</answer>
    <explanation>This would be true for overriding methods, but overloaded methods are free to throw new checked exceptions.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="104">
    <line>an inner class has free access to ALL member data of its enclosing (outer) class.</line>
    <answer>true</answer>
    <explanation>An inner class has a special relationship and can see even private member data of the outer class. </explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="105">
    <line>It is not possible to have an instance of a non-static inner class before any instances of the outer class have been created.</line>
    <answer>true</answer>
    <explanation>A non-static inner class can ONLY exist as part of a relationship with an instance of its enclosing class.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="106">
    <line>this is a legal way to create an instance of an inner class: OuterClass.InnerClass x = new OuterClass().new InnerClass();</line>
    <answer>true</answer>
    <explanation>It may look strange, but that syntax is how you can create an instance of an inner class when you don't yet have an instance of the outer class. An inner class must ALWAYS be tied to an instance of the outer class.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="107">
    <line>a static inner class (considered a top-level nested class) can NOT access non-static variables of the outer class.</line>
    <answer>true</answer>
    <explanation>A static inner class is treated as a top-level nested class, and has no reference -- no special relationship -- to any instance of the enclosing class.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="108">
    <line>Class B extends A. B overrides the doStuff() method inherited from A. Which methods gets called if you say: A myA = new B(); myA.doStuff();</line>
    <answer>doStuff() in B</answer>
    <explanation>Java's polymorphism causes instance methods to be bound (chosen) at runtime, not during compilation. At runtime, the overriding method of the 'actual' object (which is a B) is dynamically chosen, even though the reference type is A.</explanation>
    <bogusanswer>doStuff() in A</bogusanswer>
    <bogusanswer>compiler error </bogusanswer>
    <bogusanswer>runtime error </bogusanswer>
  </question>
 
  <question id="109">
    <line>Are you allowed to have more than one top-level (non-inner) class definition per source file?</line>
    <answer>Yes</answer>
    <explanation>You can have as many top-level classes in one source file as you like, but only one can be declared public and the source file name must match the public class name.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="110">
    <line>Are you required to have a public class in each source file?</line>
    <answer>No</answer>
    <explanation>You don't have to have any public classes in your source files.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="111">
    <line>Assume the bit value of byte x is 00101011. What is the result of x>>2;</line>
    <answer>00001010</answer>
    <explanation>The right shift operator fills from the left with the value of the sign bit (in this case, 0).</explanation>
    <bogusanswer>11001010</bogusanswer>
    <bogusanswer>10101100</bogusanswer>
  </question>
  
  <question id="112">
    <line>Assume the bit pattern of byte x is:  10110001. What will the sign of x be after x>>2;</line>
    <answer>negative</answer>
    <explanation>The right-shift operator fills all bits from the left with the same value as the original sign bit -- in this case 1.</explanation>
    <bogusanswer>positive</bogusanswer>
  </question>
  
  <question id="113">
    <line>What does the unsigned right-shift operator do to a negative number?</line>
    <answer>change its sign</answer>
    <explanation>The unsigned right-shift operator always fills from the left with zero, so the result will always become positive.</explanation>
    <bogusanswer>keep it negative</bogusanswer>
  </question>
  
  <question id="114">
    <line>What is the result of 00110011 &amp; 11110000 ?</line>
    <answer>00110000</answer>
    <explanation>The bitwise AND operator, &amp;, will result in bit values of 1 only if BOTH bits being compared are 1.</explanation>
    <bogusanswer>11110011</bogusanswer>
    <bogusanswer>00000000</bogusanswer>
  </question>
  
  <question id="115">
    <line>the short-circuit operators work on boolean types, not bits.</line>
    <answer>true</answer>
    <explanation>Short-circuit operators work only on logical expressions; they make boolean tests more efficient by not testing both operands unless its necessary. </explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="116">
    <line>Methods which have no access modifier can only be accessed by classes within the same package. TRUE or FALSE:</line>
    <answer>true</answer>
    <explanation>No access modifier means &quot;default&quot; which means &quot;only to others in the package&quot;.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="117">
    <line>methods which are marked protected can be accessed only by classes within the same package.</line>
    <answer>false</answer>
    <explanation>Protected means &quot;any subclass, regardless of package, can inherit these members -- methods or data&quot;</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="118">
    <line>Can a public method be overriden by a protected method?</line>
    <answer>No</answer>
    <explanation>Overriding methods can NOT be more restrictive than the original method. </explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="119">
    <line>methods and variables can be declared as final, but a class can not be declared final.</line>
    <answer>false</answer>
    <explanation>A final class is a class which can NOT be subclassed. Its the end-of-the-line in the inheritance tree.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="120">
    <line>Can a final class be subclassed?</line>
    <answer>No</answer>
    <explanation>A final class means the end-of-the-line for the inhertance tree. No subclasses can be made from a final class, only instances.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="121">
    <line>Can you have a final abstract class?</line>
    <answer>No</answer>
    <explanation>final and abstract are opposites: a final class can NOT be subclassed, while an abstract class MUST be subclassed... so final abstract makes no sense.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="122">
    <line>Can a member variable be declared abstract?</line>
    <answer>No</answer>
    <explanation>Only methods and classes can be declared abstract.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="123">
    <line>Can an abstract method be overridden?</line>
    <answer>Yes</answer>
    <explanation>An abstract method has no choice! It MUST be overridden.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="124">
    <line>Can a class be declared native?</line>
    <answer>No</answer>
    <explanation>Methods can be native, not classes or variables.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="125">
    <line>Can a member variable be declared native?</line>
    <answer>No</answer>
    <explanation>Only methods can be native.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="126">
    <line>Can a method be declared transient?</line>
    <answer>No</answer>
    <explanation>The transient modifier is used to keep variable data from being saved if an object is &quot;serialized&quot; (saved as an object ). Since methods don't change their state for an individual object, methods are never serialized, so transient is not needed for methods.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="127">
    <line>Can a class be declared transient?</line>
    <answer>No</answer>
    <explanation>Only data members can be declared as transient... meaning their value won't be saved when an object is &quot;serialized&quot; (saved).</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="128">
    <line>Can a member variable be declared synchronized?</line>
    <answer>No</answer>
    <explanation>The word synchronized marks method code so that it can't be run by more than one thread at a time. </explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="129">
    <line>Can a class be declared synchronized?</line>
    <answer>No</answer>
    <explanation>The keyword synchronized marks method code so that only one thread can access it at a time.</explanation>
    <bogusanswer>Yes</bogusanswer>
  </question>
  
  <question id="130">
    <line>Can a method be declared synchronized?</line>
    <answer>Yes</answer>
    <explanation>The synchronized modifier means that only one thread at a time can run the synchronized code.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="131">
    <line>What happens when you compile this:  byte a=2; a++; </line>
    <answer>Compiles and runs</answer>
    <explanation>The ++operator increments a as a byte, so there's no problem. You WOULD have a problem with a = a + 1; because the a + 1 would result in an int which would then have to be cast back to the byte variable a.</explanation>
    <bogusanswer>Won't compile without cast</bogusanswer>
    <bogusanswer>Runtime error</bogusanswer>
  </question>
  
  <question id="132">
    <line>Class Y is a subclass of class X.  Will this compile? X myX = new X(); Y myY = (Y) myX;</line>
    <answer>Yes</answer>
    <explanation>It will compile, because the myX object might really have been a Y object all along (as far as the compiler knows), but at runtime it will fail.</explanation>
    <bogusanswer>No</bogusanswer>
  </question>
  
  <question id="133">
    <line>In a switch statement, the argument to the switch ( ) can be a variable as long as it is compatible with an int.</line>
    <answer>true</answer>
    <explanation>The switch can obviously hold a variable, because that's what you're testing! But the variable cannot be anything except an int-compatible primitive.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="134">
    <line>in a switch construct, the default statement will always execute.</line>
    <answer>false</answer>
    <explanation>The default statement will only execute if no matching case is found, OR if there is &quot;fall through&quot; from a case statement executed above.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="135">
    <line>in a switch construct, the default statement will execute if no case values match the switch() argument.</line>
    <answer>true</answer>
    <explanation>If there are no matching cases found, the default will run.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="136">
    <line>in a try/catch construct, the catch blocks may be written in any order and the most narrow/specific catch argument is chosen at runtime.</line>
    <answer>false</answer>
    <explanation>The catch blocks must be written in order from most specific to least (e.g. IOException comes above Exception) or the compiler will complain.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="137">
    <line>a finally block will only be executed if the exception is not caught.</line>
    <answer>false</answer>
    <explanation>The finally block will ALWAYS run... unless the try/catch calls System.exit().</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="138">
    <line>a MalformedURLException is a checked exception.</line>
    <answer>true</answer>
    <explanation>A  MalformedURLException is checked by the compiler to be sure that the &quot;handle or declare&quot; rule is followed.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="139">
    <line>an ArithmeticException is a checked exception.</line>
    <answer>false</answer>
    <explanation>ArithmeticExceptions are considered programmer problems, and are NOT checked by the compiler. A divide-by-zero error, for example, should be checked for by your own code.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="140">
    <line>if a try/catch construct calls System.exit(), the finally block will execute first.</line>
    <answer>false</answer>
    <explanation>System.exit() means stop the program now! Even a finally block can't stop that from happening.</explanation>
    <bogusanswer>true</bogusanswer>
  </question>
  
  <question id="141">
    <line>an overriding method can change the access modifier from default to protected.</line>
    <answer>true</answer>
    <explanation>Since the protected modifier is less restrictive than default, an overriding method is allowed to make that change.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="142">
    <line>To invoke an overriden method from the method which overrides it, use:</line>
    <answer>super.theMethod();</answer>
    <explanation>The super keyword is like a reference to the superclass.</explanation>
    <bogusanswer>this.theMethod();</bogusanswer>
  </question>
  
  <question id="143">
    <line>constructors are not inherited.</line>
    <answer>true</answer>
    <explanation>Constructors are different from methods in several ways, and inheritance is one of them. Subclasses do NOT get a constructor from the superclass.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>
  
  <question id="144">
    <line>It is legal to declare an array with an interface as the type (instead of a class or primitive) -- example: PetInterface []p;</line>
    <answer>true</answer>
    <explanation>One of the best uses of polymorphism in Java is to declare an array using an interface type, then add objects which can be from any class, as long as they implement the interface type of the array.</explanation>
    <bogusanswer>false</bogusanswer>
  </question>

</roundup>
