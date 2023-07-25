package com.example.refactorer

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.wm.ToolWindowManager
import kotlinx.coroutines.*
import javax.swing.JPanel
import javax.swing.JTextArea

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

            e.getData(CommonDataKeys.EDITOR)?.selectionModel?.selectedText
                ?.takeIf { it.isNotBlank() }
                ?.let { text ->
                    refactorJob = scope.launch {
                        val suggestions = withContext(Dispatchers.IO) { getRefactoringSuggestion(text) }

                        toolWindow?.contentManager?.getContent(0)?.component?.let { panel ->
                            if (panel is JPanel) {
                                val codeArea = panel.components.firstOrNull { it is JTextArea } as JTextArea?
                                codeArea?.text = suggestions.firstOrNull()?.message?.content ?: "Can't suggest anything!"
                            }
                        }
                    }
                }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

}
