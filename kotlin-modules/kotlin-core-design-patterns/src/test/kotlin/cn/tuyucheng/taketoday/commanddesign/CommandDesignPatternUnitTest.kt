package cn.tuyucheng.taketoday.commanddesign

import cn.tuyucheng.taketoday.commanddesign.command.CopyCommand
import cn.tuyucheng.taketoday.commanddesign.command.CutCommand
import cn.tuyucheng.taketoday.commanddesign.command.PasteCommand
import cn.tuyucheng.taketoday.commanddesign.executor.TextEditorInvoker
import cn.tuyucheng.taketoday.commanddesign.receiver.TextEditor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CommandDesignPatternUnitTest {

   @Test
   fun `executes cut command on a text editor`() {
      // given
      val clipboard = Clipboard()
      val editor = TextEditor("Tuyucheng")
      val invoker = TextEditorInvoker()

      // when
      invoker.executeCommand(CutCommand(editor, clipboard))

      // then
      assertThat(editor.content).isEqualTo("Tuyuchen")
   }

   @Test
   fun `executes copy and paste command on a text editor`() {
      // given
      val clipboard = Clipboard()
      val editor = TextEditor("Tuyucheng")
      val invoker = TextEditorInvoker()

      // when
      invoker.executeCommand(CopyCommand(editor, clipboard))
      invoker.executeCommand(PasteCommand(editor, clipboard))

      // then
      assertThat(editor.content).isEqualTo("TuyuchengTuyucheng")
   }

   @Test
   fun `undoes paste command`() {
      // given
      val clipboard = Clipboard()
      val editor = TextEditor("Tuyucheng")
      val invoker = TextEditorInvoker()

      invoker.executeCommand(CopyCommand(editor, clipboard))
      invoker.executeCommand(PasteCommand(editor, clipboard))

      // when
      invoker.undo()

      // then
      assertThat(editor.content).isEqualTo("Tuyucheng")
   }
}