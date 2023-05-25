package cn.tuyucheng.taketoday.commanddesign.command

import cn.tuyucheng.taketoday.commanddesign.Clipboard
import cn.tuyucheng.taketoday.commanddesign.receiver.TextEditor

class CopyCommand(private val receiver: TextEditor, private val clipboard: Clipboard) : Command {
   override fun execute() {
      clipboard.content = receiver.copy()
   }

   override fun undo() {
      clipboard.content = ""
   }
}