package com.example.refactorer

import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory

class RefactoringToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Create a content object and set the component for the tool window
        val content = ContentFactory.SERVICE.getInstance().createContent(ScrollableContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}