package ast;

import java.util.List;
/** A node in the abstract syntax tree of a program. */
public interface Node extends Cloneable{

   /**
    * The number of nodes in the AST rooted at this node, including this node
    *
    * @return The size of the AST rooted at this node
    */
   int size();

   /**
    * Returns the node at {@code index} in the AST rooted at this node. Indices
    * are defined such that:<br>
    * 1. Indices are in the range {@code [0, size())}<br>
    * 2. {@code this.nodeAt(0) == this} for all nodes in the AST <br>
    * 3. All nodes in the AST rooted at {@code this} must be reachable by a call
    * to {@code
    * this.nodeAt(i)} with an appropriate index {@code i}
    *
    * @param index
    *           The index of the node to retrieve
    * @return The node at {@code index}
    * @throws IndexOutOfBoundsException
    *            if {@code index} is not in the range of valid indices
    */
   Node nodeAt(int index);

   /**
    * Appends the program represented by this node prettily to the given
    * StringBuilder.
    *
    * <p>
    * The output of this method must be consistent with both the critter grammar
    * and itself; that is:<br>
    * 1. It must be possible to put the result of this method into a valid
    * critter program<br>
    * 2. Placing the result of this method into a valid critter program then
    * parsing the program must yield an AST which contains a subtree identical
    * to the one rooted at {@code this}
    *
    * @param sb
    *           The {@code StringBuilder} to which the program will be appended
    * @return The {@code StringBuilder} to which this program was appended
    */
   StringBuilder prettyPrint(StringBuilder sb);

   /**
    * Returns the pretty-print of the abstract syntax subtree rooted at this
    * {@code Node}.
    *
    * <p>
    * This method returns the same result as {@code prettyPrint(...).toString()}
    *
    * @return The pretty-print of the AST rooted at this {@code Node}.
    */
   @Override
   String toString();

   /**
    * Gets the children of this {@code Node}.
    *
    * @return A list of the children this {@code Node}.
    */
   List<Node> getChildren();

   /**
    * Gets the parent of this {@code Node}. Null is returned if this
    * {@code Node} is the root.
    *
    * @return the parent of this {@code Node}, or {@code null} if it is the
    *         root.
    */
   Node getParent();
   void setParent(Node n);

   /**
    * Adds to the children of this node
    * 
    * @param c
    *           - adds c to children
    */
//   void addChildren(Node c);

//   /**
//    * flattens the tree out to a long arraylist
//    * 
//    * @return a flattened version of the tree with this node as the root object
//    */
//   public ArrayList<Node> getFlattened();

   /**
    * Implements visitor design pattern
    * 
    * @param visitor
    *           - accepts a type of visitor to visit() to
    */
//   public void accept(Visitor visitor);

   /**
    * returns the Node in the children arraylist at index
    * 
    * @param index
    *           - the nth element being get in children
    * @return - node in the children arraylist at index
    */
   public Node getChild(int index);
   
   /**
    * returns a cloned version of this Node
    */
}