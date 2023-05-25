package cn.tuyucheng.taketoday.commanddesign

import cn.tuyucheng.taketoday.commanddesign.command.CopyCommand
import cn.tuyucheng.taketoday.commanddesign.command.CutCommand
import cn.tuyucheng.taketoday.commanddesign.command.PasteCommand
import cn.tuyucheng.taketoday.commanddesign.executor.TextEditorInvoker
import cn.tuyucheng.taketoday.commanddesign.receiver.TextEditor

fun main() {
   val clipboard = Clipboard()
   val editor = TextEditor("Tuyucheng")
   val invoker = TextEditorInvoker()

   invoker.executeCommand(CutCommand(editor, clipboard))
   invoker.executeCommand(CopyCommand(editor, clipboard))
   invoker.executeCommand(PasteCommand(editor, clipboard))

   editor.print() // prints "TuyuchengTuyuchen"

   invoker.undo() // undoes the cut command

   editor.print() // prints "TuyuchengTuyucheng"
}