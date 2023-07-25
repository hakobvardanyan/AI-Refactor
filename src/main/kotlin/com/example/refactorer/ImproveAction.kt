package com.example.refactorer

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import kotlinx.coroutines.*

class ImproveAction : AnAction() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var refactorJob: Job? = null

    override fun update(e: AnActionEvent) {
        super.update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        refactorJob?.cancel()

        e.project?.let { project ->
            e.getData(CommonDataKeys.EDITOR)?.selectionModel?.let { selectionModel ->
                selectionModel.selectedText?.takeIf { it.isNotBlank() }?.let { selectedText ->
                    refactorJob = scope.launch {
                        val suggestions =
                            withContext(Dispatchers.IO) { requestCodeImprovement(selectedText) }
                        suggestions.firstOrNull()?.message?.content?.let {
                            WriteCommandAction.runWriteCommandAction(project) {
                                e.getData(CommonDataKeys.EDITOR)?.document?.replaceString(
                                    selectionModel.selectionStart,
                                    selectionModel.selectionEnd,
                                    it
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

}
