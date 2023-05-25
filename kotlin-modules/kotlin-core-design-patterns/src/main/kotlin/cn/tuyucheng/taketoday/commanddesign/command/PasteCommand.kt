package cn.tuyucheng.taketoday.commanddesign.command

import cn.tuyucheng.taketoday.commanddesign.Clipboard
import cn.tuyucheng.taketoday.commanddesign.receiver.TextEditor

class PasteCommand(private val receiver: TextEditor, private val clipboard: Clipboard) : Command {
   override fun execute() {
      receiver.write(clipboard.content)
   }

   override fun undo() {
      receiver.delete(clipboard.content)
   }
}