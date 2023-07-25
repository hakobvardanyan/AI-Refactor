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
                    createOpenAiService().getRefactoringSuggestion(
                        APIRequest(messages = listOf(RequestMessage(content = "Pretend you are senior engineer, please suggest a good alternative for the following code without any fairy tales: \n$selectedText")))
                    )
                }
                Messages.showMessageDialog(
                    e.project,
                    suggestion.choices.firstOrNull()?.message?.content ?: "Can't suggest anything!",
                    "Suggested Refactoring",
                    Messages.getInformationIcon())
            }

        }

    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
