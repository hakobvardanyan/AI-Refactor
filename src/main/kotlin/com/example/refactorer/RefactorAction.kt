package com.example.refactorer

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.*

class RefactorAction : AnAction() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var refactorJob: Job? = null

    override fun update(e: AnActionEvent) {
        super.update(e)
    }

    override fun actionPerformed(e: AnActionEvent) {
        refactorJob?.cancel()

        val selectedText: String? = e.getData(CommonDataKeys.EDITOR)?.selectionModel?.selectedText

        if (!selectedText.isNullOrBlank()) {

            refactorJob = scope.launch {
                val suggestion = withContext(Dispatchers.IO) {
                    openAiService.getRefactoringSuggestion(
                        APIRequest(kotlinCode = "Pretend you are staff kotlin engineer and suggest refactoring on this code below \n$selectedText")
                    )
                }
                Messages.showMessageDialog(
                    e.project,
                    suggestion.choices.joinToString(" "),
                    "Suggested Refactoring",
                    Messages.getInformationIcon());
            }

        }

    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
