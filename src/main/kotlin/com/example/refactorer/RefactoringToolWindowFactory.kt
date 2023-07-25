package com.example.refactorer

import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import javax.swing.JPanel
import javax.swing.JTextArea

class RefactoringToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // Create your custom UI components or load your code here
        val panel = JPanel()
        val codeArea = JTextArea("Your code goes here...")
        panel.add(codeArea)

        // Create a content object and set the component for the tool window
        val content = ContentFactory.SERVICE.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}