package cn.tuyucheng.taketoday.commanddesign.command

import cn.tuyucheng.taketoday.commanddesign.Clipboard
import cn.tuyucheng.taketoday.commanddesign.receiver.TextEditor

class CutCommand(private val receiver: TextEditor, private val clipboard: Clipboard) : Command {
   override fun execute() {
      clipboard.content = receiver.cut()
   }

   override fun undo() {
      receiver.write(clipboard.content)
      clipboard.content = ""
   }
}