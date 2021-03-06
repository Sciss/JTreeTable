/*
 * TreeModel.scala
 * (TreeTable)
 *
 * Copyright (c) 2013-2020 Hanns Holger Rutz. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *
 * For further information, please contact Hanns Holger Rutz at
 * contact@sciss.de
 */

package de.sciss.treetable

import scala.swing.Publisher
import scala.swing.event.Event

//object TreeModel {
//  def wrap[A](peer: jtree.TreeModel): TreeModel[A] = {
//    val _peer = peer
//    new TreeModel[A] {
//      val peer = _peer
//    }
//  }
//}
trait TreeModel[A] extends Publisher {
  def root: A

  def getChildCount(parent: A): Int
  def getChild(parent: A, index: Int): A
  def isLeaf(node: A): Boolean

  // val peer: jtree.TreeModel

  def valueForPathChanged(path: TreeTable.Path[A], newValue: A): Unit

  def getIndexOfChild(parent: A, child: A): Int

  // final case class NodesChanged(parentPath: TreeTable.Path[A], children: (Int, A)*) extends Event
}