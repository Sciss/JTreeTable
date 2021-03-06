package de.sciss.treetable

import java.util

import de.sciss.submin.Submin
import de.sciss.treetable.TreeTable.Path
import javax.swing.plaf.ColorUIResource
import javax.swing.tree.TreeNode
import javax.swing.{JSeparator, JTree, UIManager}

import scala.annotation.switch
import scala.swing._
import scala.util.control.NonFatal

object ScalaDemo extends SimpleSwingApplication {
  override def startup(args: Array[String]): Unit = {
    try {
      val isDark = args.contains("--dark")
      Submin.install(isDark)
      if (isDark) {
        UIManager.put("Table.background", new ColorUIResource( 29,  32,  36))
        UIManager.put("Table.foreground", new ColorUIResource(220, 220, 220))
      }
//      // val lafName = "javax.swing.plaf.nimbus.NimbusLookAndFeel"
//      // val lafName = "com.alee.laf.WebLookAndFeel"
//      val lafName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
//      UIManager.installLookAndFeel("Web Look And Feel", lafName)
//      UIManager.setLookAndFeel(lafName)
    } catch {
      case NonFatal(_) =>
    }

    super.startup(args)
  }

  sealed trait Node extends javax.swing.tree.TreeNode {
    def key   : String
    def value : String

    def getParent: TreeNode = throw new UnsupportedOperationException
  }
  case class Branch(key: String, value: String, childNodes: Seq[Node]) extends Node {
    def getChildAt(childIndex: Int): TreeNode = childNodes(childIndex)

    def getChildCount: Int = childNodes.size

    def getIndex(node: TreeNode): Int = childNodes.indexOf(node)

    def getAllowsChildren: Boolean = true

    def isLeaf: Boolean = false

    def children(): util.Enumeration[_ <: TreeNode] = {
      import scala.collection.JavaConverters._
      childNodes.iterator.asJavaEnumeration
    }
  }
  case class Leaf(key: String, value: String) extends Node {
    def getChildAt(childIndex: Int): TreeNode = throw new UnsupportedOperationException

    def getChildCount: Int = 0

    def getIndex(node: TreeNode): Int = throw new UnsupportedOperationException

    def getAllowsChildren: Boolean = false

    def isLeaf: Boolean = true

    def children(): util.Enumeration[_ <: TreeNode] = {
      import scala.collection.JavaConverters._
      Nil.iterator.asJavaEnumeration
    }
  }

  lazy val top: Frame = {
    val tm: TreeModel[Node] = new TreeModel[Node] {
      def getIndexOfChild(parent: Node, child: Node): Int = parent match {
        case Branch(_, _, ch) => ch.indexOf(child)
        case _ => -1
      }

      def valueForPathChanged(path: Path[Node], newValue: Node): Unit = ()

      def isLeaf(node: Node): Boolean = node.isInstanceOf[Leaf]

      def getChild(parent: Node, index: Int): Node = parent match {
        case Branch(_, _, ch) => ch(index)
        case _ => throw new IllegalArgumentException(parent.toString)
      }

      def getChildCount(parent: Node): Int = parent match {
        case Branch(_, _, ch) => ch.size
        case _ => 0
      }

      lazy val root: Node = Branch("Root", "", Seq(
        Branch("Foo", "Bar", Seq(
          Leaf("Tree", "Table"),
          Leaf("Model", "Event")
        )),
        Branch("Empty", "Baz", Nil),
        Leaf("Leaf", "Blah")
      ))
    }

    val tcm = new TreeColumnModel[Node] {
      def hierarchicalColumn: Int = 0

      def isCellEditable(node: Node, column: Int): Boolean = false

      def setValueAt(value: Any, node: Node, column: Int): Unit = ()

      def getValueAt(node: Node, column: Int): Any = (column: @switch) match {
        case 0 => node.key
        case 1 => node.value
      }

      def columnCount: Int = 2

      def getColumnClass(column: Int): Class[_] = classOf[String]

      def getColumnName(column: Int): String = (column: @switch) match {
        case 0 => "Key"
        case 1 => "Value"
      }
    }

    new MainFrame {
      title = "TreeTable"
      contents = new BoxPanel(Orientation.Vertical) {
        private val sep1 = Component.wrap(new JSeparator)
        sep1.border = Swing.EmptyBorder(8, 0, 8, 0)
        private val sep2 = Component.wrap(new JSeparator)
        sep2.border = Swing.EmptyBorder(8, 0, 8, 0)
        contents ++= Seq(
          new TreeTable[Node, TreeColumnModel[Node]](tm, tcm),
          sep1,
          Component.wrap(new JTree(Array[AnyRef]("Foo", "Bar", "Baz"))),
          sep2,
          new Table(Array(Array[Any]("Foo", "Bar"), Array[Any]("Baz", "Blah")), Seq("Key", "Value"))
        )
      }
      pack().centerOnScreen()
      open()
    }
  }
}