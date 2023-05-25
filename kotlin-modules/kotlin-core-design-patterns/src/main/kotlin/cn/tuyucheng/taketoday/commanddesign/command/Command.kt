package cn.tuyucheng.taketoday.commanddesign.command

interface Command {
   fun execute()
   fun undo()
}