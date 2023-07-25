package com.example.refactorer

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import kotlinx.coroutines.*

class RefactorAction : AnAction() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var refactorJob: Job? = null

    override fun update(e: AnActionEvent) {
        super.update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        refactorJob?.cancel()

        e.project?.let { project ->
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Refactoring Assistance")
            toolWindow?.show(null) // Pass the anchor or null to show as floating window
            val content = getContent(toolWindow)

            getSelectedText(e)?.let { text ->
                refactorJob = scope.launch {
                    content.setText("Requesting assistance from ChatGPT...")
                    val suggestions = withContext(Dispatchers.IO) { getRefactoringSuggestion(text) }

                    content.setText(
                        suggestions.firstOrNull()?.message?.content ?: "Can't suggest anything!"
                    )
                }
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    private fun getContent(toolWindow: ToolWindow?) = toolWindow?.contentManager?.contents
        ?.find {
            it.component is ScrollableContent
        }?.component as ScrollableContent

    private fun getSelectedText(e: AnActionEvent) = e.getData(CommonDataKeys.EDITOR)
        ?.selectionModel
        ?.selectedText
        ?.takeIf {
            it.isNotBlank()
        }

}
